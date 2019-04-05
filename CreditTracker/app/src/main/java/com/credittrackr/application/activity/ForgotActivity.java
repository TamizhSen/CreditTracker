package com.credittrackr.application.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;

public class ForgotActivity extends Activity {
    private static final String TAG = ForgotActivity.class.getSimpleName();
	private EditText inputEmail;
	private Button btnLogin;
	private Button btnSubmit;
    private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot);

		inputEmail = (EditText) findViewById(R.id.email);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

		// Logout button click event
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = inputEmail.getText().toString().trim();

				// Check for empty data in the form
				if (!email.isEmpty()) {
					// login user
					doForgotPassword(email);
				} else {
					// Prompt user to enter credentials
					Toast.makeText(getApplicationContext(),
							"Please enter the credentials!", Toast.LENGTH_LONG)
							.show();
				}
				// logoutUser();
			}
		});
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});
	}

	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {

		// Launching the login activity
		Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

    /**
     * function to verify login details in mysql db
     * */
    private void doForgotPassword(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Loading...");
        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_FORGOT_PASSWORD, jsonObject, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = response;


                    // Now store the user in SQLite
                    Boolean status = jObj.getBoolean("success");

                    if (status) {
                        Toast.makeText(getApplicationContext(), "Password sent to your email, Please do longin and reset it.", Toast.LENGTH_LONG).show();
                        // Launch main activity
                        Intent intent = new Intent(ForgotActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "The server not responding please try again.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Username is incorrect", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Username is incorrectr.");
                Toast.makeText(getApplicationContext(),
                        "Username is incorrectr.", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
