package com.credittrackr.application.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.credittrackr.application.R;
import com.credittrackr.application.activity.AddExpenseActivity;
import com.credittrackr.application.activity.ExpenseListAdapter;
import com.credittrackr.application.activity.ExpensesActivity;
import com.credittrackr.application.activity.Item;
import com.credittrackr.application.activity.MainActivity;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ARG_PARAM1 = "status";
    private String mParam1 = "";
    TextView usernameTV;
    ImageView notificationIcon;
    private RecyclerView recyclerView;
    private List<Item> cartList;
    private FloatingActionButton addExpense;
    private ExpenseListAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    String email = "";

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        Log.d(TAG, "get Expenses List Response: " + mParam1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notification_main, container, false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getActivity());
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");
        /* Log.d(TAG, "get Expenses List inflate: " + mParam1);
        usernameTV = (TextView) v.findViewById(R.id.notificationmsg);
        notificationIcon = (ImageView) v.findViewById(R.id.notificationIcon);
        if(mParam1.equals("Expenses Added Successfully")) {
            usernameTV.setText(mParam1);
            usernameTV.setVisibility(View.VISIBLE);
            notificationIcon.setVisibility(View.INVISIBLE);
        } else {
            usernameTV.setVisibility(View.INVISIBLE);
            notificationIcon.setVisibility(View.VISIBLE);
        } */

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinator_layout);
        addExpense = (FloatingActionButton) v.findViewById(R.id.fab);
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
        addExpense.show();
        cartList = new ArrayList<>();
        mAdapter = new ExpenseListAdapter(getActivity(), cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        getExpensesList(email);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }/**
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

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_GET_SORT_EXPENSES_LIST, jsonObject, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.d(TAG, "get Expenses List Response: " + response.toString());
                hideDialog();

                if (response == null) {
                    Toast.makeText(getActivity(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), "Username is incorrect", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Username is incorrectr.", error);
                Toast.makeText(getActivity(),
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
