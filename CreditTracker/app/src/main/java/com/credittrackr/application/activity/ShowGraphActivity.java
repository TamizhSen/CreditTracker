package com.credittrackr.application.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;

public class ShowGraphActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    LinearLayout layoutBarChart, layoutPieChart;
    private SQLiteHandler db;
    public static String strSeparator = ",";
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }
    String email = "";
    private ProgressDialog pDialog;
    String cardStr = "";
    JSONArray expenses;
    String[] cards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_graph);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        layoutBarChart = (LinearLayout)findViewById(R.id.barchartLayout);
        layoutPieChart = (LinearLayout)findViewById(R.id.piechartLayout);

        layoutPieChart.setVisibility(View.VISIBLE);


        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        email = user.get("email");

        // String[] cards = convertStringToArray(user.get("cards"));
        // Log.e(TAG, "cards>>>>>>>>."+ cards);

        MaterialSpinner spinner1 = (MaterialSpinner) findViewById(R.id.spinner1);
        spinner1.setItems("Monthly", "Year");
        spinner1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if (item.equals("Monthly")) {
                    layoutBarChart.setVisibility(View.GONE);
                    layoutPieChart.setVisibility(View.VISIBLE);
                } else {
                    layoutPieChart.setVisibility(View.GONE);
                    layoutBarChart.setVisibility(View.VISIBLE);
                }
            }
        });


        /* BarChart chart = (BarChart)findViewById(R.id.barchart);

        ArrayList<BarEntry> NoOfEmp1 = new ArrayList<BarEntry>();

        NoOfEmp1.add(new BarEntry(945f, 0));
        NoOfEmp1.add(new BarEntry(1040f, 1));
        NoOfEmp1.add(new BarEntry(1133f, 2));
        NoOfEmp1.add(new BarEntry(1240f, 3));
        NoOfEmp1.add(new BarEntry(1369f, 4));
        NoOfEmp1.add(new BarEntry(1487f, 5));
        NoOfEmp1.add(new BarEntry(1501f, 6));
        NoOfEmp1.add(new BarEntry(1645f, 7));
        NoOfEmp1.add(new BarEntry(1578f, 8));
        NoOfEmp1.add(new BarEntry(1695f, 9));

        ArrayList<String> year1 = new ArrayList<String>();

        year1.add("2008");
        year1.add("2009");
        year1.add("2010");
        year1.add("2011");
        year1.add("2012");
        year1.add("2013");
        year1.add("2014");
        year1.add("2015");
        year1.add("2016");
        year1.add("2017");

        BarDataSet bardataset = new BarDataSet(NoOfEmp1, "No Of Employee");
        chart.animateY(5000);
        BarData data1 = new BarData(year1, bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data1); */
        getExpensesList(email);
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
     * function to verify login details in mysql db
     * */
    private void getExpensesList(final String email) {
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

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_GET_EXPENSES_LIST, jsonObject, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.d(TAG, "get Expenses List Response: " + response.toString());
                hideDialog();

                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jObj = response;

                    expenses = response.getJSONArray("expenses");
                    getCard(email);


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Username is incorrect", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Username is incorrectr.", error);
                Toast.makeText(getApplicationContext(),
                        "Username is incorrectr."+error, Toast.LENGTH_LONG).show();
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

    private void showGraph(JSONArray expenses, String card){
        float  housingTotal = 0;
        float  transportationTotal = 0;
        float  foodTotal = 0;
        float  debtTotal = 0;
        float  otherTotal = 0;
        try {
            Log.d(TAG, "get Expenses List Response: " + expenses.toString());
            Log.d(TAG, "get Expenses List Response: " + card);
            for (int i = 0;i<expenses.length(); i++) {
                JSONObject expenseItem = expenses.getJSONObject(i);
                Log.d(TAG, "get Expenses List Response: " + expenseItem.toString());
                Log.d(TAG, "get Expenses List Response: " + expenseItem.getString("card").equals(card));
                if ((expenseItem.getString("categories").equals("Groceries")
                        || expenseItem.getString("categories").equals("Electronic")
                        || expenseItem.getString("categories").equals("Furniture")
                        || expenseItem.getString("categories").equals("Insurance")
                        || expenseItem.getString("categories").equals("Heat /Gas")
                        || expenseItem.getString("categories").equals("Clothing")) && expenseItem.getString("card").equals(card)){
                    housingTotal += Float.parseFloat(expenseItem.getString("expense"));
                }
                if ((expenseItem.getString("categories").equals("Travel")
                        || expenseItem.getString("categories").equals("Gas / Fuel")
                        || expenseItem.getString("categories").equals("Parking"))&& expenseItem.getString("card").equals(card)){
                    transportationTotal += Float.parseFloat(expenseItem.getString("expense"));
                }
                if ((expenseItem.getString("categories").equals("Restaurant")
                        || expenseItem.getString("categories").equals("Hotel"))&& expenseItem.getString("card").equals(card)){
                    foodTotal += Float.parseFloat(expenseItem.getString("expense"));
                }
                if (expenseItem.getString("categories").equals("Medical")&& expenseItem.getString("card").equals(card)){
                    debtTotal += Float.parseFloat(expenseItem.getString("expense"));
                }
                if ((expenseItem.getString("categories").equals("Movie")
                        || expenseItem.getString("categories").equals("Other")
                        || expenseItem.getString("categories").equals("Multimedia")
                        || expenseItem.getString("categories").equals("Taxes")
                        || expenseItem.getString("categories").equals("Tv/Phone/Internet")
                        || expenseItem.getString("categories").equals("Academic"))&& expenseItem.getString("card").equals(card)){
                    otherTotal += Float.parseFloat(expenseItem.getString("expense"));
                }
            }

            int index = 0;

            PieChart pieChart = (PieChart)findViewById(R.id.piechart);

            ArrayList<Entry> NoOfEmp = new ArrayList<Entry>();
            ArrayList<String> categories = new ArrayList<String>();
            ArrayList<String> barcategories = new ArrayList<String>();

            BarChart chart = (BarChart)findViewById(R.id.barchart);
            ArrayList<BarEntry> NoOfEmp1 = new ArrayList<BarEntry>();

            if (housingTotal > 0) {
                NoOfEmp.add(new Entry(housingTotal, index));
                NoOfEmp1.add(new BarEntry(housingTotal, index));
                categories.add("Housing");
                barcategories.add("H");
                index++;
            }
            if (transportationTotal > 0) {
                NoOfEmp.add(new Entry(transportationTotal, index));
                NoOfEmp1.add(new BarEntry(transportationTotal, index));
                categories.add("Transport");
                barcategories.add("T");
                index++;
            }
            if (foodTotal > 0) {
                NoOfEmp.add(new Entry(foodTotal, index));
                NoOfEmp1.add(new BarEntry(foodTotal, index));
                categories.add("Food");
                barcategories.add("F");
                index++;
            }
            if (debtTotal > 0) {
                NoOfEmp.add(new Entry(debtTotal, index));
                NoOfEmp1.add(new BarEntry(debtTotal, index));
                categories.add("Medical");
                barcategories.add("M");
                index++;
            }
            if (otherTotal > 0) {
                NoOfEmp.add(new Entry(otherTotal, index));
                NoOfEmp1.add(new BarEntry(otherTotal, index));
                categories.add("Others");
                barcategories.add("O");
            }

            PieDataSet dataSet = new PieDataSet(NoOfEmp, "");
            PieData data = new PieData(categories, dataSet);
            pieChart.setData(data);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieChart.animateXY(5000, 5000);

            BarDataSet bardataset = new BarDataSet(NoOfEmp1, "H-Housing,T-Transport,F-Food,M-Medical,O-Other");
            chart.animateY(5000);
            BarData data1 = new BarData(barcategories, bardataset);
            bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            chart.setData(data1);
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Username is incorrect", Toast.LENGTH_LONG).show();
        }
    }

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

                    JSONArray cardsArray = response.getJSONArray("cards");

                    String cardsArr = convertArrayToString(cardsArray);
                    cards = convertStringToArray(cardsArr);
                    if(cards.length > 0 ){
                        cardStr = cards[0];
                    }
                    showGraph(expenses, cardStr);
                    MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
                    spinner.setItems(cards);
                    spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                            cardStr = item;
                            showGraph(expenses, cardStr);
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
