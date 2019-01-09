package org.tensorflow.demo;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.urls.URLs;
import org.tensorflow.demo.volley.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //Latitude & Longitude
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;

    final ArrayList<HashMap<String, String>> location = new ArrayList<>();
    final HashMap<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URLs.URL_RETRIEVE_STORE_LOCATIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray data = new JSONArray(response);

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);

                        map.put("LocationID", c.getString("storeID"));
                        map.put("Latitude", c.getString("lat"));
                        map.put("Longitude", c.getString("long"));
                        map.put("LocationName", c.getString("storeName"));
                        location.add(map);

                        Latitude = Double.parseDouble(location.get(0).get("Latitude").toString());
                        Longitude = Double.parseDouble(location.get(0).get("Longitude").toString());

                        Latitude = Double.parseDouble(location.get(i).get("Latitude").toString());
                        Longitude = Double.parseDouble(location.get(i).get("Longitude").toString());
                        String name = location.get(i).get("LocationName").toString();

                        MarkerOptions markerOptions = new MarkerOptions().position(
                                new LatLng(Latitude, Longitude)).title(name);
                        mMap.addMarker(markerOptions);
                    }

                    LatLng coordinate = new LatLng(Latitude, Longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 12));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}