package org.tensorflow.demo.userinterface;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.R;
import org.tensorflow.demo.SharedPrefs.SharedPrefManager;
import org.tensorflow.demo.models.User;
import org.tensorflow.demo.urls.URLs;
import org.tensorflow.demo.volley.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends Activity {

    EditText firstName, lastName, email, phoneNo;
    ImageView imageViewProfile;
    Button btnUpdateProfile;
    FancyAlertDialog fad;
    ProgressBar progressBar;

    String ID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = findViewById(R.id.edit_text_profile_first_name);
        lastName = findViewById(R.id.edit_text_profile_last_name);
        phoneNo = findViewById(R.id.edit_text_profile_phoneno);
        email = findViewById(R.id.edit_text_profile_email);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());

        firstName.setText(sharedPrefManager.getUser().getFirstName());
        lastName.setText(sharedPrefManager.getUser().getLastName());
        phoneNo.setText(String.valueOf(sharedPrefManager.getUser().getPhoneNo()));
        email.setText(sharedPrefManager.getUser().getEmail());

        imageViewProfile = findViewById(R.id.image_view_profile_farm_doctor);

        progressBar = findViewById(R.id.progress_bar_profile);

        int i = sharedPrefManager.getUser().getUserID();
        ID = String.valueOf(i);
        //Toast.makeText(getApplicationContext(), "User ID = " +  String.valueOf(i), Toast.LENGTH_LONG).show();



        btnUpdateProfile = findViewById(R.id.btn_update_profile);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fad = new FancyAlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Update")
                        .setBackgroundColor(Color.parseColor("#CDDC39"))  //Don't pass R.color.colorvalue
                        .setMessage("Do you really want to change your details ?")
                        .setNegativeBtnText("Cancel")
                        .setPositiveBtnBackground(Color.parseColor("#FF4081"))  //Don't pass R.color.colorvalue
                        .setPositiveBtnText("Proceed")
                        .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                        .setAnimation(Animation.SLIDE)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_star_border_black_24dp, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                updateProfile();
//                                SharedPrefManager sharedPrefManager = new SharedPrefManager(getApplicationContext());
//                                int i = sharedPrefManager.getUser().getUserID();
//                                Toast.makeText(getApplicationContext(), "User ID = " + String.valueOf(i), Toast.LENGTH_LONG).show();
                            }
                        })
                        .OnNegativeClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                Snackbar.make(getCurrentFocus(), "Update operation cancelled.", Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .build();
            }
        });
    }

    private void updateProfile() {

        //Toast.makeText(getApplicationContext(), ID, Toast.LENGTH_LONG).show();
        final String phneNo = phoneNo.getText().toString().trim();
        final String frstName = firstName.getText().toString().trim();
        final String lstName = lastName.getText().toString().trim();
        final String e_mail = email.getText().toString().trim();

        if(!TextUtils.isEmpty(frstName)&&!TextUtils.isEmpty(lstName)&&!TextUtils.isEmpty(e_mail)&&
                !TextUtils.isEmpty(phneNo)&&!TextUtils.isEmpty(ID)) {

            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    URLs.URL_UPDATE_PROFILE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();

                    try {
                      //  Toast.makeText(getApplicationContext(), "Execution fikas here", Toast.LENGTH_SHORT).show();
                        //converting response to json obj
                        JSONObject obj = new JSONObject(response);

                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();

                            //getting the user from the response
                            JSONObject userJson = obj.getJSONObject("user");

                            //creating a new user object
                            User user = new User(
                                    userJson.getInt("userID"),
                                    userJson.getString("firstName"),
                                    userJson.getString("lastName"),
                                    userJson.getInt("phoneNumber"),
                                    userJson.getString("email")
                            );

                            //storing the user in shared preferences
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                            progressBar.setVisibility(View.GONE);

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("firstName", frstName);
                    params.put("lastName", lstName);
                    params.put("email", e_mail);
                    params.put("phoneNo", phneNo);
                    params.put("userID", ID);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Snackbar.make(getCurrentFocus(), "Please fill in the necessary details.",Snackbar.LENGTH_LONG).show();
        }
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
