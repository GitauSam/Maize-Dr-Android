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

public class UserRegistrationActivity extends Activity {

    EditText editTextFirstName, editTextLastName, editTextEmail, editTextPhoneno, editTextPassword, editTextConfirmPassword;

    TextView textViewUserAlrRegd;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "Already logged on", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, HomeActivity.class));
            return;
        }
        editTextFirstName = findViewById(R.id.edit_text_userreg_first_name);
        editTextLastName = findViewById(R.id.edit_text_userreg_last_name);
        editTextEmail = findViewById(R.id.edit_text_userreg_email);
        editTextPhoneno = findViewById(R.id.edit_text_userreg_phoneno);
        editTextPassword = findViewById(R.id.edit_text_userreg_password);
        editTextConfirmPassword = findViewById(R.id.edit_text_userreg_confirm_password);

        textViewUserAlrRegd = findViewById(R.id.text_view_userreg_account_exists);
        textViewUserAlrRegd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserRegistrationActivity.this, UserLoginActivity.class));
                finish();
            }
        });

        Button buttonRegister = findViewById(R.id.btn_userreg_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        progressBar = findViewById(R.id.progress_bar_userreg);
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void registerUser() {
        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String phoneNo = editTextPhoneno.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if(!TextUtils.isEmpty(firstName)&&!TextUtils.isEmpty(lastName)&&!TextUtils.isEmpty(email)&&
                !TextUtils.isEmpty(phoneNo)&&!TextUtils.isEmpty(password)&&!TextUtils.isEmpty(confirmPassword)) {
            if(confirmPasswordLength(password)) {
                if(validatePassword(confirmPassword, password)) {

                    progressBar.setVisibility(View.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            URLs.URL_REGISTER, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            progressBar.setVisibility(View.GONE);

                            //Toast.makeText(getApplicationContext(), "Execution fikas here", Toast.LENGTH_SHORT).show();

                            try {
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

                                    Constants.setID(user.getUserID());
                                    //storing the user in shared preferences
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                    //starting next activity
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                                        Toast.makeText(getApplicationContext(),
//                                                "User ID: " + String.valueOf(user.getUserID()) + " " +
//                                                "Firstname: " + user.getFirstName() + " " +
//                                                "Lastname: " + user.getLastName() + " " +
//                                                "Phone number: " + user.getPhoneNo() + " " +
//                                                "Email: " + user.getEmail(), Toast.LENGTH_LONG).show();
                                } else {
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
                            params.put("firstName", firstName);
                            params.put("lastName", lastName);
                            params.put("email", email);
                            params.put("phoneNo", phoneNo);
                            params.put("password", password);
                            return params;
                        }
                    };

                    VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
                } else {
                    editTextConfirmPassword.setError("Passwords do not match");
                }
            } else {
                editTextPassword.setError("Password should be at least 6 charachters");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please fill in the necessary details.", Toast.LENGTH_LONG).show();
        }
    }

    public boolean confirmPasswordLength(String password) {
        return password.length() > 5;
    }

    public boolean validatePassword(String confirmPassword, String password) {
        return TextUtils.equals(confirmPassword, password);
    }
}
