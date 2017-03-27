package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Gallery extends Fragment {

    View view;

    public Gallery() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_gallery, container, false); //creates what we can see

        //always return view
        return view;
    }
}
