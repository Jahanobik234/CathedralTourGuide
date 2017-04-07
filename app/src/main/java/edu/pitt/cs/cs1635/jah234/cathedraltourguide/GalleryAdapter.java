package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by A on 4/7/2017.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>
{
    private LinkedList<Uri> images;
    private Context context;

    private final GalleryAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onImageClick(int position);
    }

    public GalleryAdapter(Context context, LinkedList<Uri> images, GalleryAdapter.OnItemClickListener listener) {
        this.listener = listener;
        this.images = images;
        this.context = context;
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView image;

        public GalleryViewHolder(View v)
        {
            super(v);
            image = (ImageView)v.findViewById(R.id.galleryImage);
        }

        public void bind(final int position, final GalleryAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    listener.onImageClick(position);
                }
            });
        }

    }

    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_card, viewGroup, false);
        return new GalleryAdapter.GalleryViewHolder(v);
    }

    public void onBindViewHolder(GalleryAdapter.GalleryViewHolder viewHolder, int i)
    {
        //viewHolder.image.setImageBitmap(images.get(i).getImage());
        Glide.with(context).load(images.get(i))
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.image);
        viewHolder.bind(i, listener);

    }

    public int getItemCount()
    {
        return images.size();
    }
}