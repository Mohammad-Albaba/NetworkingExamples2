package com.example.networkingexamples2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {
    public interface OnLoadMoreLister{
        void onLoadMore(int page);
    }
    private OnLoadMoreLister onLoadMoreLister;
    private final List<Photo> mItems;
    private int page;
    private boolean isLoading;
    private boolean isLastPage;

    public PhotosAdapter(List<Photo> items, OnLoadMoreLister onLoadMoreLister) {
        this.onLoadMoreLister = onLoadMoreLister;
        mItems = items;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.bind(mItems.get(position));
        if (position == mItems.size() - 1){
            if (!isLoading && !isLastPage){
                isLoading = true;
                onLoadMoreLister.onLoadMore(++page);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbImageView;
        private TextView dateTextView;
        private TextView titleTextView;
        private TextView descTextView;


        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbImageView = itemView.findViewById(R.id.image_view_thumb);
            dateTextView = itemView.findViewById(R.id.text_view_date);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            descTextView = itemView.findViewById(R.id.text_view_desc);


        }

        public void bind(Photo item) {
            Glide.with(thumbImageView).load(item.getThumbUrl()).into(thumbImageView);
//            ImageLoader imageLoader = NetworkUtils.getInstance(thumbImageView.getContext()).getImageLoader();
//            imageLoader.get(item.getThumbUrl(), ImageLoader.getImageListener(thumbImageView, R.drawable.placeholder, R.drawable.error));
            dateTextView.setText(item.getData());
            titleTextView.setText(item.getTitle());
            descTextView.setText(item.getDesc());

        }

    }
    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;
    }
    public void setLastPage(boolean isLastPage){
        this.isLastPage = isLastPage;
    }
}
