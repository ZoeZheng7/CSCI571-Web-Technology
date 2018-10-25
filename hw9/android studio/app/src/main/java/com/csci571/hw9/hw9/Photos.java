package com.csci571.hw9.hw9;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class Photos extends Fragment {

    protected GeoDataClient mGeoDataClient;

//    private OnFragmentInteractionListener mListener;

    private View view;

    private Context context;
    private PlacePhotoMetadataBuffer photoMetadataBuffer;

    public Photos() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_photos, container, false);
        context = getContext();
        if (getArguments() != null) {
            String id = getArguments().getString("id");
            getPhotos(id);
        }
        else {
            Toast.makeText(context, "Error: No arguments in bundle passed to this fragment", Toast.LENGTH_SHORT).show();
        }

//        Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();

//        TextView test = (TextView) view.findViewById(R.id.photoTest);
//        test.setText(id);
        return view;
    }

    // Request photos and metadata for the specified place.
    private void getPhotos(String id) {
        final String placeId = id;
        mGeoDataClient = Places.getGeoDataClient(context, null);
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                try {
                    PlacePhotoMetadataResponse photos = task.getResult();
                    photoMetadataBuffer = photos.getPhotoMetadata();
                    // Get the first photo in the list.
                    final LinearLayout group = (LinearLayout) view.findViewById(R.id.viewGroup);
                    int size = 0;
                    for (PlacePhotoMetadata photoMetadata : photoMetadataBuffer) {
                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                        size++;
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                PlacePhotoResponse photo = task.getResult();
                                Bitmap bitmap = photo.getBitmap();
                                int bmpWidth = bitmap.getWidth();
                                int bmpHeight = bitmap.getHeight();
                                int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                                int imageHeight = screenWidth*bmpHeight/bmpWidth;
                                bitmap = Bitmap.createScaledBitmap(bitmap, screenWidth, imageHeight, true);
                                ImageView view = new ImageView(context);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(20, 0, 20, 0);
                                view.setLayoutParams(params);
//                            ImageView view1 = (ImageView) view.findViewById(R.id.view1);
                                view.setImageBitmap(bitmap);
                                group.addView(view);
                            }
                        });
//                    photoMetadataBuffer.release();
                    }
                    if (size == 0) {
                        TextView no = (TextView) getActivity().findViewById(R.id.noPhoto);
                        no.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error: Network Failure", Toast.LENGTH_SHORT).show();
                }
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).

//                PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                // Get the attribution text.
//                CharSequence attribution = photoMetadata.getAttributions();
                // Get a full-size bitmap for the photo.

            }
        });
    }

    @Override
    public void onDestroy() {
        if (photoMetadataBuffer != null) {
            photoMetadataBuffer.release();
        }
        super.onDestroy();
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
