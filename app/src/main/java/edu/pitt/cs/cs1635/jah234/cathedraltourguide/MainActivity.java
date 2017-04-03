package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnSendDataListener{

    FrameLayout main; //where fragments are placed in
    BottomNavigationView menu; //the menu on the bottom
    ArrayList<String> nameList, numList; //arrays to help pair room name with its number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize arrays
        nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_names)));
        numList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_numbers)));

        //initialize things we can see
        main = (FrameLayout) findViewById(R.id.mainContent);
        menu = (BottomNavigationView) findViewById(R.id.tabMenu);

        //normally, bottomnavigationview hides the text on menu items if they're not focused, I think it looks ugly
        //admittedly, I don't fully understand this code
        BottomNavigationViewHelper.disableShiftMode(menu);

        //initial appearance of the app when first started
        if (savedInstanceState == null) {
            Achievements startFragment = new Achievements(); //create a new fragment of the proper type (Achievements, Gallery, Quiz, Room, Search)
            FragmentTransaction handler = getSupportFragmentManager().beginTransaction(); //FragmentTransaction is what handles switching one fragment for another
            handler.add(R.id.mainContent, startFragment); //specify which fragment object and placed where
            handler.commit(); //finalize order
            menu.getMenu().findItem(R.id.achievements).setChecked(true); //make sure proper menu item is focused
            setTitle("Your Achievements"); //change title in toolbar
            getPermissionToWrite(); //get permision to write to external storage
        }

        //handles navigation menu operations
        menu.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int colorId; //color to turn menubar into
                        String title; //text to change toolbar title to
                        Fragment newFragment; //fragment object to replace with
                        FragmentTransaction handler = getSupportFragmentManager().beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.achievements:
                                newFragment = new Achievements();
                                colorId = R.color.Yellow;
                                title = "Achievements";
                                break;
                            case R.id.search:
                                //newFragment = new Search();
                                newFragment = new NationalitySelector();
                                colorId = R.color.Purple;
                                title = "Room Search";
                                break;
                            case R.id.gallery:
                                newFragment = new Gallery();
                                colorId = R.color.Green;
                                title = "Gallery";
                                break;
                            case R.id.quiz:
                                newFragment = new Quiz();
                                colorId = R.color.Orange;
                                title = "Quiz";
                                break;
                            default: //shouldn't ever reach default but compiler required I put stuff here
                                newFragment = new Search();
                                colorId = R.color.Purple;
                                title = "Room Search";
                                break;
                        }

                        handler.replace(R.id.mainContent, newFragment);
                        handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                        handler.commit();
                        menu.setItemBackgroundResource(colorId);
                        setTitle(title);

                        return true;
                    }
                });

        //developer tool to retake quizzes and stuff, thinking of implementing delete save data option for users
        main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(MainActivity.this, "Deleting Saved Data", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = getSharedPreferences("saved_data", MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();
                return true;
            }
        });

    }

    //receives data from fragments
    public void send(Bundle data)
    {
        Fragment newFragment; //new fragment to replace with (if any)
        Bundle args; //arguments to send to new fragment (if any)
        FragmentTransaction handler;

        //key that pairs with what the app should do with the data
        String action = data.getString("Action");

        switch (action)
        {
            //called from Search fragment, replaces with Room fragment and loads info from specific room files
            case "New Room":
                String roomNum, roomName; //room name and number
                int index; //index of room in arrays

                newFragment = new Room();
                args = new Bundle();

                if (data.getString("Mode").equals("Name")) //selected room from spinner
                {
                    //grabs name of room
                    roomName = data.getString("Selection");

                    //grabs corresponding room number
                    index = nameList.indexOf(roomName);
                    if (index != -1)
                        roomNum = numList.get(index);
                    else
                        roomNum = "";
                }
                else //entered room number into edittext
                {
                    //grabs room number
                    roomNum = data.getString("Selection");

                    //grabs corresponding room name
                    index = numList.indexOf(roomNum);
                    if (index != -1)
                        roomName = nameList.get(index);
                    else
                        roomName = "";
                }

                if (index == -1) //if entered room number is incorrect
                {
                    Toast.makeText(MainActivity.this, "Sorry. Room Cannot be Found", Toast.LENGTH_LONG).show();
                }
                else
                {
                    args.putString("Selection", roomName);

                    newFragment.setArguments(args);

                    handler = getSupportFragmentManager().beginTransaction();
                    handler.replace(R.id.mainContent, newFragment);
                    handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                    handler.commit();

                    setTitle(roomName + " Room (Room " + roomNum + ")");
                }
                break;
            //called from Room fragment, loads Quiz fragment and goes straight to specific quiz
            case "Room Quiz":
                newFragment = new Quiz();
                args = new Bundle();
                args.putString("Room Name", data.getString("Room Name"));
                newFragment.setArguments(args);

                handler = getSupportFragmentManager().beginTransaction();
                handler.replace(R.id.mainContent, newFragment);
                handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                handler.commit();

                menu.setItemBackgroundResource(R.color.Orange);
                menu.getMenu().findItem(R.id.quiz).setChecked(true);
                setTitle("Quiz");
                break;
            default:
                break;
        }
    }

    //for grabbing permission to write to external storage
    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToWrite() {

        //checks if have permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        //checks if granted permission
        if (requestCode == 0) {
            if (!(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //modifies what back button does for Room fragment, might need it for other fragments
    @Override
    public void onBackPressed() {
        Fragment newFragment;
        FragmentTransaction handler;

        //gets the name of the fragment currently loaded into what we can see
        String current = getSupportFragmentManager().findFragmentById(R.id.mainContent).getClass().toString();

        switch (current)
        {
            //Room fragment
            case "class edu.pitt.cs.cs1635.jah234.cathedraltourguide.Room":
                newFragment = new Search();

                handler = getSupportFragmentManager().beginTransaction();
                handler.replace(R.id.mainContent, newFragment);
                handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                handler.commit();

                menu.setItemBackgroundResource(R.color.Purple);
                menu.getMenu().findItem(R.id.search).setChecked(true);
                setTitle("Room Search");
                break;
            //otherwise do as normal
            default:
                super.onBackPressed();
        }
    }

}
