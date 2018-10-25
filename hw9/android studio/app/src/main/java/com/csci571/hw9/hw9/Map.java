package com.csci571.hw9.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;


public class Map extends Fragment {

    private JSONObject location;
    private MapView mapView;
    private GoogleMap googleMap;
    private double lat;
    private double lng;
//    private String mode;
    private View view;
    private PolylineOptions options;
    private Polyline polyline;
    private LatLngBounds bounds;
    private LatLngBounds.Builder builder;
    private Marker startMarker;
    private String mode = "driving";
    private String startPoint;
    private Context context;


    public Map() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String loc = getArguments().getString("location");
            try {
                location = new JSONObject(loc);
//                Toast.makeText(getContext(), loc, Toast.LENGTH_SHORT).show();
                lat = location.getDouble("lat");
                lng = location.getDouble("lng");
            } catch (JSONException ex) {
                throw new IllegalArgumentException("Unexpected parsing error", ex);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);
        context = getContext();
        AutoCompleteTextView from = (AutoCompleteTextView) view.findViewById(R.id.fromAuto);
        CustomAutoCompleteAdapter customAdapter = new CustomAutoCompleteAdapter(getContext());
        from.setAdapter(customAdapter);
        from.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), "clickAutoComplete", Toast.LENGTH_SHORT).show();
                startPoint = (String) parent.getItemAtPosition(position);
                getDirection();
            }
        });
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        if (getActivity() != null) {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        else {
            Toast.makeText(context, "Error: API Failure", Toast.LENGTH_SHORT).show();
        }



        Spinner spin = (Spinner) view.findViewById(R.id.travelMode);
//        EditText from = (EditText) view.findViewById(R.id.from);
//        from.setText("Lorenzo");
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View mview, int position, long id) {
                switch (position) {
                    case 0:
                        mode = "driving";
                        break;
                    case 1:
                        mode = "bicycling";
                        break;
                    case 2:
                        mode = "transit";
                        break;
                    case 3:
                        mode = "walking";
                        break;
                }
                getDirection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "nothing", Toast.LENGTH_SHORT).show();
            }
        });


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
//                googleMap.setMyLocationEnabled(true);
//                bounds = new google.maps.LatLngBounds();
                LatLng loc = new LatLng(lat, lng);
                googleMap.addMarker(new MarkerOptions().position(loc));
                CameraPosition cp = new CameraPosition.Builder().target(loc).zoom(15).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void getDirection() {
        if (startPoint != null && startPoint.length() != 0) {
            options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
            if (polyline != null) {
                polyline.remove();
            }
//                EditText from = (EditText) view.findViewById(R.id.from);
//                AutoCompleteTextView from = (AutoCompleteTextView) view.findViewById(R.id.fromAuto);
            String start = startPoint;
            String url = "http://csci571-hw9.us-east-2.elasticbeanstalk.com/map?"+"start="+start+"&endLat="+lat+"&endLng="+lng+"&mode="+mode;
            RequestQueue queue = Volley.newRequestQueue(context);
//                final ProgressDialog pd = ProgressDialog.show(getContext(), "", "Fetching the route");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                            test.setText("Response is: " + response.substring(0, 200));
//                        pd.dismiss();
//                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();

                    try {
                        JSONObject res = new JSONObject(response);
                        JSONArray steps = res.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
//                            Toast.makeText(getContext(), Integer.toString(steps.length()), Toast.LENGTH_SHORT).show();
                        int length = steps.length();
//                            LatLng[] routes = new LatLng[length];
//                            ArrayList<LatLng> routes = new ArrayList<LatLng>();
                        if (startMarker != null) {
                            startMarker.remove();
                        }
                        builder = new LatLngBounds.Builder();
                        for (int i = 0; i < length; i++) {
                            JSONObject ele = steps.getJSONObject(i);
                            List<LatLng> decoded = PolyUtil.decode(ele.getJSONObject("polyline").getString("points"));
                            if (i == 0) {
                                LatLng loc = decoded.get(0);
                                startMarker = googleMap.addMarker(new MarkerOptions().position(loc));
                            }
                            options.addAll(decoded);
                            for (LatLng e : decoded) {
                                builder.include(e);
                            }
//                                if (i == 0) {
//                                    JSONObject startLoc = ele.getJSONObject("start_location");
//                                    options.add(new LatLng(startLoc.getDouble("lat"), startLoc.getDouble("lng")));
//                                }
//                                JSONObject endLoc = ele.getJSONObject("end_location");
//                                options.add(new LatLng(endLoc.getDouble("lat"), endLoc.getDouble("lng")));
                        }
                        polyline = googleMap.addPolyline(options);
                        bounds = builder.build();
                        int padding = 50;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        googleMap.moveCamera(cu);
//                            String overview = res.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
//                            Toast.makeText(getContext(), overview, Toast.LENGTH_SHORT).show();
                    } catch (JSONException ex) {
//                        throw new IllegalArgumentException("Unexpected parsing error", ex);
                        Toast.makeText(context, "Error: JSON parse works incorrectly, or corresponding route does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                            test.setText("That didn't work");
//                        pd.dismiss();
                    Toast.makeText(context, "Error: HTTP request error", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }

    }

}




