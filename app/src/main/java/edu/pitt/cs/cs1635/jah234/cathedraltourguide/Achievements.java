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

    View view;
    TextView scoreField, userAccomplishments;
    String[] achievementList;

    SharedPreferences keyPair;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        keyPair = getContext().getSharedPreferences("saved_data", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_achievements, container, false);
        TextView scoreField = (TextView)view.findViewById(R.id.points);
        TextView userAccomplishments = (TextView)view.findViewById(R.id.userAchievements);

        //scoreField.setText(String.valueOf(MainActivity.getScore())); //Set User Score
        scoreField.setText(Integer.toString(keyPair.getInt("Total Score", 0)));

        //userAccomplishments.setMovementMethod(new ScrollingMovementMethod()); //For Scrolling

        //FileInputStream fileIn= null;
        StringBuilder sb = new StringBuilder();
        /*try {
            fileIn = getContext().openFileInput("userAchievements.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer= new char[150];
            int read;

            while ((read=InputRead.read(inputBuffer))>0) {
                sb.append(String.copyValueOf(inputBuffer,0,read));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }*/

        if (keyPair.getStringSet("achievementSet", null) != null) {
            ArraySet<String> achievementSet = new ArraySet<>();
                achievementSet.addAll(keyPair.getStringSet("achievementSet", null));

                for (int i = 0; i < achievementSet.size(); i++) {
                    sb.append(achievementSet.valueAt(i));
                }

                userAccomplishments.setText(sb.toString());
        }

        return view;
    }


}