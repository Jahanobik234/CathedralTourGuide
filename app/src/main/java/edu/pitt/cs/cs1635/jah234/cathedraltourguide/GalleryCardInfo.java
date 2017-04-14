package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.SharedPreferences;
import android.net.Uri;

public class GalleryCardInfo {

    private Uri uri;
    private String header, date;
    private int position;
    private SharedPreferences keyPair;

    public GalleryCardInfo(String header) {
        this.header = header;
    }

    public GalleryCardInfo(Uri uri, int position, SharedPreferences keyPair) {
        this.uri = uri;
        this.position = position;
        this.keyPair = keyPair;

        date = parseDate(uri.getLastPathSegment());
        header = null;
    }

    public Uri getUri() {
        return uri;
    }

    public String getHeader() { return header; }

    public String getHeader(boolean bool) {
        if (bool)
            return date;
        else
        {
            String room = keyPair.getString(uri.getLastPathSegment() + "name", "(Cancel)");
            if (room.equals("(Cancel)"))
                room = "(none)";
            return room;
        }
    }

    public int getPosition() {
        return position;
    }

    public int getLocation() { return keyPair.getInt(uri.getLastPathSegment() + "location", -1); }

    public int compare(GalleryCardInfo e2) { return (getLocation() - e2.getLocation()); }

    private String parseDate(String data) {
        String year = data.substring(0, 4);
        String month = data.substring(4, 6);
        String day = data.substring(6, 8);
        return month + "/" + day  + "/" + year;
    }
}
