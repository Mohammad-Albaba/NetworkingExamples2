package com.example.networkingexamples2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.util.HashMap;
import java.util.Map;

public class Login_Activity extends AppCompatActivity {

     private TextInputEditText usernameTextInputEditText;
     private TextInputEditText passwordTextInputEditText;
     private Button loginButton;
     private ProgressBar progressBar;
     private NetworkUtils networkUtils;
     private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        networkUtils = NetworkUtils.getInstance(this);
        gson = new Gson();
        usernameTextInputEditText = findViewById(R.id.edit_text_username);
        passwordTextInputEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.progress_bar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(usernameTextInputEditText.getText().toString())){
                    usernameTextInputEditText.setError(getString(R.string.empty_username));
                }else if (TextUtils.isEmpty(passwordTextInputEditText.getText().toString())){
                    passwordTextInputEditText.setError(getString(R.string.empty_password));
                }else {
                    loginButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    StringRequest loginRequest= new StringRequest(Request.Method.POST, networkUtils.getLoginUrl(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("Response:" + response);
                            LoginResponse loginResponse = gson.fromJson(response, LoginResponse.class);
                            if (loginResponse != null){
                                if (loginResponse.getSuccess()){
                                    Data.setToken(loginResponse.getToken());
                                    startActivity(new Intent(Login_Activity.this ,MainActivity.class));
                                    finish();
                                }else {
                                    Snackbar.make(findViewById(android.R.id.content) , loginResponse.getMessage(),Snackbar.LENGTH_LONG).show();
                                    loginButton.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loginButton.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(findViewById(android.R.id.content) , networkUtils.parseVolleyErrors(error),Snackbar.LENGTH_LONG).show();

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<>();
                            params.put(NetworkUtils.PARAM_USERNAME,usernameTextInputEditText.getText().toString().trim());
                            params.put(NetworkUtils.PARAM_PASSWORD,passwordTextInputEditText.getText().toString().trim());
                            return params;
                        }

                        @Override
                        public Priority getPriority() {
                            return Priority.IMMEDIATE;
                        }
                    };
                    networkUtils.addToRequestQueue(loginRequest);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        networkUtils.cancelRequests();
        super.onDestroy();
    }
}