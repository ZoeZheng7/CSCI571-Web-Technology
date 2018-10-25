package com.csci571.hw9.hw9;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetail extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Fragment> list;
//    private PagerTabStrip tabStrip;
    private String[] titles = {"INFO", "PHOTOS", "MAP", "REVIEWS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Intent intent = getIntent();
        String value = intent.getStringExtra("key");
        TextView test = (TextView) findViewById(R.id.detailTest);
        test.setText(value);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        tabStrip = (PagerTabStrip) findViewById(R.id.pagerTab);
//        tabStrip.setBackgroundColor(R.color.colorPrimary);
//        tabStrip.setDrawFullUnderline(true);
        list = new ArrayList<Fragment>();
        list.add(new Info());
        list.add(new Photos());
        list.add(new Map());
        list.add(new Reviews());
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        DetailAdapter adapter = new DetailAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private class DetailAdapter extends FragmentPagerAdapter {

        public DetailAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }






}
