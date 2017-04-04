package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jonathan on 4/2/2017.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder>
{
    /*public String[] roomNames = {
            "African Heritage",
            "Armenian",
            "Austrian",
            "Chinese",
            "Czechoslovak",
            "Early American"
    };*/

    private final ArrayList<String> roomNames;

    public int[] flags = {
      R.drawable.africanheritage_flag,
            R.drawable.armenian_flag,
            R.drawable.austrian_flag,
            R.drawable.chinese_flag,
            R.drawable.czechoslovak_flag,
            R.drawable.earlyamerican_flag
    };

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String room);
    }

    public RoomAdapter(ArrayList<String> roomNames, OnItemClickListener listener) {
        this.listener = listener;
        this.roomNames = roomNames;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView flagImage;
        public TextView roomName;

        public RoomViewHolder(View v)
        {
            super(v);
            //flagImage = (ImageView)v.findViewById(R.id.card_flag);
            roomName = (TextView)v.findViewById(R.id.card_name);

        }

        public void bind(final String room, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    v.setBackgroundResource(R.color.colorGray);
                    listener.onItemClick(room);
                }
            });
        }

    }

    public RoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.room_card, viewGroup, false);
        RoomViewHolder viewHolder = new RoomViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(RoomViewHolder viewHolder, int i)
    {
        viewHolder.roomName.setText(roomNames.get(i));
        //viewHolder.flagImage.setImageResource(flags[i]);
        viewHolder.bind(roomNames.get(i), listener);

    }

    public int getItemCount()
    {
        return roomNames.size();
    }
}
