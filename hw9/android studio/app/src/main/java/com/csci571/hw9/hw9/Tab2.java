package com.csci571.hw9.hw9;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
//import static com.csci571.hw9.hw9.MainActivity.loveIds;
//import static com.csci571.hw9.hw9.MainActivity.loves;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class Tab2 extends Fragment {

    static private RecyclerView mRecyclerView;
    static private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static private TextView no;
    static private SharedPreferences mPrefs;
    static private Context context;

//    static SharedPreferences mPrefs = getSharedPreferences("loveIds", MODE_PRIVATE);



    //    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    //
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
//
//    public Tab2() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment Tab2.
//     */
//    //
//    public static Tab2 newInstance(String param1, String param2) {
//        Tab2 fragment = new Tab2();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getContext();
        final View view = inflater.inflate(R.layout.fragment_tab2, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favorites_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new FavorAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        no = (TextView) view.findViewById(R.id.no);
//        test = (TextView) view.findViewById(R.id.testFavor);
//        mPrefs = getContext().getSharedPreferences("loveIds", MODE_PRIVATE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("loveIds", null);
//        Set<String> fetch = mPrefs.getStringSet("List", new HashSet<String>());
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        if (fetch == null || fetch.size() == 0) {
            no.setVisibility(View.VISIBLE);
//            no.setText("get into here");
        }
        else {
            no.setVisibility(View.INVISIBLE);
        }
        return view;
//        return inflater.inflate(R.layout.fragment_tab2, container, false);
    }

    static protected void update() {
        mAdapter = new FavorAdapter(context);
        mRecyclerView.setAdapter(mAdapter);
//        no = (TextView) getView().findViewById(R.id.no);
//        Set<String> fetch = mPrefs.getStringSet("List", new HashSet<String>());
        Gson gson = new Gson();
        String json = mPrefs.getString("loveIds", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> fetch = gson.fromJson(json, type);
        if (fetch == null || fetch.size() == 0) {
            no.setVisibility(View.VISIBLE);
//            no.setText("get into here");
        }
        else {
            no.setVisibility(View.INVISIBLE);
        }
//        test.setText(Integer.toString(loves.size()));
    }


//
//
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
//
//        void onFragmentInteraction(Uri uri);
//    }
}
