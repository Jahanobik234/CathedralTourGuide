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

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private LinkedList<GalleryCardInfo> images;
    private Context context;

    private final GalleryAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onImageClick(int position);
    }

    public GalleryAdapter(Context context, LinkedList<GalleryCardInfo> images, GalleryAdapter.OnItemClickListener listener) {
        this.listener = listener;
        this.images = images;
        this.context = context;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        private TextView header;

        public HeaderViewHolder(View v)
        {
            super(v);
            header = (TextView)v.findViewById(R.id.galleryHeader);
        }

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

    @Override
    public int getItemViewType(int position) {
        if (images.get(position).getHeader() == null)
            return 1;
        else
            return 0;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        if (viewType == 0)
            return new GalleryAdapter.HeaderViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_header, viewGroup, false));
        else
            return new GalleryAdapter.GalleryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_card, viewGroup, false));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i)
    {
        if (images.get(i).getHeader() == null)
        {
            Glide.with(context).load(images.get(i).getUri())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(100, 100)
                    .centerCrop()
                    .into(((GalleryViewHolder)viewHolder).image);
            ((GalleryViewHolder)viewHolder).bind(images.get(i).getPosition(), listener);
        }
        else
            ((HeaderViewHolder)viewHolder).header.setText(images.get(i).getHeader());

    }

    public int getItemCount()
    {
        return images.size();
    }
}