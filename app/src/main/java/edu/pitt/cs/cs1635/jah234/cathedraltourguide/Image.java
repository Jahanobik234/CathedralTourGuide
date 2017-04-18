package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by A on 3/25/2017.
 */

public class Image extends AppCompatActivity{

    //Relevant objects we can see
    ImageView image, flag;
    TextView label;
    EditText comment;
    Button save, delete;
    ImageButton add;

    Uri file;

    //holds saved data
    SharedPreferences keyPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //initialize objects in view
        image = (ImageView) findViewById(R.id.image);
        flag = (ImageView) findViewById(R.id.flagImage);
        label = (TextView) findViewById(R.id.name);
        comment = (EditText) findViewById(R.id.comment);
        save = (Button) findViewById(R.id.save);
        add = (ImageButton) findViewById(R.id.location);
        delete = (Button) findViewById(R.id.delete);

        //pulls variables from intent extras
        Intent i = getIntent();
        file = Uri.parse(i.getStringExtra("Uri")); //identifier for a file

        keyPair = getSharedPreferences("saved_data", MODE_PRIVATE);


        Glide.with(this).load(file)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image);
        setTitle(parseDate(file.getLastPathSegment())); //parseDate method below

        //comments are saved in storage as (filename)comment
        if (!keyPair.getString(file.getLastPathSegment() + "comment", "").equals("")){
            comment.setText(keyPair.getString(file.getLastPathSegment() + "comment", ""));
        }

        String name = keyPair.getString(file.getLastPathSegment() + "name", "(Cancel)");
        int flagID = keyPair.getInt(file.getLastPathSegment() + "flag", 0);

        if (name.equals("(Cancel)"))
            label.setText("\t(none");
        else
            label.setText("\t" + name + " Room");

        if (flagID != 0)
            Glide.with(this).load(flagID)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(flag);

        //puts what's currently in comment box into storage
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = keyPair.edit();
                editor.putString(file.getLastPathSegment() + "comment", comment.getText().toString());
                editor.commit();
                Toast.makeText(Image.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Image.this, LocationSelector.class);
                startActivityForResult(i, 3);
            }
        });

        //still working on it
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
                        Intent result = new Intent("RESULT_ACTION");
                        setResult(RESULT_OK, result);
                        finish();
                    }
                });
                AlertDialog confirmDisplay = confirm.create();
                confirmDisplay.show();
            }
        });
    }

    private String parseDate(String date) {
        //filenames are saved as yyyyMMdd_hhmmss.jpg
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

        return month + "/" + day + "/" + year + " at " + Integer.toString((hour + 11) % 12 + 1) + ":" + minute + ":" + second + ap;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3 && resultCode == RESULT_OK)
        {
            String name = data.getExtras().getString("Name");
            int flagID = data.getExtras().getInt("Flag", 0);
            int position = data.getExtras().getInt("Position", 0);

            if (name == null || name.equals("(Cancel)"))
                label.setText("\t(none)");
            else
                label.setText("\t" + name + " Room");

            Glide.with(this).load(flagID)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(flag);

            SharedPreferences.Editor editor = keyPair.edit();
            editor.putInt(file.getLastPathSegment() + "flag", flagID);
            editor.putInt(file.getLastPathSegment() + "location", position);
            editor.putString(file.getLastPathSegment() + "name", name);
            editor.commit();
        }
    }
}
