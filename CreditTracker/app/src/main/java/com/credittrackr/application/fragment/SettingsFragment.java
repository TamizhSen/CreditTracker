package com.credittrackr.application.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.credittrackr.application.R;
import com.credittrackr.application.app.AppConfig;
import com.credittrackr.application.app.AppController;
import com.credittrackr.application.helper.SQLiteHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment  implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    EditText currentPasswordET, newPasswordET, confirmPasswordET;
    Button changePasswordBtn;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    String email = "";

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        currentPasswordET = (EditText) v.findViewById(R.id.currentPasswordET);
        newPasswordET = (EditText) v.findViewById(R.id.newPasswordET);
        confirmPasswordET = (EditText) v.findViewById(R.id.confirmPasswordET);
        changePasswordBtn = (Button) v.findViewById(R.id.btnChangePassword);
        changePasswordBtn.setOnClickListener(this);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getActivity());
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        email = user.get("email");
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
            case R.id.btnChangePassword:
                oldPassword = currentPasswordET.getText().toString();
                newPassword = newPasswordET.getText().toString();
                confirmPassword = confirmPasswordET.getText().toString();
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getActivity(),"Please enter your details!",Toast.LENGTH_LONG).show();
                    return;
                }
                if (oldPassword.equals(confirmPassword)) {
                    Toast.makeText(getActivity(),"Onewld password and new password cannot be same",Toast.LENGTH_LONG).show();
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getActivity(),"New and Confirm passwords do not match",Toast.LENGTH_LONG).show();
                    return;
                }

                changePassword(oldPassword, newPassword);

                break;
            default:
                break;
        }
    }

    private void changePassword(final String oldPassword, final String newPassword) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Adding ...");
        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", email);
            jsonObject.put("currentpassword", oldPassword);
            jsonObject.put("newpassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest strReq = new JsonObjectRequest(AppConfig.URL_UPDATE_PASSWORD, jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                hideDialog();
                try {
                    if (response.getString("message").equals("Success")) {
                        Toast.makeText(getActivity(),
                                "Password changed Successfully, Please do logout and re-login again", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        Toast.makeText(getActivity(), "Password is incorrect", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Password is incorrect", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),
                        "Password is incorrect", Toast.LENGTH_LONG).show();
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
