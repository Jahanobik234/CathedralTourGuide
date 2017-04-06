package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

/**
 * Created by A on 4/6/2017.
 */

public class RoomCardInfo {

    private String name;
    private String number;
    private int flagID;

    public RoomCardInfo(String name, String number, int flagID) {
        this.name = name;
        this.number = number;
        this.flagID = flagID;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public int getFlagID() {
        return flagID;
    }

    public boolean contains(CharSequence text) {
        return name.contains(text) || number.contains(text);
    }
}
