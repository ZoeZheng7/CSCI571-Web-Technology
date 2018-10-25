package com.csci571.hw9.hw9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;

public class detail extends AppCompatActivity {

    private Bundle bundle1 = new Bundle();
    private Bundle bundle2 = new Bundle();
    private Bundle bundle3 = new Bundle();
    private Bundle bundle4 = new Bundle();

    private String name;
    private String placeId;
    private String vicinity;
    private String icon;
    private String website;
    private String address;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        TextView tab1 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom, null);
        tab1.setText("INFO");
        tab1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tab1);
        TextView tab2 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom, null);
        tab2.setText("PHOTOS");
        tab2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photos, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tab2);
        TextView tab3 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom, null);
        tab3.setText("MAP");
        tab3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.map, 0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tab3);
        TextView tab4 = (TextView) LayoutInflater.from(this).inflate(R.layout.custom, null);
        tab4.setText("REVIEWS");
        tab4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.review, 0, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tab4);

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.colorPrimaryLight));
            drawable.setSize(5, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
//        TextView test = (TextView) findViewById(R.id.detailTest);
//        test.setText(value);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        JSONObject response;
        JSONObject res;

//        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();


        JSONObject location;
        JSONArray googleReviews = new JSONArray();


        try {
            response = new JSONObject(value);
            try {
                res = response.getJSONObject("result");
                name = res.getString("name");
                getSupportActionBar().setTitle(name);
                location = res.getJSONObject("geometry").getJSONObject("location");
                placeId = res.getString("place_id");
                vicinity = res.getString("vicinity");
                icon = res.getString("icon");
                address = res.getString("formatted_address" );
                if (res.has("website")) {
                    website = res.getString("website");
                }
                else {
                    website = res.getString("url");
                }
                if (res.has("reviews")) {
                    googleReviews = res.getJSONArray("reviews");
                }

                bundle1.putString("res", res.toString());
                bundle2.putString("id", placeId);
                bundle3.putString("location", location.toString());
                bundle4.putString("googleReviews", googleReviews.toString());
                bundle4.putString("name", name);
                bundle4.putString("address", address);
//
            }catch (Exception ex) {
//                throw new IllegalArgumentException("Unexpected parsing error", ex);
                Toast.makeText(this, "Error: results returned are not normal", Toast.LENGTH_SHORT).show();

            }


        } catch (Exception ex) {
//            throw new IllegalArgumentException("Unexpected parsing error", ex);
            Toast.makeText(this, "Error: results returned are not normal", Toast.LENGTH_SHORT).show();

        }








//
//        phone = res.getString("international_phone_number");
//        price = res.getInt("price_level");
//        rating = res.getInt("rating");
//        url = res.getString("url");
//        website = res.getString("website");
//        googleReviews = res.getJSONArray("reviews");








    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = mPrefs.getString("loveIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        if (fetch != null && fetch.contains(placeId)) {
            menu.findItem(R.id.favor).setIcon(R.drawable.heart_fill_white);
        }
        else {
            menu.findItem(R.id.favor).setIcon(R.drawable.heart_outline_white);
        }
        return true;
    }

    private void removeF(String name, String id, MenuItem favorTmp) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
        favorTmp.setIcon(R.drawable.heart_outline_white);
        Toast.makeText(this, name+" was removed from favorites", Toast.LENGTH_SHORT).show();
        Tab2.update();
    }

    private void addF(String name, String vicinity, String  icon, String id, MenuItem favorTmp) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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
//        gson = new Gson();
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
        favorTmp.setIcon(R.drawable.heart_fill_white);
        Toast.makeText(this, name+" was added to favorites", Toast.LENGTH_SHORT).show();
        Tab2.update();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.share) {
//            Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.google.com"));
//            startActivity(viewIntent);
            try {
                String url = "https://twitter.com/intent/tweet?text=Check%20out%20"+URLEncoder.encode(name, "UTF-8")+"%20located%20at%20"+URLEncoder.encode(vicinity, "UTF-8")+".%20Website:%20&url="+URLEncoder.encode(website, "UTF-8")+"&hashtags=TravelAndEntertainmentSearch";
//                Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                startActivity(viewIntent);
            }catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Unexpected parsing error", e);
            }
        }

        if (id == R.id.favor) {
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            Gson gson = new Gson();
            String json = mPrefs.getString("loveIds", null);
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> fetch = gson.fromJson(json, type);
            if (fetch != null && fetch.contains(placeId)) {
                removeF(name, placeId, item);

            } else {
                addF(name, vicinity, icon, placeId, item);
            }
        }


        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Info info = new Info();
                    info.setArguments(bundle1);
                    return info;
                case 1:
                    Photos photos = new Photos();
                    photos.setArguments(bundle2);
                    return photos;
                case 2:
                    Map map = new Map();
                    map.setArguments(bundle3);
                    return map;
                case 3:
                    Reviews reviews = new Reviews();
                    reviews.setArguments(bundle4);
                    return reviews;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }
    }
}
