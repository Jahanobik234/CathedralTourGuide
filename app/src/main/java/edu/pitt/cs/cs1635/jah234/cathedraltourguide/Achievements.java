package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Achievements extends Fragment {

    private static int userScore;
    View view;

    public Achievements()
    {
        userScore = 0; //Set User Score to 0
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_achievements, container, false);
        TextView scoreField = (TextView)view.findViewById(R.id.points);
        scoreField.setText(String.valueOf(userScore)); //Set User Score
        return view;
    }

    public static void alterScore(int value)
    {
        userScore+= value;
    }

}