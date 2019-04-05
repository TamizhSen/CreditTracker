package com.credittrackr.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;
import com.credittrackr.application.helper.SessionManager;

public class PaymentActivity extends AppCompatActivity {

    private EditText inputDescription;
    private EditText inputExp;
    private Button btnAddExpense;
    final Calendar myCalendar = Calendar.getInstance();
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    String email = "";
    String displayName = "";
    String cardStr = "";
    String[] cards;

    public static String strSeparator = ",";
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputDescription = (EditText) findViewById(R.id.descriptionET);
        inputExp = (EditText) findViewById(R.id.expenseET);
        btnAddExpense = (Button) findViewById(R.id.btnAddExpense);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());


        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");
        displayName = user.get("firstName");

        getCard(email);

         // convertStringToArray(user.get("cards"));

        // Register Button Click event
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String description = inputDescription.getText().toString().trim();
                String amount = inputExp.getText().toString().trim();

                if (cardStr.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please add at least one card!", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                if (!cardStr.isEmpty() && !amount.isEmpty()) {
                    payment(description, amount);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url firstname, lastname, email, password, dateOfBirth, mobileNumber
     * */
    private void payment(final String description, final String amount) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Adding ...");
        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("card", cardStr);
            jsonObject.put("description", description);
            jsonObject.put("amount", amount);
            jsonObject.put("displayName", displayName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_ADD_PAYMENT, jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideDialog();

                Toast.makeText(getApplicationContext(),
                        "Payment Added Successfully", Toast.LENGTH_LONG)
                        .show();

                // Launch login activity
                Intent intent = new Intent(
                        PaymentActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Username " + email + " is already taken", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static String convertArrayToString(JSONArray array){
        String str = "";
        for (int i = 0;i<array.length(); i++) {
            try {
                JSONObject cards = array.getJSONObject(i);
                String input = cards.getString("cardNumber");
                StringBuilder result = new StringBuilder();
                for (int l = 0; l < input.length(); l++) {
                    if (l % 4 == 0 && l != 0) {
                        result.append(" ");
                    }

                    result.append(input.charAt(l));
                }
                str = str+result;
                // Do not append comma at the end of last element
                if(i<array.length()-1){
                    str = str+strSeparator;
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }
        }
        return str;
    }
    /**
     * function to verify login details in mysql db
     * */
    private void getCard(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_get_expenses";

        pDialog.setMessage("Loading...");
        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_GET_CARD_LIST, jsonObject, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                hideDialog();

                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jObj = response;

                    JSONArray expenses = response.getJSONArray("cards");

                    String cardsArr = convertArrayToString(expenses);
                    cards = convertStringToArray(cardsArr);
                    if(cards.length > 0 ){
                        cardStr = cards[0];
                    }

                    // Now store the user in SQLite
                    String status = jObj.getString("message");
                    MaterialSpinner cardSpinner = (MaterialSpinner) findViewById(R.id.card);
                    cardSpinner.setItems(cards);
                    cardSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                            cardStr = item;
                        }
                    });


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Username is incorrect", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Username is incorrectr."+error, Toast.LENGTH_LONG).show();
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
