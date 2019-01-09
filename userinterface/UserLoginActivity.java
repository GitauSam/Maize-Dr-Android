package org.tensorflow.demo.userinterface;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.demo.R;
import org.tensorflow.demo.SharedPrefs.SharedPrefManager;
import org.tensorflow.demo.models.Constants;
import org.tensorflow.demo.models.User;
import org.tensorflow.demo.urls.URLs;
import org.tensorflow.demo.volley.VolleySingleton;

import java.util.HashMap;
import java.util.Map;


public class UserLoginActivity extends Activity {

    EditText editTextUsertag;
    EditText editTextPassword;
    Button buttonLogin;
    TextView textViewUserLoginNotRegd;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "Already Logged on", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        editTextUsertag = findViewById(R.id.edit_text_usertag);
        editTextPassword = findViewById(R.id.edit_text_password);

        buttonLogin = findViewById(R.id.btn_userlogin_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        textViewUserLoginNotRegd = findViewById(R.id.text_view_account_not_exists);
        textViewUserLoginNotRegd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLoginActivity.this, UserRegistrationActivity.class));
                finish();
            }
        });

        progressBar = findViewById(R.id.progress_bar_userlogin);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void userLogin() {

        final int flag;
        final String userTag = editTextUsertag.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(userTag) && !TextUtils.isEmpty(password)) {

            progressBar.setVisibility(View.VISIBLE);

            if(isDouble(userTag)) {
                flag = 1;
            } else {
                flag = 0;
            }

            Toast.makeText(getApplicationContext(), String.valueOf(flag), Toast.LENGTH_SHORT).show();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject obj = new JSONObject(response);

                                if (!obj.getBoolean("error")) {
                                    progressBar.setVisibility(View.INVISIBLE);

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

                                    Constants.setID(user.getUserID());

                                    //storing the user in shared preferences
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                    //starting next activity
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                                    Toast.makeText(getApplicationContext(),
//                                            "User ID: " + String.valueOf(user.getUserID()) + " " +
//                                                    "Firstname: " + user.getFirstName() + " " +
//                                                    "Lastname: " + user.getLastName() + " " +
//                                                    "Phone number: " + user.getPhoneNo() + " " +
//                                                    "Email: " + user.getEmail(), Toast.LENGTH_LONG).show();

                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("flag", String.valueOf(flag));
                    params.put("userTag", userTag);
                    params.put("password", password);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

        } else {
//            Snackbar.make(g)
            Toast.makeText(getApplicationContext(), "Please fill in the necessary details!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isDouble(String input) {
        try{
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }
}
