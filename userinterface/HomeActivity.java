package org.tensorflow.demo.userinterface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.fabtransitionactivity.SheetLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.GoogleMapsActivity;
import org.tensorflow.demo.ImageRecActivity;
import org.tensorflow.demo.R;
import org.tensorflow.demo.SharedPrefs.SharedPrefManager;
import org.tensorflow.demo.models.UserNotice;
import org.tensorflow.demo.urls.URLs;
import org.tensorflow.demo.userinterface.adapters.UserNoticeAdapter;
import org.tensorflow.demo.volley.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends Activity implements SheetLayout.OnFabAnimationEndListener{

    @BindView(R.id.bottom_sheet) SheetLayout mSheetLayout;
//    @BindView(R.id.btn_home_add_notice)
//    Button addNotice;

    ProgressBar progressBar;

    Button addNotice;

    RecyclerView recyclerViewUserNotice;
    UserNoticeAdapter noticeAdapter;
    List<UserNotice> userNoticeList;

    SwipeRefreshLayout swipeRefreshLayout;

    BottomNavigationView bottomNavigationView;

    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);


        userNoticeList = new ArrayList<>();

        addNotice = findViewById(R.id.btn_home_add_notice);
        addNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AddNoticeActivity.class));
            }
        });

        recyclerViewUserNotice = findViewById(R.id.recycler_view_user_notice);
        recyclerViewUserNotice.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewUserNotice.setLayoutManager(layoutManager);


//        mSheetLayout.setFab(fabHome);
        mSheetLayout.setFabAnimationEndListener(this);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userNoticeList.clear();
                UserNoticeAdapter.userNoticeList.clear();
                notifyAdapter();
                retrieveNotices();

                Handler h = new Handler();
                h.postDelayed(r, 2000);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.lightblue, R.color.bluegrey, R.color.fungreen);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        break;
                    case R.id.action_library:
                        startActivity(new Intent(HomeActivity.this, LibraryActivity.class));
                        break;
                    case R.id.action_camera:
                        startActivity(new Intent(HomeActivity.this, ImageRecActivity.class));
                        break;
                    case R.id.action_maps:
                        startActivity(new Intent(HomeActivity.this, GoogleMapsActivity.class));
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        progressBar = findViewById(R.id.progress_bar_home);
        retrieveNotices();
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            swipeRefreshLayout.setRefreshing(false);
        }
    };

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

//    @OnClick(R.id.fab_home)
//    void onFabClick() {
//        mSheetLayout.expandFab();
//    }

    @Override
    public void onFabAnimationEnd() {
        Toast.makeText(getApplicationContext(), "Going to AddNoticeActivity in a few", Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(this, AddNoticeActivity.class);
        //startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mSheetLayout.contractFab();
        }
    }

    private void retrieveNotices() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URLs.URL_GET_NOTICES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response)

            {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //converting json str to array obj
                    JSONArray array = jsonObject.getJSONArray("images");

                    //traversing through all the objs
                    for (int i = 0; i < array.length(); i++) {

                        //getting notice obj from json array
                        JSONObject userNotice = array.getJSONObject(i);

                        //adding the notice to notices list
                        userNoticeList.add(new UserNotice(
                                userNotice.getString("firstName"),
                                userNotice.getString("lastName"),
                                userNotice.getString("tags"),
                                userNotice.getString("image")
                        ));
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                    noticeAdapter = new UserNoticeAdapter(HomeActivity.this, userNoticeList);
                    recyclerViewUserNotice.setAdapter(noticeAdapter);

                } catch (JSONException e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void notifyAdapter() {
        noticeAdapter.notifyDataSetChanged();
    }

    private void addItems() {

        userNoticeList.add(new UserNotice("Kid",
                "Dexter",
                "Sleep tight, America! Your fate lies safely in the hands of Dexter, a child genius who whips up dazzling, " +
                        "world-saving inventions in his secret laboratory. Big sister Dee Dee frequently wrecks his experiments, " +
                        "but his bigger nemesis is Mandark, his brilliant rival at Huber Elementary School. Mom and Dad, of course, " +
                        "have no idea what their little angel is up to.",
                R.drawable.ic_launcher));

        userNoticeList.add(new UserNotice("Inspector",
                "Gadget",
                "Inspector Gadget, a bumbling detective, needs lots of help and luck to solve cases assigned by short-tempered Chief Quimby. " +
                        "That help comes from his young, but smart-beyond-her-years niece, Penny, and faithful dog, Brain, who has a human IQ. " +
                        "The trio relies on high-tech items -- i.e. Penny's watch links to a video communicator in Brain's collar -- " +
                        "and other tools to elude trouble from Dr. Claw of MAD. Despite Gadget's shortcomings, he manages to solve each case. " +
                        "Don Adams -- who played Maxwell Smart in the 1960s series \"Get Smart\" -- voices Gadget.",
                R.drawable.ic_launcher));

        userNoticeList.add(new UserNotice("Ben",
                "10",
                "Ben Tennyson, a 10-year-old boy, discovers a magical device that can turn him into 10 different alien heroes, " +
                        "each with its own unique abilities. With this newfound power, " +
                        "Ben, Grandpa Max and cousin Gwen help others and stop evildoers - " +
                        "but that doesn't mean he doesn't cause some superpowered kid mischief once and a while.",
                R.drawable.ic_launcher));

        userNoticeList.add(new UserNotice("Billy",
                "Mandy",
                "Billy and Mandy are a mismatched pair of youngsters who befriend the Grim Reaper after winning a limbo contest against him. " +
                        "Since the Grim Reaper lost, he has to be their friend, and the three go on adventures together. " +
                        "Between school, family and visitors from the spirit world, " +
                        "Billy and Mandy sometimes need help to clean up the messes they get themselves into, and that's where Hos Delgado comes in. " +
                        "Hos and the Grim Reaper are not friends, and with Mandy's bossy nature and Billy's curious attitude funny escapades ensue.",
                R.drawable.ic_launcher));

        userNoticeList.add(new UserNotice("Juniper",
                "Lee",
                "The series centers on the life of a preteen girl named Juniper Lee, who lives in Orchid Bay City," +
                        "which is a city filled with magical monsters and demons, both good and bad. " +
                        "Juniper has recently been made the new Te Xuan Ze, the protector and the keeper of the balance between the human and magic worlds. " +
                        "To accomplish her task, she has been magically enhanced, making her far stronger and faster than ordinary humans. " +
                        "She can also use various kinds of magic to assist her. Maintaining the balance often interferes with her personal life, " +
                        "including her schoolwork and her social life, but she always manages to keep everything flowing.",
                R.drawable.ic_launcher));

    }
}
