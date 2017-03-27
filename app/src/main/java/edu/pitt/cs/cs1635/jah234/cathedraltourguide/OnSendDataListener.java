package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.os.Bundle;

/**
 * Created by A on 3/21/2017.
 */

//just an interface needed to send data from fragment to MainActivity
public interface OnSendDataListener {

    void send(Bundle data);
}
