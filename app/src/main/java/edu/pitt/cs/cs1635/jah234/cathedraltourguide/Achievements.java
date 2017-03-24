package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Achievements extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_achievements, container, false);
        TextView scoreField = (TextView)view.findViewById(R.id.points);
        scoreField.setText(String.valueOf(MainActivity.getScore())); //Set User Score

        TextView userAccomplishments = (TextView)view.findViewById(R.id.userAchievements);
        userAccomplishments.setMovementMethod(new ScrollingMovementMethod()); //For Scrolling

        FileInputStream fileIn= null;
        StringBuilder sb = new StringBuilder();
        try {
            fileIn = getContext().openFileInput("userAchievements.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer= new char[150];
            int read;

            while ((read=InputRead.read(inputBuffer))>0) {
                sb.append(String.copyValueOf(inputBuffer,0,read));
            }

            userAccomplishments.setText(sb.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


}