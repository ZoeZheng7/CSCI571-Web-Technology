package com.csci571.hw9.hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;



public class Info extends Fragment {


//    private OnFragmentInteractionListener mListener;
    private Context context;

    public Info() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment Info.
     */
//    public static Info newInstance(String param1, String param2) {
//        Info fragment = new Info();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        context = getContext();
        String response = "";
        if (getArguments() != null) {
            response = getArguments().getString("res");
        }
        else {
            Toast.makeText(context, "Error: No arguments in bundle passed to this fragment", Toast.LENGTH_SHORT).show();
        }
        JSONObject res;
        String address;
        String phone;
        int price;
        float rating;
        String url;
        String website;

        try {
            res = new JSONObject(response);
            if (res.has("formatted_address")) {
                address = res.getString("formatted_address");
                LinearLayout infoAdd = (LinearLayout) view.findViewById(R.id.infoAdd);
                TextView placeAddr = (TextView) view.findViewById(R.id.placeAdd);
                infoAdd.setVisibility(View.VISIBLE);
                placeAddr.setText(address);
            }
            if (res.has("international_phone_number")) {
                phone = res.getString("international_phone_number");
                LinearLayout infoPhone = (LinearLayout) view.findViewById(R.id.infoPhone);
                TextView placePhone = (TextView) view.findViewById(R.id.placePhone);
                infoPhone.setVisibility(View.VISIBLE);
                placePhone.setText(phone);
            }
            if (res.has("price_level")) {
                price = res.getInt("price_level");
                LinearLayout infoPrice = (LinearLayout) view.findViewById(R.id.infoPrice);
                TextView placePrice = (TextView) view.findViewById(R.id.placePrice);
                infoPrice.setVisibility(View.VISIBLE);
                String price_level = "";
                for (int i = 0; i < price; i++) {
                    price_level += "$";
                }
                placePrice.setText(price_level);
            }
            if (res.has("rating")) {
                rating = (float) res.getDouble("rating");
                LinearLayout infoRating = (LinearLayout) view.findViewById(R.id.infoRating);
                RatingBar placeRating = (RatingBar) view.findViewById(R.id.placeRating);
                infoRating.setVisibility(View.VISIBLE);
                placeRating.setRating(rating);
            }
            if (res.has("url")) {
                url = res.getString("url");
                LinearLayout infoUrl = (LinearLayout) view.findViewById(R.id.infoGoogle);
                TextView placeUrl = (TextView) view.findViewById(R.id.placeGoogle);
                infoUrl.setVisibility(View.VISIBLE);
                placeUrl.setText(url);
            }
            if (res.has("website")) {
                website = res.getString("website");
                LinearLayout infoWebsite = (LinearLayout) view.findViewById(R.id.infoWebsite);
                TextView placeWebsite = (TextView) view.findViewById(R.id.placeWebsite);
                infoWebsite.setVisibility(View.VISIBLE);
                placeWebsite.setText(website);
            }
        } catch (JSONException ex) {
            throw new IllegalArgumentException("Unexpected parsing error", ex);
        }

        return view;
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }
}
