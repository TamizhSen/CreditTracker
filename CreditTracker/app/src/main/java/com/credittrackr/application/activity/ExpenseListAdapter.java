package com.credittrackr.application.activity;

/**
 * Created by ravi on 26/09/17.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.MyViewHolder> {
    private Context context;
    private List<Item> cartList;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private String email = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryTV, cardTV, description, expenseTV, price;
        // public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            categoryTV = (TextView) view.findViewById(R.id.categories);
            cardTV = (TextView) view.findViewById(R.id.card);
            expenseTV = (TextView) view.findViewById(R.id.expenseDate);
            description = (TextView) view.findViewById(R.id.description);
            price = (TextView) view.findViewById(R.id.price);
            // thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            viewBackground = (RelativeLayout) view.findViewById(R.id.view_background);
            viewForeground = (RelativeLayout) view.findViewById(R.id.view_foreground);
        }
    }


    public ExpenseListAdapter(Context context, List<Item> cartList) {
        this.context = context;
        this.cartList = cartList;
        // Progress dialog
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(context);


        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Item item = cartList.get(position);
        holder.categoryTV.setText(item.getCategories());
        holder.cardTV.setText(item.getCard());
        holder.expenseTV.setText(item.getExpenseDate());
        if (item.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        }
        holder.description.setText(item.getDescription());
        holder.price.setText("$" + item.getExpense().replace("$", ""));

        // holder.viewForeground
        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the 'context' here

            }
        });

        holder.viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("DELETE EXPENSE");
                String alert5 = "Are you sure you want to delete this expense?";
                alertDialog.setMessage(alert5);
                //alertDialog.setMessage("Minimum   ");
                alertDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteExpense(email, item.getId(), position);

                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeItem(int position) {
        cartList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Item item, int position) {
        cartList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    private void deleteExpense(final String email, final String id, final int position) {
        // Tag used to cancel the request
        String tag_string_req = "req_get_expenses";

        pDialog.setMessage("Loading...");
        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_EXPENSE_DELETE, jsonObject, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                hideDialog();

                if (response == null) {
                    Toast.makeText(context, "Couldn't fetch the expense! Pleas try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jObj = response;

                    // Now store the user in SQLite
                    String status = jObj.getString("message");

                    if (status.equals("Success")) {
                        cartList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context,
                                "Expenses deleted successfully", Toast.LENGTH_LONG)
                                .show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(context, "Username is incorrect", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,
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
