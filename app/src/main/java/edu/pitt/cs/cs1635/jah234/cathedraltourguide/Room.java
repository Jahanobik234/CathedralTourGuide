package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

//public class Room extends AppCompatActivity {
public class Room extends Fragment {

    TextView intro;
    ImageView flag;
    View view;

    InputStream stream;
    BufferedReader input;
    StringBuilder large_text;
    String line;
    Drawable image;
    String selection;

    /*public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        if (savedInstanceState != null) {
            Toast.makeText(Room.this, savedInstanceState.getString("Selection"), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(Room.this, "Selection", Toast.LENGTH_SHORT).show();
        }
    }*/

    public Room() {
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_room, container, false);

        intro = (TextView) view.findViewById(R.id.intro);
        flag = (ImageView) view.findViewById(R.id.flag);

        selection = getArguments().getString("Selection");

        try
        {
            stream = getContext().getAssets().open(selection + "_intro.txt");
            large_text = new StringBuilder(stream.available());
            input = new BufferedReader(new InputStreamReader(stream));
            while ((line = input.readLine()) != null)
            {
                large_text.append(line).append("\n");
            }
            stream.close();

            stream = getContext().getAssets().open(selection + "_flag.png");
            image = Drawable.createFromStream(stream, null);
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        intro.setText(large_text);
        flag.setImageDrawable(image);

        return view;
    }
}

