package com.csci571.hw9.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import static java.lang.reflect.Modifier.TRANSIENT;
import static java.lang.reflect.Modifier.classModifiers;

//import static com.csci571.hw9.hw9.MainActivity.loveIds;
//import static com.csci571.hw9.hw9.MainActivity.loves;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private JSONArray mDataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView cate;
        public TextView nam;
        public TextView addr;
        public ImageButton favor;
        public LinearLayout nameAddr;
        public ViewHolder(View v) {
            super(v);
            cate = (ImageView) v.findViewById(R.id.cateImage);
            nam = (TextView) v.findViewById(R.id.name);
            addr = (TextView) v.findViewById(R.id.address);
            favor = (ImageButton) v.findViewById(R.id.favorites);
            nameAddr = (LinearLayout) v.findViewById(R.id.nameAddr);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(JSONArray myDataset, Context context) {
        this.context = context;
        mDataset = myDataset;
    }

//    private OnItemClickListener mOnItemClickListener = null;
//
//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.mOnItemClickListener = listener;
//    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_line_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnItemClickListener != null) {
//                    mOnItemClickListener.onItemClick(v, (int)v.getTag());
//                }
//            }
//        });


        return vh;
    }

//    @Override
//    public void onClick(View v) {
//        if (mOnItemClickListener != null) {
//            mOnItemClickListener.onItemClick(v, (int)v.getTag());
//        }
//    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder Holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final ViewHolder holder = Holder;

        try {
            JSONObject element = mDataset.getJSONObject(position);
            final String icon = element.getString("icon");
            Picasso.get().load(icon).into(holder.cate);
//            holder.cate.setImageURI(Uri.parse(element.getString("icon")));
            final String name = element.getString("name");
            final String vicinity = element.getString("vicinity");
            holder.nam.setText(name);
            final String id = element.getString("place_id");
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = mPrefs.getString("loveIds", null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> fetch = gson.fromJson(json, type);
            final ImageButton favorTmp = holder.favor;
            if (fetch != null && fetch.contains(id)) {
                holder.favor.setImageResource(R.drawable.heart_fill_red);
            }
            else {
                holder.favor.setImageResource(R.drawable.heart_outline_black);
            }
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
                            Toast.makeText(context, "Error: Network Error", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "Error: Network Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                    queue.add(stringRequest);
                }
            });
            holder.favor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Gson gson = new Gson();
                    String json = mPrefs.getString("loveIds", null);
                    Type type = new TypeToken<ArrayList<String>>() {}.getType();
                    ArrayList<String> fetch = gson.fromJson(json, type);
                    if (fetch != null && fetch.contains(id)) {
                        removeF(name, id, favorTmp);

                    } else {
                        addF(name, vicinity, icon, id, favorTmp);
                    }
                }
            });
            holder.addr.setText(vicinity);

        } catch (JSONException ex) {
            Toast.makeText(context, "Error: JSON parse works incorrectly", Toast.LENGTH_SHORT).show();
        }
        holder.itemView.setTag(position);

    }

    private void removeF(String name, String id, ImageButton favorTmp) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("loveIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        fetch.remove(id);
        SharedPreferences.Editor editor = mPrefs.edit();
        gson = new Gson();
        json = gson.toJson(fetch);
        editor.putString("loveIds", json);
        editor.apply();

        editor.remove(id);
        editor.apply();
        favorTmp.setImageResource(R.drawable.heart_outline_black);
        Toast.makeText(context, name+" was removed from favorites", Toast.LENGTH_SHORT).show();
        Tab2.update();
    }

    private void addF(String name, String vicinity, String  icon, String id, ImageButton favorTmp) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("loveIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        if (fetch == null) {
            fetch = new ArrayList<String>();
        }
        fetch.add(id);
        SharedPreferences.Editor editor = mPrefs.edit();
        gson = new Gson();
        json = gson.toJson(fetch);
        editor.putString("loveIds", json);
        editor.apply();

        Place tmp = new Place(name, vicinity, icon, id);
        gson = new Gson();
//                        final Gson gsonTmp = new GsonBuilder()
//                                .excludeFieldsWithoutExposeAnnotation()
//                                .excludeFieldsWithModifiers(TRANSIENT) // STATIC|TRANSIENT in the default configuration
//                                .create();
//                        Gson gsonTmp = new GsonBuilder()
////                                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
////                                .serializeNulls()
////                                .create();
        Gson gsonTmp = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        final String jsonTmp = gsonTmp.toJson(tmp);
//                        json = gson.toJson(Place.class);
        editor.putString(id, jsonTmp);
        editor.commit();
//                        loves.add(tmp);
        favorTmp.setImageResource(R.drawable.heart_fill_red);
        Toast.makeText(context, name+" was added to favorites", Toast.LENGTH_SHORT).show();
        Tab2.update();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.length() : 0;
    }
}
