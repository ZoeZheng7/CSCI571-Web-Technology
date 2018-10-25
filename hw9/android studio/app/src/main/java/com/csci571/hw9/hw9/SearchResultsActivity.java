package com.csci571.hw9.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<JSONArray> data = new ArrayList<JSONArray>();
    private int page = 0;
    private ArrayList<String> token = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else {
            Toast.makeText(this, "Error: Toolbar is null", Toast.LENGTH_SHORT).show();
        }


        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        JSONObject results;
        JSONArray res;
        try {
            results = new JSONObject(value);
            if (results.has("token")) {
                token.add(results.getString("token"));
            }
            else {
                token.add(null);
            }
//            token.add(results.getString("token"));
            res = results.getJSONArray("places");
            data.add(res);
            page = 1;
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new MyAdapter(res, this);
            mRecyclerView.setAdapter(mAdapter);
        } catch (JSONException ex) {
            Toast.makeText(this, "Error: JSON parse works incorrectly", Toast.LENGTH_SHORT).show();
        }

//        final Context context = this;
//        mAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
//           @Override
//           public void onItemClick(View view, int position) {
//               Toast.makeText(context, Integer.toString(position), Toast.LENGTH_SHORT).show();
//           }
//        });
        Button pre = (Button) findViewById(R.id.pre);
        Button next = (Button) findViewById(R.id.next);
        TextView re = (TextView) findViewById(R.id.results);
//        re.setText(Integer.toString(token.size()));
        pre.setEnabled(false);
        if (token.get(0) == null) {
            next.setEnabled(false);
        }
//        re.setText(value);
        if (data.get(0).length() == 0) {
//            re.setText("No");
            re.setVisibility(View.VISIBLE);
        }

//        TextView re = (TextView) findViewById(R.id.results);
//        re.setText(token);
    }

    public void prePage(View view) {
        page -= 1;
        mAdapter = new MyAdapter(data.get(page-1), this);
        mRecyclerView.setAdapter(mAdapter);
        Button pre = (Button) findViewById(R.id.pre);
        Button next = (Button) findViewById(R.id.next);
        if (page == 1) {
            pre.setEnabled(false);
        }
        else {
            pre.setEnabled(true);
        }
        if (token.get(page-1) == null) {
            next.setEnabled(false);
        }
        else {
            next.setEnabled(true);
        }
    }

    public void nextPage(View view) {
        page += 1;
//        final TextView re = (TextView) findViewById(R.id.test);
//        re.setText(token.get(page-1));
        final Button pre = (Button) findViewById(R.id.pre);
        final Button next = (Button) findViewById(R.id.next);
        if (page > data.size()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://csci571-hw9.us-east-2.elasticbeanstalk.com/next?"+"token="+token.get(page-2);
//                    System.out.print(url);
//            if (token.get(token.size()-1) == null) {
//                re.setText("null token");
//            }
//            else {
//                re.setText(token.get(token.size()-1));
//            }
            final Context context = this;

            final ProgressDialog pd = ProgressDialog.show(this, "", "Fetching next page");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                            test.setText("Response is: " + response.substring(0, 200));
//                    re.setText(response);
                    JSONObject results;
                    JSONArray res;
                    try {
                        results = new JSONObject(response);
                        if (results.has("token")) {
                            token.add(results.getString("token"));

                        }
                        else {
                            token.add(null);
                        }
                        res = results.getJSONArray("places");
                        data.add(res);
//                        re.setText(Integer.toString(token.size()));
//                        re.setText(token.get(token.size()-1));
                        if (page == 1) {
                            pre.setEnabled(false);
                        }
                        else {
                            pre.setEnabled(true);
                        }
                        if (token.get(page-1) == null) {
                            next.setEnabled(false);
                        }
                        else {
                            next.setEnabled(true);
                        }
                        mAdapter = new MyAdapter(res, context);
                        mRecyclerView.setAdapter(mAdapter);
                    } catch (JSONException ex) {
//                        throw new IllegalArgumentException("Unexpected parsing error", ex);
                        Toast.makeText(context, "Error: JSON parse works incorrectly", Toast.LENGTH_SHORT).show();
                    }

                    pd.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    test.setText("That didn't work");
                    pd.dismiss();
                    Toast.makeText(context, "Error: returns returned are not normal", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
        else {
            mAdapter = new MyAdapter(data.get(page-1), this);
            mRecyclerView.setAdapter(mAdapter);
            if (page == 1) {
                pre.setEnabled(false);
            }
            else {
                pre.setEnabled(true);
            }
            if (token.get(page-1) == null) {
                next.setEnabled(false);
            }
            else {
                next.setEnabled(true);
            }
        }
//        re.setText(Integer.toString(token.size()));




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
