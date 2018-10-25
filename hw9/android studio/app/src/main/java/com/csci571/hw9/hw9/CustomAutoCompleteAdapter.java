package com.csci571.hw9.hw9;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CustomAutoCompleteAdapter extends ArrayAdapter {

    private Context context;
    private List<String> dataList;
    private GoogleApiClient googleApiClient;
    private GeoDataClient geoDataClient;
    public static final String TAG = "CustomAutoCompAdapter";

    public CustomAutoCompleteAdapter(Context context) {
        super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        this.context = context;
        geoDataClient = Places.getGeoDataClient(context, null);
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        if (view == null) {
            if (position != (dataList.size()-1)){
                view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
                TextView textOne = view.findViewById(android.R.id.text1);
                textOne.setText(dataList.get(position));
            }
            else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.google_logo, parent, false);
            }
        }



        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new CustomAutoCompleteAdapter.CustomAutoCompleteFilter();
    }

    public class CustomAutoCompleteFilter extends Filter {
        private Object lock1 = new Object();
        private Object lock2 = new Object();
        private boolean placeResults = false;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            placeResults = false;
            final List<String> placeList = new ArrayList<String>();

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock1) {
                    results.values = new ArrayList<String>();
                    results.count = 0;
                }
            }
            else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();
                Task<AutocompletePredictionBufferResponse> task = getAutoCompletePlaces(searchStrLowerCase);
                task.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Auto complete prediction successful");
                            AutocompletePredictionBufferResponse predictions = task.getResult();
                            String autoPlace;
                            for (AutocompletePrediction prediction : predictions) {
                                autoPlace = prediction.getFullText(null).toString();
                                placeList.add(autoPlace);
                            }
                            predictions.release();
                            Log.d(TAG, "Auto complete predictions size " + placeList.size());
                        }
                        else {
                            Log.d(TAG, "Auto complete prediction unsuccessful");
                        }
                        placeResults = true;
                        synchronized (lock2) {
                            lock2.notifyAll();
                        }
                    }
                });

                while (!placeResults) {
                    synchronized (lock2) {
                        try {
                            lock2.wait();
                        } catch (InterruptedException e) {
                            Toast.makeText(context, "Error: Interrupted Exception", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                placeList.add("footer");
                results.values = placeList;
                results.count = placeList.size();
                Log.d(TAG, "Autocomplete predictions size after wait" + results.count);
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<String>) results.values;
            }
            else {
                dataList = null;
            }
            if (results.count == 0) {
                notifyDataSetChanged();
            }
            else {
                notifyDataSetInvalidated();
            }
        }

        private Task<AutocompletePredictionBufferResponse> getAutoCompletePlaces(String query) {
//        LatLngBounds defaultBound = new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 180));
            Task<AutocompletePredictionBufferResponse> results = geoDataClient.getAutocompletePredictions(query.toString(), null, null);
            return results;
        }
    }


}
