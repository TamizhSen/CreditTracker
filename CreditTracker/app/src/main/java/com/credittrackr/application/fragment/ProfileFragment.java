package com.credittrackr.application.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.credittrackr.application.R;
import com.credittrackr.application.activity.MainActivity;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment  implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView usernameTV, emailTV, dateOfBirthTV, mobileNumberTV;
    ImageView editIcon;

    // TODO: Rename and change types of parameters
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    String email = "";

    private ProfileFragment.OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTV = (TextView) v.findViewById(R.id.username);
        emailTV = (TextView) v.findViewById(R.id.usernametext);
        dateOfBirthTV = (TextView) v.findViewById(R.id.dob);
        mobileNumberTV = (TextView) v.findViewById(R.id.mobilenotext);
        editIcon = (ImageView) v.findViewById(R.id.edit);
        editIcon.setOnClickListener(this);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getActivity());
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");
        getProfile(email);
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
    }

    public void onClick(final View v) { //check for what button is pressed
        switch (v.getId()) {
            case R.id.edit:
                //startActivity(new Intent(getActivity(), MainActivity.class));
                String value = "settings";
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("status", value);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * function to verify login details in mysql db
     * */
    private void getProfile(final String email) {
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

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_GET_PROFILE, jsonObject, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                hideDialog();

                if (response == null) {
                    Toast.makeText(getActivity(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    JSONObject jObj = response;

                    // Now store the user in SQLite
                    String firstName = jObj.getString("firstName");
                    String lastName = jObj.getString("lastName");
                    String email = jObj.getString("username");
                    String dob = jObj.getString("dateOfBirth");
                    String mobileno = jObj.getString("mobileNumber");

                    usernameTV.setText(firstName + " " + lastName);
                    emailTV.setText(email);
                    dateOfBirthTV.setText(dob);
                    mobileNumberTV.setText(mobileno);


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Username is incorrect", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
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
