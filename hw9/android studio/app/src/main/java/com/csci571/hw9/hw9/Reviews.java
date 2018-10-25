package com.csci571.hw9.hw9;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class Reviews extends Fragment {

    private ReviewClass[] gReviews;
    private ReviewClass[] yReviews;
    private ReviewClass[] selectedReviews;
    private ReviewClass[] defaultReviews;
    private String name;
    private String address;
    private int size_google;
    private int size_yelp;
    private int size;
    private int mode;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private View view;
    private Context context;

    public Reviews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment Reviews.
     */
//    public static Reviews newInstance(String param1, String param2) {
//        Reviews fragment = new Reviews();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        JSONArray googleReviews;
        if (getArguments() != null) {
            String gr = getArguments().getString("googleReviews");
            name = getArguments().getString("name");
            address = getArguments().getString("address");
            String country = "";
            String city = "";
            String state = "";
            String ad1 = "";
            String ad2 = "";
            if (address != null) {
                String[] adds = address.split(", ");
                int len = adds.length;
                try {
                    country = "US";
                    city = adds[len-3];
                    ad1 = adds[0];
                    ad2 = len == 4 ? "" : adds[1];
                    state = adds[len-2].split(" ")[0];
                } catch (Exception e) {
                    Toast.makeText(context, "Error: Corresponding address does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            RequestQueue queue = Volley.newRequestQueue(context);
            String url_yelp = "http://csci571-hw9.us-east-2.elasticbeanstalk.com/yelp?"+"name="+name+"&address1="+ad1+"&address2="+ad2+"&city="+city+"&state="+state+"&country="+country;
//            final ProgressDialog pd = ProgressDialog.show(getContext(), "", "Fetching reviews");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_yelp, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
//                            test.setText("Response is: " + response.substring(0, 200));
//                    pd.dismiss();
//                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                    if (response.equals("error") || response.equals("[]")) {
                        size_yelp = 0;
                        yReviews = new ReviewClass[size_yelp];
                    }
                    else {
                        try {
                            JSONArray yelpReviews = new JSONArray(response);
                            size_yelp = yelpReviews.length();
                            yReviews = new ReviewClass[size_yelp];
                            for (int j = 0; j < size_yelp; j++) {
                                JSONObject element = yelpReviews.getJSONObject(j);
                                JSONObject user = element.has("user") ? element.getJSONObject("user") : null;
                                String elementName = user == null ? "" : user.getString("name");
                                String elementPic = user == null ? "" : user.getString("image_url");
                                int elementRating = element.has("rating") ? element.getInt("rating") : 0;
                                String elementContent = element.has("text") ? element.getString("text") : "";
                                String elementUrl = element.has("url") ? element.getString("url") : "";
                                String elementTime = element.has("time_created") ? element.getString("time_created") : "No created time";
                                ReviewClass review = new ReviewClass(elementPic, elementName, elementRating, elementTime, elementContent, elementUrl);
                                yReviews[j] = review;
                            }
                        } catch (JSONException ex) {
                            Toast.makeText(context, "Error: JSON parse works incorrectly", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                            test.setText("That didn't work");
//                    pd.dismiss();
                    Toast.makeText(context, "Error: HTTP request error", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
            try {
                googleReviews = new JSONArray(gr);
                size_google = googleReviews.length();
                gReviews = new ReviewClass[size_google];
                size = size_google;
                for (int i = 0; i < size_google; i++) {
                    JSONObject element = googleReviews.getJSONObject(i);
                    String elementName = element.has("author_name") ? element.getString("author_name") : "";
                    String elementPic = element.has("profile_photo_url") ? element.getString("profile_photo_url") : "";
                    int elementRating = element.has("rating") ? element.getInt("rating") : 0;
                    String elementContent = element.has("text") ? element.getString("text") : "";
                    String elementUrl = element.has("author_name") ? element.getString("author_url") : "";
                    long time = element.has("time") ? 1000*element.getLong("time") : 0;
//                    long time = 1000*element.getLong("time");
                    Date date = new Date(time);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String elementTime = df.format(date);
                    ReviewClass review = new ReviewClass(elementPic, elementName, elementRating, elementTime, elementContent, elementUrl);
                    gReviews[i] = review;
                }
                mode = 0;
                defaultReviews = new ReviewClass[size_google];
                System.arraycopy(gReviews, 0, defaultReviews, 0, size_google);
                selectedReviews = new ReviewClass[size_google];
                System.arraycopy(defaultReviews, 0, selectedReviews, 0, size_google);
            } catch (JSONException ex) {
//                throw new IllegalArgumentException("Unexpected parsing error", ex);
                Toast.makeText(context, "Error: JSON parse works incorrectly", Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reviews, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.reviewList);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
//        gReviews = new ReviewClass[2];
//        ReviewClass tmp1 = new ReviewClass("https://s3-media3.fl.yelpcdn.com/photo/iwoAD12zkONZxJ94ChAaMg/o.jpg", "Ella A.", 5, "2016-08-29 00:41:13", "Went back again to this place since the last time i visited the bay area 5 months ago, and nothing has changed. Still the sketchy Mission, Still the cashier...", "https://www.yelp.com/biz/la-palma-mexicatessen-san-francisco?hrid=hp8hAJ-AnlpqxCCu7kyCWA&adjust_creative=0sidDfoTIHle5vvHEBvF0w&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_reviews&utm_source=0sidDfoTIHle5vvHEBvF0w");
//        ReviewClass tmp2 = new ReviewClass(null, "Yanni L.", 4, "2016-09-28 08:55:29", "The \"restaurant\" is inside a small deli so there is no sit down area. Just grab and go.\n\nInside, they sell individually packaged ingredients so that you can...", "https://www.yelp.com/biz/la-palma-mexicatessen-san-francisco?hrid=fj87uymFDJbq0Cy5hXTHIA&adjust_creative=0sidDfoTIHle5vvHEBvF0w&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_reviews&utm_source=0sidDfoTIHle5vvHEBvF0w");
//        gReviews[0] = tmp1;
//        gReviews[1] = tmp2;

        mAdapter = new ReviewAdapter(getContext(), selectedReviews);
        mRecyclerView.setAdapter(mAdapter);
        checkNoReview();
        LinearLayout spinners = (LinearLayout) view.findViewById(R.id.spinners);
        Spinner reviewSource = (Spinner) view.findViewById(R.id.reviewSource);
        Spinner reviewSort = (Spinner) view.findViewById(R.id.reviewSort);
//        final int sort = reviewSort.getSelectedItemPosition();
        reviewSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    size = size_google;
                    defaultReviews = new ReviewClass[size];
                    selectedReviews = new ReviewClass[size];
                    if (gReviews != null) {
                        System.arraycopy(gReviews, 0, defaultReviews, 0, size);
                    }
                    update();
                }
                else {
                    size =size_yelp;
                    defaultReviews = new ReviewClass[size];
                    selectedReviews = new ReviewClass[size];
                    if (yReviews != null) {
                        System.arraycopy(yReviews, 0, defaultReviews, 0, size);
                    }
                    update();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        reviewSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                mode = position;
                update();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Toast.makeText(getContext(), "noting", Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }

    private void update() {
        checkNoReview();
        switch (mode) {
            case 0:
                defaultOrder();
                break;
            case 1:
                highestRating();
                break;
            case 2:
                lowestRating();
                break;
            case 3:
                mostRecent();
                break;
            case 4:
                leastRecent();
                break;
        }
    }


    private void defaultOrder() {
//        Toast.makeText(getContext(), "default", Toast.LENGTH_SHORT).show();

        System.arraycopy(defaultReviews, 0, selectedReviews, 0, size);
        mAdapter = new ReviewAdapter(getContext(), selectedReviews);
        mRecyclerView.setAdapter(mAdapter);
    }

    class HighestRatingComparator implements Comparator<ReviewClass> {
        @Override
        public int compare(ReviewClass a, ReviewClass b) {
            return b.getRating() - a.getRating();
        }
    }

    private void highestRating() {
//        Toast.makeText(getContext(), "highest rating", Toast.LENGTH_SHORT).show();

        System.arraycopy(defaultReviews, 0, selectedReviews, 0, size);
        Arrays.sort(selectedReviews, new HighestRatingComparator());
        mAdapter = new ReviewAdapter(getContext(), selectedReviews);
        mRecyclerView.setAdapter(mAdapter);
    }

    class LowestRatingComparator implements Comparator<ReviewClass> {
        @Override
        public int compare(ReviewClass a, ReviewClass b) {
            return a.getRating() - b.getRating();
        }
    }

    private void lowestRating() {
//        Toast.makeText(getContext(), "lowest rating", Toast.LENGTH_SHORT).show();

        System.arraycopy(defaultReviews, 0, selectedReviews, 0, size);
        Arrays.sort(selectedReviews, new LowestRatingComparator());
        mAdapter = new ReviewAdapter(getContext(), selectedReviews);
        mRecyclerView.setAdapter(mAdapter);
    }

    class MostRecentComparator implements Comparator<ReviewClass> {
        @Override
        public int compare(ReviewClass a, ReviewClass b) {
            return (b.getTime()).compareTo(a.getTime());
        }
    }

    private void mostRecent() {
//        Toast.makeText(getContext(), "most recent", Toast.LENGTH_SHORT).show();

        System.arraycopy(defaultReviews, 0, selectedReviews, 0, size);
        Arrays.sort(selectedReviews, new MostRecentComparator());
        mAdapter = new ReviewAdapter(getContext(), selectedReviews);
        mRecyclerView.setAdapter(mAdapter);
    }

    class LeastRecentComparator implements Comparator<ReviewClass> {
        @Override
        public int compare(ReviewClass a, ReviewClass b) {
            return (a.getTime()).compareTo(b.getTime());
        }
    }

    private void leastRecent() {
//        Toast.makeText(getContext(), "least recent", Toast.LENGTH_SHORT).show();

        System.arraycopy(defaultReviews, 0, selectedReviews, 0, size);
        Arrays.sort(selectedReviews, new LeastRecentComparator());
//        TextView noReviews = (TextView) view.findViewById(R.id.noReviews);
//        noReviews.setVisibility(View.VISIBLE);
        mAdapter = new ReviewAdapter(getContext(), selectedReviews);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void checkNoReview() {
        TextView noReviews = (TextView) view.findViewById(R.id.noReviews);
        if (defaultReviews.length == 0) {
            noReviews.setVisibility(View.VISIBLE);
        }
        else {
            noReviews.setVisibility(View.GONE);
        }
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
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

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
//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(Uri uri);
//    }
}
