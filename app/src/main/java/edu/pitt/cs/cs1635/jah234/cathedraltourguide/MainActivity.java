package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.Manifest;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static edu.pitt.cs.cs1635.jah234.cathedraltourguide.R.id.roomName;

public class MainActivity extends AppCompatActivity implements OnSendDataListener{

    FrameLayout main; //where fragments are placed in
    BottomNavigationView menu; //the menu on the bottom

    List<String> listGroupingNames;
    HashMap<String, List<String>> listSubgroupNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize things we can see
        main = (FrameLayout) findViewById(R.id.mainContent);
        menu = (BottomNavigationView) findViewById(R.id.tabMenu);

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

        AlertDialog.Builder userMessage = new AlertDialog.Builder(this);
        userMessage.setTitle("Welcome to TourCathy!");
        userMessage.setMessage(R.string.achievements_header);
        userMessage.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog userDisplay = userMessage.create();
        userDisplay.show();

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
                                newFragment = new NationalitySelector();
                                colorId = R.color.Purple;
                                title = "Room Search";
                                break;
                            case R.id.gallery:
                                newFragment = new Gallery();
                                colorId = R.color.Green;
                                title = "Gallery";
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
    }

    //receives data from fragments
    public void send(Bundle data)
    {
        Fragment newFragment; //new fragment to replace with (if any)
        FragmentTransaction handler;

        //key that pairs with what the app should do with the data
        String action = data.getString("Action");

        switch (action)
        {
            //called from Search fragment, replaces with Room fragment and loads info from specific room files
            case "New Room":
                newFragment = new Room();

                newFragment.setArguments(data);

                handler = getSupportFragmentManager().beginTransaction();
                handler.replace(R.id.mainContent, newFragment);
                handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                handler.commit();
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
                newFragment = new NationalitySelector();

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

    //Code Borrowed From Android Developers Online
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                LayoutInflater li = LayoutInflater.from(this);
                final View helpView = li.inflate(R.layout.help_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Help");

                // Load up help menu tips
                prepareListData();
                alertDialogBuilder.setView(helpView);
                ExpandableListView myList = (ExpandableListView) helpView.findViewById(R.id.list_help_tips);
                ExpandableListAdapter myAdapter = new ExpandableListAdapter(helpView.getContext(), listGroupingNames, listSubgroupNames);
                myList.setAdapter(myAdapter);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                // Show input alert dialog
                alertDialog.show();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void showUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void hideUp() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void prepareListData() {
        listGroupingNames = new ArrayList<String>();
        listSubgroupNames = new HashMap<String, List<String>>();

        // Adding group names
        listGroupingNames.add("What tab menu buttons do");
        listGroupingNames.add("How to earn achievement points");
        listGroupingNames.add("How to search for a room");
        listGroupingNames.add("How to view image gallery");
        listGroupingNames.add("How to take a quiz");

        // Adding subgroup names
        List<String> tabmenu = new ArrayList<String>();
        tabmenu.add("Achievements - Is used for displaying your current progress and accomplishments you have done.");
        tabmenu.add("Search - Is used for locating the information for a specific room.");
        tabmenu.add("Gallery - Is used for displaying images that you take.");
        tabmenu.add("Quiz - Is used to test your knowledge about nationality rooms.");

        List<String> achievements = new ArrayList<String>();
        achievements.add("\tOne way to earn achievement points is by taking quizzes. Be careful, " +
                "only your first attempt will be remembered! You can also earn points by " +
                "finding objects in the Object Hunt section of the room. This hunt can be found " +
                "under each given room's page");

        List<String> search = new ArrayList<String>();
        search.add("\tTo visit a room's page, click on Search in the bottom bar and select the room" +
                "you wish to visit.");

        List<String> gallery = new ArrayList<String>();
        gallery.add("\tTo view your gallery, click on the Gallery in the bottom bar.");

        List<String> quiz = new ArrayList<String>();
        quiz.add("\tTake quizzes by searching for a specific nationality room and then swiping left on the room's information page.");

        // Associate group name with subgroup
        listSubgroupNames.put(listGroupingNames.get(0), tabmenu);
        listSubgroupNames.put(listGroupingNames.get(1), achievements);
        listSubgroupNames.put(listGroupingNames.get(2), search);
        listSubgroupNames.put(listGroupingNames.get(3), gallery);
        listSubgroupNames.put(listGroupingNames.get(4), quiz);
    }

}
