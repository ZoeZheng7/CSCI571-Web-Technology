package com.csci571.hw9.hw9;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class Tab1 extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
//
//    public Tab1() {
//        // Required empty public constructor
//    }

    private Context context;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        double lat;
        double lng;
        context = getContext();
//        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        LocationManager locationManager = null;
        if (context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        else {
            Toast.makeText(getContext(), "Errors: API Failure", Toast.LENGTH_SHORT).show();
        }


//        LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
//        if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
//            locationManager.requestLocationUpdates(provider);
//        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (getActivity() != null) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                Toast.makeText(getContext(), "Errors: API Failure", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Location location;
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
                else {
                    lat = 0.0;
                    lng = 0.0;
                    Toast.makeText(getContext(), "Errors: Cannot get location", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                lat = 0.0;
                lng = 0.0;
                Toast.makeText(getContext(), "Errors: Cannot get location", Toast.LENGTH_SHORT).show();
            }
            TextView latitude = (TextView) view.findViewById(R.id.lat);
            TextView longitude = (TextView) view.findViewById(R.id.lng);
            latitude.setText(String.valueOf(lat));
            longitude.setText(String.valueOf(lng));
        }

        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.location);
//        RadioButton rb1 = (RadioButton) view.findViewById(R.id.current);
//        final EditText et = (EditText) view.findViewById(R.id.custom);

        final AutoCompleteTextView et = (AutoCompleteTextView) view.findViewById(R.id.autoComplete);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        CustomAutoCompleteAdapter customAdapter = new CustomAutoCompleteAdapter(getContext());
        et.setAdapter(customAdapter);



        Button search = (Button) view.findViewById(R.id.search);
        Button clear = (Button) view.findViewById(R.id.clear);


        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText keyword = (EditText) view.findViewById(R.id.keyword);
                keyword.setText("");
                EditText distance = (EditText) view.findViewById(R.id.distance);
                distance.setText("");
                Spinner category = (Spinner) view.findViewById(R.id.category);
                category.setSelection(0);
                et.setText("");
                rg.check(R.id.current);
                TextView valid1 = (TextView) view.findViewById(R.id.validation1);
                TextView valid2 = (TextView) view.findViewById(R.id.validation2);
                valid1.setVisibility(View.GONE);
                valid2.setVisibility(View.GONE);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText keyword = (EditText) view.findViewById(R.id.keyword);
                String key = keyword.getText().toString();
                TextView valid1 = (TextView) view.findViewById(R.id.validation1);
                TextView valid2 = (TextView) view.findViewById(R.id.validation2);
                String custom = et.getText().toString();
                Spinner category = (Spinner) view.findViewById(R.id.category);
                String cate = category.getSelectedItem().toString();
                RadioButton other = (RadioButton) view.findViewById(R.id.other);
                EditText distance = (EditText) view.findViewById(R.id.distance);
                String dis = distance.getText().toString();
                if (dis.isEmpty()) {
                    dis = "undefined";
                }
                String location = custom;
                if (location.isEmpty()) {
                    location = "undefined";
                }
                boolean flag = false;
                if (key.matches("^\\s*$")) {
                    valid1.setVisibility(View.VISIBLE);
                    flag = true;
                }
                else {
                    valid1.setVisibility(View.GONE);
                }
                if (other.isChecked() && (custom.matches("^\\s*$"))) {
                    valid2.setVisibility(View.VISIBLE);
                    flag = true;
                }
                else {
                    valid2.setVisibility(View.GONE);
                }
                if (flag) {
                    Toast.makeText(context, "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }
                else {
                    TextView latitude = (TextView) view.findViewById(R.id.lat);
                    TextView longitude = (TextView) view.findViewById(R.id.lng);
                    String lat = latitude.getText().toString();
                    String lng = longitude.getText().toString();
                    final TextView test = (TextView) view.findViewById(R.id.volloyTest);
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    String url = "http://csci571-hw9.us-east-2.elasticbeanstalk.com/messages?"+"keyword="+key+"&category="+cate+"&distance="+dis+"&location="+location+"&lon="+lng+"&lat="+lat;
//                    System.out.print(url);
//                    test.setText(url);
                    final ProgressDialog pd = ProgressDialog.show(getContext(), "", "Fetching results");
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            test.setText("Response is: " + response.substring(0, 200));
                            pd.dismiss();
                            Intent myIntent = new Intent(getContext(), SearchResultsActivity.class);
                            myIntent.putExtra("key", response);
                            if (getActivity() != null) {
                                getActivity().startActivity(myIntent);
                            }
                            else {
                                Toast.makeText(getContext(), "Errors: API Failure", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            test.setText("That didn't work");
                            pd.dismiss();
                            Toast.makeText(getContext(), "Errors: Network Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.current:
                        et.setEnabled(false);
                        et.setFocusableInTouchMode(false);
                        et.setText("");
                        break;
                    case R.id.other:
                        et.setEnabled(true);
                        et.setFocusableInTouchMode(true);
                        break;
                }
            }
        });
        return view;
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
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
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
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
