package org.tensorflow.demo.userinterface;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.ImageRecActivity;
import org.tensorflow.demo.R;
import org.tensorflow.demo.SharedPrefs.SharedPrefManager;
import org.tensorflow.demo.urls.URLs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddNoticeActivity extends Activity {

    ImageView imageViewAddNotice;
    Button btnSendNotice;
    Button btnAddImage;

    EditText editTextNotice;

    Bitmap bitmap;

    ProgressDialog progressDialog;

    SharedPrefManager sharedPrefManager;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        imageViewAddNotice = findViewById(R.id.image_view_add_notice_);

        editTextNotice = findViewById(R.id.edit_text_add_notice);

        progressDialog = new ProgressDialog(this);

        sharedPrefManager = new SharedPrefManager(this);

        btnAddImage = findViewById(R.id.btn_notice_activity_add_image);
        btnSendNotice = findViewById(R.id.btn_notice_activity_send_notice);
        //checking the permission
        //if the permission is not given we will open setting to add permission
        //else app will not open
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FancyAlertDialog.Builder(AddNoticeActivity.this)
                        .setTitle("Update")
                        .setBackgroundColor(Color.parseColor("#CDDC39"))  //Don't pass R.color.colorvalue
                        .setMessage("Please Select One")
                        .setNegativeBtnText("Camera")
                        .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                        .setPositiveBtnText("Gallery")
                        .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                        .setAnimation(Animation.SLIDE)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, 100);
                            }
                        })
                        .OnNegativeClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                Toast.makeText(getApplicationContext(), "You have selected camera option", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
            }
        });

        btnSendNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling the method uploadBitmap to upload image
                uploadBitmap(bitmap);
//                startActivity(new Intent(AddNoticeActivity.this, LibraryActivity.class));
            }
        });

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(AddNoticeActivity.this, HomeActivity.class));
                        break;
                    case R.id.action_library:
                        startActivity(new Intent(AddNoticeActivity.this, LibraryActivity.class));
                        break;
                    case R.id.action_camera:
                        startActivity(new Intent(AddNoticeActivity.this, ImageRecActivity.class));
                        break;
                    case R.id.action_maps:
                        startActivity(new Intent(AddNoticeActivity.this, MapsActivity.class));
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent(AddNoticeActivity.this, ProfileActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                //displaying selected image to imageview
                imageViewAddNotice.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * The method is taking Bitmap as an argument
    * then it will return the byte[] array for the given bitmap
    * and we will send this array to the server
    * here we are using PNG Compression with 80% quality
    * you can give quality between 0 to 100
    * 0 means worse quality
    * 100 means best quality
    * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {

        progressDialog.setMessage("Uploading Image");
        progressDialog.show();

        //getting the tag from the edittext
        final String tags = editTextNotice.getText().toString().trim();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLs.URL_UPLOAD_NOTICES,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.i("Response: ", new String(response.data));
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.i("Message: ", obj.getString("message"));
                            Log.i("First Name: ", obj.getString("firstName"));
                            Log.i("Last Name: ", obj.getString("lastName"));
                            Log.i("userID: ", obj.getString("userID"));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("notice", tags);
                params.put("firstName", sharedPrefManager.getUser().getFirstName());
                params.put("lastName", sharedPrefManager.getUser().getLastName());
                params.put("userID", String.valueOf(sharedPrefManager.getUser().getUserID()));
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(this, "Settings Activity Loading", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_logout:
                SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
                sharedPrefManager.logOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
