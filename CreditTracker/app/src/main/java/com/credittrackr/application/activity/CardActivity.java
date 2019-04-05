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
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

public class CardActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<CardItem> cartList;
    private CartListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton addExpense;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    String email = "";

    // url to fetch menu json
    private static final String URL = "https://api.androidhive.info/json/menu.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_main);
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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
        addExpense = (FloatingActionButton) findViewById(R.id.fab);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
                startActivity(new Intent(CardActivity.this, AddCardActivity.class));
                finish();
            }
        });
        addExpense.show();
        cartList = new ArrayList<>();
        mAdapter = new CartListAdapter(this, cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        // ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerCardItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        // new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        // making http call and fetching menu json
        // prepareCart();
        getExpensesList(email);

        /*/ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP) {
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
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);*/
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

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_GET_CARD_LIST, jsonObject, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.d(TAG, "get Expenses List Response: " + response.toString());
                hideDialog();

                if (response == null) {
                    Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jObj = response;

                    JSONArray expenses = response.getJSONArray("cards");

                    // Now store the user in SQLite
                    String status = jObj.getString("message");

                    List<CardItem> items = new Gson().fromJson(expenses.toString(), new TypeToken<List<CardItem>>() {
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
            String name = cartList.get(viewHolder.getAdapterPosition()).getCardNumber();

            // backup of removed item for undo purpose
            final CardItem deletedItem = cartList.get(viewHolder.getAdapterPosition());
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
}
