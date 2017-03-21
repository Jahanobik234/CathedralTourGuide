package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements OnSendDataListener{

    BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                newFragment = new Room();
                args = new Bundle();
                args.putString("Selection", data.getString("Selection"));
                newFragment.setArguments(args);

                handler = getSupportFragmentManager().beginTransaction();
                handler.replace(R.id.mainContent, newFragment);
                handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                handler.commit();

                setTitle(data.getString("Selection") + " Room");
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

}
