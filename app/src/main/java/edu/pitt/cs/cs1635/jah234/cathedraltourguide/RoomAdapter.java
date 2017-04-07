package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by Jonathan on 4/2/2017.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder>
{
    private ArrayList<RoomCardInfo> rooms;
    //private ArrayList<String> roomNames;
    private Context context;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public RoomAdapter(Context context, ArrayList<RoomCardInfo> rooms, OnItemClickListener listener) {
        this.listener = listener;
        this.rooms = rooms;
        this.context = context;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView flagImage;
        private TextView roomName;

        public RoomViewHolder(View v)
        {
            super(v);
            flagImage = (ImageView)v.findViewById(R.id.card_flag);
            roomName = (TextView)v.findViewById(R.id.card_name);

        }

        public void bind(final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    v.setBackgroundResource(R.color.colorGray);
                    listener.onItemClick(position);
                }
            });
        }

    }

    public RoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.room_card, viewGroup, false);
        return new RoomViewHolder(v);
    }

    public void onBindViewHolder(RoomViewHolder viewHolder, int i)
    {
        viewHolder.roomName.setText(rooms.get(i).getName() + " Room\nRoom " + rooms.get(i).getNumber());
        if (rooms.get(i).getFlagID() == 0)
            Glide.with(context).load(R.drawable.africanheritage_flag)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.flagImage);
        else
            Glide.with(context).load(rooms.get(i).getFlagID())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.flagImage);
        viewHolder.bind(i, listener);

    }

    public int getItemCount()
    {
        return rooms.size();
    }
}
