package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

public class RoomCardInfo {

    private String name;
    private String number;
    private int flagID;
    private int position;

    public RoomCardInfo(String name, String number, int flagID, int position) {
        this.name = name;
        this.number = number;
        this.flagID = flagID;
        this.position = position;
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

    public int getPosition() {
        return position;
    }

    public boolean contains(String text) {
        return name.toLowerCase().contains(text.toLowerCase()) || number.indexOf(text) >= 0;
    }
}
