package com.credittrackr.application.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;

public class ExpensesActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Item> cartList;
    private ExpenseListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton addExpense;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    String email = "";
    String cardStr = "";
    // final String[] EXPENSEGATEGORIES = {"Movie","Music","Sports","Restaurant","Groceries","Liquor","Electronic","Furniture","Clothing","Gifts","Insurance","Medical","Taxes","Gas / Fuel"};

    // url to fetch menu json
    private static final String URL = "https://api.androidhive.info/json/menu.json";
    String[] cards;
    public static String strSeparator = ",";
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_main);
        /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.my_cart));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");
        // getCard(email);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
        addExpense = (FloatingActionButton) findViewById(R.id.fab);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
                startActivity(new Intent(ExpensesActivity.this, AddExpenseActivity.class));
                finish();
            }
        });
        /*MaterialSpinner cardSpinner = (MaterialSpinner) findViewById(R.id.cardlist);
        cardSpinner.setItems(EXPENSEGATEGORIES);
        cardSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                cardStr = item;
            }
        }); */
        addExpense.show();
        cartList = new ArrayList<>();
        mAdapter = new ExpenseListAdapter(this, cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        //ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        //new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        // making http call and fetching menu json
        // prepareCart();
        getExpensesList(email);

        /* ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView); */
    }

    /**
     * method make volley network call and parses json
     */
    private void prepareCart() {
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Item> items = new Gson().fromJson(response.toString(), new TypeToken<List<Item>>() {
                        }.getType());

                        // adding items to cart list
                        cartList.clear();
                        cartList.addAll(items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
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

                    JSONArray expenses = response.getJSONArray("expenses");

                    // Now store the user in SQLite
                    String status = jObj.getString("message");

                    List<Item> items = new Gson().fromJson(expenses.toString(), new TypeToken<List<Item>>() {
                    }.getType());

                    // adding items to cart list
                    cartList.clear();
                    cartList.addAll(items);

                    // refreshing recycler view
                    mAdapter.notifyDataSetChanged();


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


    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    /*@Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewHolder.getAdapterPosition()).getDescription();

            // backup of removed item for undo purpose
            final Item deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from expenses!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }*/

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds cartList to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    } */
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
                    /* MaterialSpinner cardSpinner = (MaterialSpinner) findViewById(R.id.cardlist);
                    cardSpinner.setItems(cards);
                    cardSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                            cardStr = item;
                        }
                    }); */


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
}
