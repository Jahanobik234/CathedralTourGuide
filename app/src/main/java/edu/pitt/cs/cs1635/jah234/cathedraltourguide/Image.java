package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by A on 3/25/2017.
 */

public class Image extends AppCompatActivity{

    ImageView image;
    TextView link;
    EditText comment;
    Button save, delete;

    String root, room;
    Uri file;

    SharedPreferences keyPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        image = (ImageView) findViewById(R.id.image);
        link = (TextView) findViewById(R.id.link);
        comment = (EditText) findViewById(R.id.comment);
        save = (Button) findViewById(R.id.save);
        delete = (Button) findViewById(R.id.delete);

        Intent i = getIntent();
        root = i.getStringExtra("From");
        room = i.getStringExtra("Room");
        file = Uri.parse(i.getStringExtra("Uri"));

        keyPair = getSharedPreferences("comment_data", MODE_PRIVATE);

        image.setImageURI(file);
        setTitle(parseDate(file.getLastPathSegment()));
        link.setText("Taken in " + room + " Room (Click to Go To)");

        if (!keyPair.getString(file.getLastPathSegment() + "comment", "").equals("")){
            comment.setText(keyPair.getString(file.getLastPathSegment() + "comment", null));
        }

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = keyPair.edit();
                editor.putString(file.getLastPathSegment() + "comment", comment.getText().toString());
                editor.commit();
                Toast.makeText(Image.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(Image.this);
                confirm.setTitle("Confirm Delete?");
                confirm.setMessage("Are you sure you wish to delete this image? (This action cannot be reversed)");
                confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Intent result = new Intent("RESULT_ACTION");
                        //setResult(Image.RESULT_OK, result);
                        Toast.makeText(Image.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog confirmDisplay = confirm.create();
                confirmDisplay.show();
            }
        });
    }

    private String parseDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        int hour = Integer.parseInt(date.substring(9, 11));
        String minute = date.substring(11,13);
        String second = date.substring(13,15);
        String ap;

        if (hour < 12)
            ap = "AM";
        else
            ap = "PM";

        return month + "/" + day + "/" + year + " at " + Integer.toString(hour % 12) + ":" + minute + ":" + second + ap;
    }

}
