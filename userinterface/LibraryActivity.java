package org.tensorflow.demo.userinterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.GoogleMapsActivity;
import org.tensorflow.demo.ImageRecActivity;
import org.tensorflow.demo.R;
import org.tensorflow.demo.SharedPrefs.SharedPrefManager;
import org.tensorflow.demo.models.Diseases;
import org.tensorflow.demo.urls.URLs;
import org.tensorflow.demo.userinterface.adapters.DiseasesAdapter;
import org.tensorflow.demo.volley.VolleySingleton;

import java.util.ArrayList;

public class LibraryActivity extends Activity {

    RecyclerView diseasesRecyclerview;

    ProgressBar diseasesProgressbar;

    ArrayList<Diseases> diseasesArrayList;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        diseasesProgressbar = findViewById(R.id.progress_bar_disease_library);
        diseasesRecyclerview = findViewById(R.id.recycler_view_fragment_disease_library);
        diseasesRecyclerview.setHasFixedSize(true);
        diseasesRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        diseasesArrayList = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(LibraryActivity.this, HomeActivity.class));
                        break;
                    case R.id.action_library:
                        break;
                    case R.id.action_camera:
                        startActivity(new Intent(LibraryActivity.this, ImageRecActivity.class));
                        break;
                    case R.id.action_maps:
                        startActivity(new Intent(LibraryActivity.this, GoogleMapsActivity.class));
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent(LibraryActivity.this, ProfileActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        addItems();

        //loadDiseases();

    }

    private void loadDiseases() {

        DiseasesAdapter diseasesAdapter = new DiseasesAdapter(getApplicationContext(), diseasesArrayList);

        diseasesProgressbar.setVisibility(View.GONE);

        diseasesRecyclerview.setAdapter(diseasesAdapter);
    }

    private void addItems() {

        //diseasesArrayList.add(new Diseases("JKUAT Agro-Store", "Located inside JKUAT in Juja", "imageURL"));

        //diseasesArrayList.add(new Diseases("Lys-Alyn", "Located in Nairobi next to Ambassador", "imageURL"));

        //diseasesArrayList.add(new Diseases("Agro-house", "Located in Nairobi opposite Afya Centre", "imageURL"));

        //diseasesArrayList.add(new Diseases("Syngenta", "Located in Kiambu along the Eastern Bypass", "imageURL"));

        //diseasesArrayList.add(new Diseases("KARI", "Located in Nairobi", "imageURL"));



        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URLs.URL_RETRIEVE_DISEASES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response)

            {
                try {
                    //converting json str to array obj
                    JSONArray array = new JSONArray(response);

                    //traversing through all the objs
                    for (int i = 0; i < array.length(); i++) {

                        //getting notice obj from json array
                        JSONObject diseases = array.getJSONObject(i);

                        //adding the notice to notices list
                        diseasesArrayList.add(new Diseases(
                                diseases.getString("diseaseName"),
                                diseases.getString("diseaseScientificName"),
                                diseases.getString("imageURL"),
                                diseases.getString("symptoms")
                        ));
                    }

                    diseasesProgressbar.setVisibility(View.INVISIBLE);
                    DiseasesAdapter diseasesAdapter = new DiseasesAdapter(LibraryActivity.this, diseasesArrayList);
                    diseasesRecyclerview.setAdapter(diseasesAdapter);

                } catch (JSONException e) {
                    diseasesProgressbar.setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                diseasesProgressbar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);


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
