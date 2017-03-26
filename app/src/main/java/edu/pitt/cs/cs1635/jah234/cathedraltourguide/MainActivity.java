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

    FrameLayout main;
    BottomNavigationView menu;
    ArrayList<String> nameList, numList;

    //public static ArrayList<String> quizzesTaken;
    //private static int userScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_names)));
        numList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_numbers)));
        //quizzesTaken = new ArrayList<>();

        /*try {
            FileOutputStream fileout = this.openFileOutput("userAchievements.txt", this.MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write("");
            outputWriter.close(); //Close Writer
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*try {
            inFile = openFileInput("save_data.bin");
            if (inFile.read(quizScores) < 30)
            {
                Log.e("File Read Quiz", "Not Enough Bytes");
            }
            else if (inFile.read(achievementChecklist) < 55)
            {
                Log.e("File Read Achievements", "Not Enough Bytes");
            }

        } catch (FileNotFoundException nfe) {
            new File(getFilesDir(), "save_data.bin");
        } catch (IOException ioe) {
            Log.e("IOException", ioe.toString());
        }*/

        main = (FrameLayout) findViewById(R.id.mainContent);
        menu = (BottomNavigationView) findViewById(R.id.tabMenu);
        BottomNavigationViewHelper.disableShiftMode(menu);

        if (savedInstanceState == null) {
            Achievements startFragment = new Achievements();
            FragmentTransaction handler = getSupportFragmentManager().beginTransaction();
            handler.add(R.id.mainContent, startFragment);
            handler.commit();
            menu.getMenu().findItem(R.id.achievements).setChecked(true);
            setTitle("Your Achievements");
            getPermissionToWrite();
        }

        menu.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int colorId;
                        String title;
                        Fragment newFragment;
                        FragmentTransaction handler = getSupportFragmentManager().beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.achievements:
                                newFragment = new Achievements();
                                colorId = R.color.Yellow;
                                title = "Achievements";
                                break;
                            case R.id.search:
                                newFragment = new Search();
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
                            default:
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

    public void send(Bundle data)
    {
        Fragment newFragment;
        Bundle args;
        FragmentTransaction handler;

        String action = data.getString("Action");

        switch (action)
        {
            case "New Room":
                String roomNum, roomName;
                int index;

                newFragment = new Room();
                args = new Bundle();
                if (data.getString("Mode").equals("Name"))
                {
                    roomName = data.getString("Selection");
                    index = nameList.indexOf(roomName);
                    if (index != -1)
                        roomNum = numList.get(index);
                    else
                        roomNum = "";
                }
                else
                {
                    roomNum = data.getString("Selection");
                    index = numList.indexOf(roomNum);
                    if (index != -1)
                        roomName = nameList.get(index);
                    else
                        roomName = "";
                }

                if (index == -1)
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

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToWrite() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == 0) {
            if (!(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment newFragment;
        FragmentTransaction handler;

        String current = getSupportFragmentManager().findFragmentById(R.id.mainContent).getClass().toString();

        switch (current)
        {
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
            default:
                super.onBackPressed();
        }
    }

    /*public static int getScore()
    {
        return userScore;
    }*/

}
