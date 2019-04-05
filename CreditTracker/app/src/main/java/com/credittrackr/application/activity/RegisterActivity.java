/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.credittrackr.application.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;
import com.credittrackr.application.helper.SessionManager;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputMobileNo;
    private EditText inputDateOfBirth;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    final Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFirstName = (EditText) findViewById(R.id.firstname);
        inputLastName = (EditText) findViewById(R.id.lastname);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputMobileNo = (EditText) findViewById(R.id.mobileno);
        inputDateOfBirth = (EditText) findViewById(R.id.dateofbirth);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // inputDateOfBirth.setKeyListener(null);
        // inputDateOfBirth.setEnabled(false);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String firstname = inputFirstName.getText().toString().trim();
                String lastname = inputLastName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String dateOfBirth = inputDateOfBirth.getText().toString().trim();
                String mobileNumber = inputMobileNo.getText().toString().trim();

                if (!firstname.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !password.isEmpty() && !dateOfBirth.isEmpty() && !mobileNumber.isEmpty()) {
                    registerUser(firstname, lastname, email, password, dateOfBirth, mobileNumber);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        inputDateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url firstname, lastname, email, password, dateOfBirth, mobileNumber
     * */
    private void registerUser(final String firstname, final String lastname, final String email,
                              final String password, final String dateOfBirth, final String mobileNumber) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", firstname);
            jsonObject.put("lastName", lastname);
            jsonObject.put("username", email);
            jsonObject.put("password", password);
            jsonObject.put("dateOfBirth", dateOfBirth);
            jsonObject.put("mobileNumber", mobileNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_REGISTER, jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                // Launch login activity
                Intent intent = new Intent(
                        RegisterActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error");
                Toast.makeText(getApplicationContext(),
                        "Username " + email + " is already taken", Toast.LENGTH_LONG).show();
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
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        inputDateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }
}
