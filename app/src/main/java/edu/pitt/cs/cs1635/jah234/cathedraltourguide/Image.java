package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by A on 3/25/2017.
 */

public class Image extends AppCompatActivity{

    ImageView image;
    TextView link;
    Button save, delete;

    String root, room;
    Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        image = (ImageView) findViewById(R.id.image);
        link = (TextView) findViewById(R.id.link);
        save = (Button) findViewById(R.id.save);
        delete = (Button) findViewById(R.id.delete);

        Intent i = getIntent();
        root = i.getStringExtra("From");
        room = i.getStringExtra("Room");
        file = Uri.parse(i.getStringExtra("Uri"));

        image.setImageURI(file);
        setTitle(parseDate(file.getLastPathSegment()));
        link.setText("Taken in " + room + " Room (Click to Go To)");

    }

    private String parseDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String hour = date.substring(9, 11);
        String minute = date.substring(11,13);
        String second = date.substring(13,15);

        return month + "/" + day + "/" + year + " at " + hour + ":" + minute + ":" + second;
    }

}
