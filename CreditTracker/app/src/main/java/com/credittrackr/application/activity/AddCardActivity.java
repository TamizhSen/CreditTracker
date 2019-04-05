package com.credittrackr.application.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;
import com.credittrackr.application.helper.SessionManager;
import com.jaredrummler.materialspinner.MaterialSpinner;

import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class AddCardActivity extends AppCompatActivity {

    private EditText cardNumber;
    //private EditText cardType;
    private EditText expirationDate;
    //private EditText bank;
    private EditText interestRate;
    private EditText startDate;
    private EditText endDate;
    private EditText maximumLimit;
    private EditText cardTypeOther;
    private EditText bankOther;
    private Button btnAddCard;
    final Calendar myCalendar = Calendar.getInstance();
    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    String email = "";
    String cardTypeStr = "Network";
    String bankStr = "Royal Bank of Canada (RBC)";
    final String[] BANK =  {"Royal Bank of Canada (RBC)", "Toronto-Dominion Bank (TD)","Bank of Nova Scotia (Scotiabank)","Bank of Montreal (BMO)"," Canadian Imperial Bank of Commerce (CIBC)","National Bank of Canada","Desjardins Group","HSBC Bank of Canada","Laurentian Bank of Canada","Canadian Western Bank","Other"};
    final String[] CARDS = {"Network","American Express","Discover","MasterCard","Visa","Other"};

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel("expirationDate");
        }

    };
    DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel("startDate");
        }

    };
    DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel("endDate");
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardNumber = (EditText) findViewById(R.id.cardNumber);
        //cardType = (EditText) findViewById(R.id.cardType);
        expirationDate = (EditText) findViewById(R.id.expirationDate);
        //bank = (EditText) findViewById(R.id.bank);
        interestRate = (EditText) findViewById(R.id.interestRate);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        maximumLimit = (EditText) findViewById(R.id.maximumLimit);
        cardTypeOther = (EditText) findViewById(R.id.cardTypeOther);
        bankOther = (EditText) findViewById(R.id.bankOther);
        btnAddCard = (Button) findViewById(R.id.btnAddCard);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        /* if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(AddExpenseActivity.this,
                    MainActivity.class);
            startActivity(intent);

        } */

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");

        // Register Button Click event
        btnAddCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String cardNumberStr = cardNumber.getText().toString().trim();
                // String cardTypeStr = cardType.getText().toString().trim();
                String expirationDateStr = expirationDate.getText().toString().trim();
                // String bankStr = bank.getText().toString().trim();
                String interestRateStr = interestRate.getText().toString().trim();
                String startDateStr = startDate.getText().toString().trim();
                String endDateStr = endDate.getText().toString().trim();
                String maximumLimitStr = maximumLimit.getText().toString().trim();
                if (cardTypeStr.equals("Other")) {
                    cardTypeStr = cardTypeOther.getText().toString().trim();
                }
                if (bankStr.equals("Other")) {
                    bankStr = bankOther.getText().toString().trim();
                }

                if (!cardNumberStr.isEmpty() && !cardTypeStr.isEmpty() && !expirationDateStr.isEmpty() && !bankStr.isEmpty() && !interestRateStr.isEmpty() && !startDateStr.isEmpty() && !endDateStr.isEmpty() && !maximumLimitStr.isEmpty()) {
                    if (CheckDates(startDateStr, endDateStr)) {
                        Toast.makeText(getApplicationContext(),
                                "End date should be greater than start date!", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    if (CheckDates(endDateStr, expirationDateStr)) {
                        Toast.makeText(getApplicationContext(),
                                "Payment date should be greater than end date!", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    addCard(cardNumberStr, cardTypeStr, expirationDateStr, bankStr, interestRateStr, startDateStr, endDateStr, maximumLimitStr);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        expirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddCardActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddCardActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddCardActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        MaterialSpinner cardTypeSpinner = (MaterialSpinner) findViewById(R.id.cardType);
        cardTypeSpinner.setItems(CARDS);
        cardTypeSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                cardTypeStr = item;
                if (item.equals("Other")){
                    cardTypeStr = "";
                    cardTypeOther.setVisibility(View.VISIBLE);
                } else {
                    cardTypeOther.setVisibility(View.GONE);
                }
            }
        });
        MaterialSpinner bankSpinner = (MaterialSpinner) findViewById(R.id.bank);
        bankSpinner.setItems(BANK);
        bankSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                bankStr = item;
                if (item.equals("Other")){
                    bankStr = "";
                    bankOther.setVisibility(View.VISIBLE);
                    // Get the TextView current LayoutParams
                    LayoutParams lp = (LinearLayout.LayoutParams) bankOther.getLayoutParams();

                    // Set TextView layout margin 25 pixels to all side
                    // Left Top Right Bottom Margin
                    lp.setMargins(0,0,0,15);
                    // Get the TextView current LayoutParams
                    LayoutParams lp1 = (LinearLayout.LayoutParams) interestRate.getLayoutParams();

                    // Set TextView layout margin 25 pixels to all side
                    // Left Top Right Bottom Margin
                    lp1.setMargins(0,0,0,15);

                    // Apply the updated layout parameters to TextView
                    bankOther.setLayoutParams(lp);
                } else {
                    bankOther.setVisibility(View.GONE);
                    LayoutParams lp1 = (LinearLayout.LayoutParams) interestRate.getLayoutParams();

                    // Set TextView layout margin 25 pixels to all side
                    // Left Top Right Bottom Margin
                    lp1.setMargins(0,15,0,15);
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

    private void updateLabel(String dateFlog) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (dateFlog.equals("expirationDate")) {
            expirationDate.setText(sdf.format(myCalendar.getTime()));
        }
        if (dateFlog.equals("startDate")) {
            startDate.setText(sdf.format(myCalendar.getTime()));
        }
        if (dateFlog.equals("endDate")) {
            endDate.setText(sdf.format(myCalendar.getTime()));
        }

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url firstname, lastname, email, password, dateOfBirth, mobileNumber
     * */
    private void addCard(final String cardNumberStr, final String cardTypeStr, final String expirationDateStr, final String bankStr,
                         final String interestRateStr, final String startDateStr, final String endDateStr, final String maximumLimitStr) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Adding ...");
        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("cardNumber", cardNumberStr);
            jsonObject.put("cardType", cardTypeStr);
            jsonObject.put("expirationDate", expirationDateStr);
            jsonObject.put("bank", bankStr);
            jsonObject.put("interestRate", interestRateStr);
            jsonObject.put("startDate", startDateStr);
            jsonObject.put("endDate", endDateStr);
            jsonObject.put("maximumLimit", maximumLimitStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_ADD_CARD, jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideDialog();

                Toast.makeText(getApplicationContext(),
                        "Card Added Successfully", Toast.LENGTH_LONG)
                        .show();

                // Launch login activity
                Intent intent = new Intent(
                        AddCardActivity.this,
                        CardActivity.class);
                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "Card " + email + " is already Added", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    SimpleDateFormat dfDate  = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    public boolean CheckDates(final String date1, final String date2) {
        boolean b = true;
        try {
            if(dfDate.parse(date1).before(dfDate.parse(date2)))
            {
                b = false;//If start date is before end date
            }
            /* else if(dfDate.parse(date1).equals(dfDate.parse(date2)))
            {
                b = true;//If two dates are equal
            } */
            else
            {
                b = true; //If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
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
