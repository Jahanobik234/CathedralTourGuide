package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class Achievements extends Fragment {

    View view; //Everything you can see
    TextView scoreField, userAccomplishments; //relevant objects you can see in view

    SharedPreferences keyPair; //holds saved data

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //keyPair = saved data
        //keyPair.get Params: key to identify value to fetch, value to return if can't find in saved data
        keyPair = getContext().getSharedPreferences("saved_data", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_achievements, container, false); //creates what we can see

        //initialize objects in view
        scoreField = (TextView)view.findViewById(R.id.points);
        userAccomplishments = (TextView)view.findViewById(R.id.userAchievements);

        //set score to saved value
        scoreField.setText(Integer.toString(keyPair.getInt("Total Score", 0)));

        //stringbuilder to make appending text together easier
        StringBuilder sb = new StringBuilder();

        //find set of data stored in saved data
        if (keyPair.getStringSet("achievementSet", null) != null) {

            //convert Set into ArraySet so that individual indices can be referenced
            ArraySet<String> achievementSet = new ArraySet<>();
            achievementSet.addAll(keyPair.getStringSet("achievementSet", null));

            //append each value
            for (int i = 0; i < achievementSet.size(); i++)
                sb.append(achievementSet.valueAt(i) + "\n");

            //set list of accomplishments to block of text
            userAccomplishments.setText(sb.toString());
        }

        //always return view
        return view;
    }


}