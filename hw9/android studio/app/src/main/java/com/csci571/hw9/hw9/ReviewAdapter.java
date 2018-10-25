package com.csci571.hw9.hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private ReviewClass[] reviews;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView pic;
        public TextView name;
        public RatingBar rating;
        public TextView time;
        public TextView content;
        public LinearLayout cell;

        public ViewHolder(View v) {
            super(v);
            pic = (ImageView) v.findViewById(R.id.reviewPic);
            name = (TextView) v.findViewById(R.id.reviewName);
            rating = (RatingBar) v.findViewById(R.id.reviewRating);
            time = (TextView) v.findViewById(R.id.reviewTime);
            content = (TextView) v.findViewById(R.id.reviewContent);
            cell = (LinearLayout) v.findViewById(R.id.cell);
        }

    }

    public ReviewAdapter(Context context, ReviewClass[] reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    @NonNull
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_line, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ReviewAdapter.ViewHolder vh = new ReviewAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewClass element = reviews[position];
        if (element.getPic() != null) {
            Picasso.get().load(element.getPic()).into(holder.pic);
        }
        holder.name.setText(element.getName());
        holder.rating.setRating((float) element.getRating());
        holder.time.setText(element.getTime());
        holder.content.setText(element.getContent());
        final String url = element.getUrl();
        holder.cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                context.startActivity(viewIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }
}
