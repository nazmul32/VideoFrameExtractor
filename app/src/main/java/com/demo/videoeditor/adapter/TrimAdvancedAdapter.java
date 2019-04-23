package com.demo.videoeditor.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.demo.videoeditor.R;
import com.demo.videoeditor.util.ImageUtils;

import java.util.ArrayList;

public class TrimAdvancedAdapter extends RecyclerView.Adapter<TrimAdvancedAdapter.TrimAdvancedViewHolder> {
    private ArrayList<Bitmap> dataSet = new ArrayList<>();
    private Context context;
    private RelativeLayout.LayoutParams relativeLayoutParams;
    private int startPosition = -1;
    private int endPosition = -1;

    public TrimAdvancedAdapter(Context context) {
        this.context = context;
        int layoutWidth = ImageUtils.getScreeWidth((Activity) context) / 2;
        int layoutHeight = (int) ImageUtils.convertDpToPixel(120, context);
        relativeLayoutParams  = new RelativeLayout.LayoutParams(
                layoutWidth, layoutHeight);
    }
    public void updateDataSet(ArrayList<Bitmap> partialDataSet) {
        dataSet.addAll(dataSet.size(), partialDataSet);
        notifyItemRangeInserted(dataSet.size(), partialDataSet.size());
    }

    @NonNull
    @Override
    public TrimAdvancedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_advanced_trim, parent, false);
        view.setLayoutParams(relativeLayoutParams);
        TrimAdvancedViewHolder vh = new TrimAdvancedViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull TrimAdvancedViewHolder trimAdvancedViewHolder, int position) {
//        Glide.with(context)
//                .asBitmap()
//                .load(dataSet.get(position))
//                .error(R.drawable.bg_error)
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                .into(trimAdvancedViewHolder.imageView);
        if (startPosition != -1) {
            if (position < startPosition) {
                trimAdvancedViewHolder.frameImageView.setAlpha(0.5f);
            }
            if (position == startPosition) {
                trimAdvancedViewHolder.startMarkerImageView.setVisibility(View.VISIBLE);
            }
        }

        if (endPosition != -1) {
            if (position > endPosition) {
                trimAdvancedViewHolder.frameImageView.setAlpha(0.5f);
            }
            if (position == endPosition) {
                trimAdvancedViewHolder.endMarkerImageView.setVisibility(View.VISIBLE);
            }
        }

        if (startPosition == -1 && endPosition == -1) {
            trimAdvancedViewHolder.endMarkerImageView.setVisibility(View.GONE);
            trimAdvancedViewHolder.startMarkerImageView.setVisibility(View.GONE);
            trimAdvancedViewHolder.frameImageView.setAlpha(1f);
        }

        trimAdvancedViewHolder.frameImageView.setImageBitmap(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet == null ? 0 : dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class TrimAdvancedViewHolder extends RecyclerView.ViewHolder {
        public ImageView frameImageView;
        public ImageView startMarkerImageView;
        public ImageView endMarkerImageView;
        public TrimAdvancedViewHolder(View v) {
            super(v);
            frameImageView = v.findViewById(R.id.image_view);
            startMarkerImageView = v.findViewById(R.id.trim_selection_start_marker);
            endMarkerImageView = v.findViewById(R.id.trim_selection_end_marker);
        }
    }

    public void markFrame(int position, boolean isStart) {
        if (isStart) {
            startPosition = position;
        } else {
            endPosition = position;
        }
        notifyDataSetChanged();
    }
}
