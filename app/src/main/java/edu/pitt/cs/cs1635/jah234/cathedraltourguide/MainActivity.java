package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnSendDataListener{

    BottomNavigationView menu;
    ArrayList<String> nameList, numList;
    private static int userScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_names)));
        numList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_numbers)));

        menu = (BottomNavigationView) findViewById(R.id.tabMenu);
        BottomNavigationViewHelper.disableShiftMode(menu);

        if (savedInstanceState == null) {
            Search startFragment = new Search();
            FragmentTransaction handler = getSupportFragmentManager().beginTransaction();
            handler.add(R.id.mainContent, startFragment);
            handler.commit();
            menu.getMenu().findItem(R.id.search).setChecked(true);
            setTitle("Room Search");
        }

        menu.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment newFragment;
                        FragmentTransaction handler = getSupportFragmentManager().beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.search:
                                newFragment = new Search();
                                menu.setItemBackgroundResource(R.color.Purple);
                                setTitle("Room Search");
                                break;
                            case R.id.achievements:
                                newFragment = new Achievements();
                                menu.setItemBackgroundResource(R.color.Yellow);
                                setTitle("Achievements");
                                break;
                            case R.id.gallery:
                                newFragment = new Gallery();
                                menu.setItemBackgroundResource(R.color.Green);
                                setTitle("Gallery");
                                break;
                            case R.id.quiz:
                                newFragment = new Quiz();
                                menu.setItemBackgroundResource(R.color.Orange);
                                setTitle("Quiz");
                                break;
                            default:
                                newFragment = new Search();
                                menu.setItemBackgroundResource(R.color.Purple);
                                break;
                        }

                        handler.replace(R.id.mainContent, newFragment);
                        handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                        handler.commit();

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

                newFragment = new Room();
                args = new Bundle();
                if (data.getString("Mode").equals("Name"))
                {
                    roomName = data.getString("Selection");
                    roomNum = numList.get(nameList.indexOf(roomName));
                }
                else
                {
                    roomNum = data.getString("Selection");
                    roomName = nameList.get(numList.indexOf(roomNum));
                }
                args.putString("Selection", roomName);

                newFragment.setArguments(args);

                handler = getSupportFragmentManager().beginTransaction();
                handler.replace(R.id.mainContent, newFragment);
                handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                handler.commit();

                setTitle(roomName + " Room (Room " + roomNum + ")");
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

    public static void alterScore(int value)
    {
        userScore+= value;
    }
    public static int getScore()
    {
        return userScore;
    }

}
