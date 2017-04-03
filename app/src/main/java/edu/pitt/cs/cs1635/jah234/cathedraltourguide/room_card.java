package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

/**
 * Created by Jonathan on 4/2/2017.
 */

public class room_card
{
    private String roomName;
    private int flagImage;

    public room_card(String rn, int fi)
    {
        roomName = rn;
        flagImage = fi;
    }

    public String getName()
    {
        return roomName;
    }

    public int getImage()
    {
        return flagImage;
    }

    public void setImage(int i)
    {
        flagImage = i;
    }
}
