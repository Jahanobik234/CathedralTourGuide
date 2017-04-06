package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

/**
 * Created by A on 4/6/2017.
 */

public class AudioHelper {

    public String getTime(long milliseconds){
        StringBuilder timeString = new StringBuilder();

        int minutes = (int) (milliseconds / (1000*60));
        int seconds = (int) ((milliseconds % (1000*60)) / 1000);

        if (minutes < 10)
            timeString.append("0");

        timeString.append(minutes).append(":");

        if(seconds < 10)
            timeString.append("0");

        timeString.append(seconds);

        return timeString.toString();
    }

    public int getPercent(long currentMSec, long totalMSec){

        return (int)((currentMSec * 100) / totalMSec);
    }

    public int getSeek(int progress, long totalMSec) {

        return (int)((((long) progress) * totalMSec) / 100);
    }
}