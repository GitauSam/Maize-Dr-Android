package org.tensorflow.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.tensorflow.demo.userinterface.HomeActivity;
import org.tensorflow.demo.userinterface.LibraryActivity;
import org.tensorflow.demo.userinterface.MapsActivity;
import org.tensorflow.demo.userinterface.ProfileActivity;

import java.io.IOException;
import java.util.List;

public class ImageRecActivity extends Activity {

    final static int TAKE_PICTURE = 1;

    ImageView ivThumbnailPhoto;

    TextView tvResults;

    private Classifier classifier;

    BottomNavigationView bottomNavigationView;

    private String mCurrentPhotoPath;

    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/retrained_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/retrained_labels.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_rec);

        ivThumbnailPhoto = findViewById(R.id.ivThumbnail);
        tvResults = findViewById(R.id.tvResults);

        new FancyAlertDialog.Builder(ImageRecActivity.this)
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
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, TAKE_PICTURE);
                    }
                })
                .build();


        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(ImageRecActivity.this, HomeActivity.class));
                        break;
                    case R.id.action_library:
                        startActivity(new Intent(ImageRecActivity.this, LibraryActivity.class));
                        break;
                    case R.id.action_camera:
                        break;
                    case R.id.action_maps:
                        startActivity(new Intent(ImageRecActivity.this, GoogleMapsActivity.class));
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent(ImageRecActivity.this, ProfileActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK && intent.hasExtra("data")) {
                        Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                        if (bitmap != null) {
                            ivThumbnailPhoto.setImageBitmap(bitmap);
                            classifier = TensorFlowImageClassifier.create(
                                    getAssets(),
                                    MODEL_FILE,
                                    LABEL_FILE,
                                    INPUT_SIZE,
                                    IMAGE_MEAN,
                                    IMAGE_STD,
                                    INPUT_NAME,
                                    OUTPUT_NAME);
                            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                            Log.i("Results", results.toString());
                            Toast.makeText(getApplicationContext(), "Results" + results.toString(), Toast.LENGTH_LONG).show();

                            tvResults.setText(results.toString());
                        }

                    }
                    break;
                case 100:

                    if (requestCode == 100 && resultCode == RESULT_OK && intent != null) {

                        //getting the image Uri
                        Uri imageUri = intent.getData();
                        try {

                            //Toast.makeText(getApplicationContext(), "Execution fikas here", Toast.LENGTH_SHORT).show();
                            //getting bitmap object from uri
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

//                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,300,  300, true);
                            classifier = TensorFlowImageClassifier.create(
                                    getAssets(),
                                    MODEL_FILE,
                                    LABEL_FILE,
                                    INPUT_SIZE,
                                    IMAGE_MEAN,
                                    IMAGE_STD,
                                    INPUT_NAME,
                                    OUTPUT_NAME);
                            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                            Log.i("Results", results.toString());
                            Toast.makeText(getApplicationContext(), "Results" + results.toString(), Toast.LENGTH_LONG).show();
//
                            tvResults.setText(results.toString());

                            //displaying selected image to imageview
                            ivThumbnailPhoto.setImageBitmap(bitmap);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
            }

    } catch (Exception error) {
            error.printStackTrace();
        }
    }



}
