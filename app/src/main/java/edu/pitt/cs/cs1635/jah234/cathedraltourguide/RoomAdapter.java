package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Jonathan on 4/2/2017.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder>
{
    public String[] roomNames = {
            "African Heritage",
            "Armenian",
            "Austrian",
            "Chinese",
            "Czechoslovak",
            "Early American"
    };

    public int[] flags = {
      R.drawable.africanheritage_flag,
            R.drawable.armenian_flag,
            R.drawable.austrian_flag,
            R.drawable.chinese_flag,
            R.drawable.czechoslovak_flag,
            R.drawable.earlyamerican_flag
    };

    public static class RoomViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView flagImage;
        public TextView roomName;
        public RoomViewHolder(View v)
        {
            super(v);
            flagImage = (ImageView)v.findViewById(R.id.card_flag);
            roomName = (TextView)v.findViewById(R.id.card_name);

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
        viewHolder.roomName.setText(roomNames[i]);
        viewHolder.flagImage.setImageResource(flags[i]);
    }

    public int getItemCount()
    {
        return roomNames.length;
    }
}
