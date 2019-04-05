package com.credittrackr.application.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.credittrackr.application.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    LinearLayout showGraph, email, addCard, addPDF, viewExpenses;
    public FragmentCommunicator fComm;
    int c;

    // private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        showGraph = (LinearLayout) v.findViewById(R.id.showgraph);
        email = (LinearLayout) v.findViewById(R.id.email);
        addCard = (LinearLayout) v.findViewById(R.id.addcard);
        addPDF = (LinearLayout) v.findViewById(R.id.addpdf);
        viewExpenses = (LinearLayout) v.findViewById(R.id.viewexpenses);
        showGraph.setOnClickListener(this);
        email.setOnClickListener(this);
        addCard.setOnClickListener(this);
        viewExpenses.setOnClickListener(this);
        addPDF.setOnClickListener(this);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /* if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        } */
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
        try {
            fComm = (FragmentCommunicator) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentCommunicator");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
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
    /* public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    } */
    public void onClick(final View v) { //check for what button is pressed
        switch (v.getId()) {
            case R.id.showgraph:
                c *= fComm.fragmentContactActivity(0);
                break;
            case R.id.email:
                c *= fComm.fragmentContactActivity(1);
                break;
            case R.id.addcard:
                c *= fComm.fragmentContactActivity(2);
                break;
            case R.id.addpdf:
                c *= fComm.fragmentContactActivity(3);
                break;
            case R.id.viewexpenses:
                c *= fComm.fragmentContactActivity(4);
                break;
            default:
                c *= fComm.fragmentContactActivity(100);
                break;
        }
    }

    public interface FragmentCommunicator{
        public int fragmentContactActivity(int b);
    }
}
