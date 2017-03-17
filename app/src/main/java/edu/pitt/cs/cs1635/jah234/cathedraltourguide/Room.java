package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Room extends AppCompatActivity {

    TextView intro;
    ImageView flag;

    InputStream stream;
    BufferedReader input;
    StringBuilder large_text;
    String line;
    Drawable image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        intro = (TextView) findViewById(R.id.intro);
        flag = (ImageView) findViewById(R.id.flag);

        Intent i = getIntent();
        String selection = i.getStringExtra("cathedraltourguide.Selection");

        try
        {
            stream = getAssets().open(selection + "_intro.txt");
            large_text = new StringBuilder(stream.available());
            input = new BufferedReader(new InputStreamReader(stream));
            while ((line = input.readLine()) != null)
            {
                large_text.append(line + "\n");
            }
            stream.close();

            stream = getAssets().open(selection + "_flag.png");
            image = Drawable.createFromStream(stream, null);
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        intro.setText(large_text);
        flag.setImageDrawable(image);

    }
}

