package com.peixing.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peixing.myapplication.R;
import com.peixing.myapplication.utils.BitmapUtils;

import java.util.ArrayList;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class PathAdapter extends RecyclerView.Adapter<PathAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> pictures;

    public PathAdapter(Context context, ArrayList<String> pictures) {
        this.context = context;
        this.pictures = pictures;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_path, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Here you can fill your row view
        String url = pictures.get(position);
        BitmapUtils.loadBitmap(context, url, false, holder.imgPathItem);
    }

    @Override
    public int getItemCount() {
        return pictures != null ? pictures.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgPathItem;

        public ViewHolder(View itemView) {
            super(itemView);
            imgPathItem = (ImageView) itemView.findViewById(R.id.img_path_item);
        }
    }
}
