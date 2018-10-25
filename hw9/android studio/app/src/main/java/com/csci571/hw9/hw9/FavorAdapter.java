package com.csci571.hw9.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;

//import static com.csci571.hw9.hw9.MainActivity.loveIds;
//import static com.csci571.hw9.hw9.MainActivity.loves;
import static com.csci571.hw9.hw9.Tab2.update;
import static java.lang.reflect.Modifier.TRANSIENT;

public class FavorAdapter extends RecyclerView.Adapter<FavorAdapter.ViewHolder> {

    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView cate;
        private TextView nam;
        private TextView addr;
        public ImageButton favor;
        public LinearLayout nameAddr;
        private ViewHolder(View v) {
            super(v);
            cate = (ImageView) v.findViewById(R.id.cateImage);
            nam = (TextView) v.findViewById(R.id.name);
            addr = (TextView) v.findViewById(R.id.address);
            favor = (ImageButton) v.findViewById(R.id.favorites);
            nameAddr = (LinearLayout) v.findViewById(R.id.nameAddr);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FavorAdapter(Context context) {
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public FavorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_line_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//            Context context = holder.nam.getContext();
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = mPrefs.getString("loveIds", null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> fetch = gson.fromJson(json, type);
//            final Gson gsonTmp = new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation()
//                .excludeFieldsWithModifiers(TRANSIENT) // STATIC|TRANSIENT in the default configuration
//                .create();
//            Gson gsonTmp = new GsonBuilder()
//                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
//                .serializeNulls()
//                .create();
        Gson gsonTmp = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        if (fetch != null) {
            String placeId = fetch.get(position);
            String eleJson = mPrefs.getString(placeId, null);
            Place element = gsonTmp.fromJson(eleJson, Place.class);
            if (element != null) {
                final String name = element.getName();
                final String vicinity = element.getVicinity();
                final String icon = element.getIcon();
                final String id = element.getId();
//                Log.d("eleName", name);
//                Log.d("eleAddr", vicinity);
//                Log.d("eleIcon", icon);
//                Log.d("eleId", id);
                Picasso.get().load(icon).into(holder.cate);
//            holder.cate.setImageURI(Uri.parse(element.getString("icon")));
                holder.nam.setText(name);
                holder.addr.setText(vicinity);
                holder.favor.setImageResource(R.drawable.heart_fill_red);
                holder.favor.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                        Gson gson = new Gson();
                        String json = mPrefs.getString("loveIds", null);
                        Type type = new TypeToken<ArrayList<String>>() {}.getType();
                        ArrayList<String> fetch = gson.fromJson(json, type);
                        if (fetch != null) {
                            fetch.remove(id);
                        }
                        else {
                            Toast.makeText(context, "Errors: Favorite list is already empty", Toast.LENGTH_SHORT).show();
                        }
                        SharedPreferences.Editor editor = mPrefs.edit();
                        gson = new Gson();
                        json = gson.toJson(fetch);
                        editor.putString("loveIds", json);
                        editor.apply();

                        editor.remove(id);
                        editor.apply();
//                    Place tmp = new Place(name, vicinity, icon, id);
//                    loves.remove(tmp);
                        Toast.makeText(context, name+" was removed from favorites", Toast.LENGTH_SHORT).show();
                        Tab2.update();
                    }
                });
                holder.nameAddr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url = "http://csci571-hw9.us-east-2.elasticbeanstalk.com/details?"+"id="+id;
//                    System.out.print(url);
//                    test.setText(url);
                        final ProgressDialog pd = ProgressDialog.show(context, "", "Fetching details");
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                            test.setText("Response is: " + response.substring(0, 200));
                                pd.dismiss();
                                Intent myIntent = new Intent(context, detail.class);
                                myIntent.putExtra("key", response);
                                context.startActivity(myIntent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Toast.makeText(context, "Errors: The results returned are not correct", Toast.LENGTH_SHORT).show();
                            }
                        });
                        queue.add(stringRequest);
                    }
                });
                holder.cate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url = "http://csci571-hw9.us-east-2.elasticbeanstalk.com/details?"+"id="+id;
//                    System.out.print(url);
//                    test.setText(url);
                        final ProgressDialog pd = ProgressDialog.show(context, "", "Fetching details");
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                            test.setText("Response is: " + response.substring(0, 200));
                                pd.dismiss();
                                Intent myIntent = new Intent(context, detail.class);
                                myIntent.putExtra("key", response);
                                context.startActivity(myIntent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                pd.dismiss();
                                Toast.makeText(context, "Errors: The results returned are not correct", Toast.LENGTH_SHORT).show();
                            }
                        });
                        queue.add(stringRequest);
                    }
                });
            }
            else {
                Toast.makeText(context, "Errors: Place is null", Toast.LENGTH_SHORT).show();

            }
        }



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("loveIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        return fetch == null ? 0 : fetch.size();
    }
}
