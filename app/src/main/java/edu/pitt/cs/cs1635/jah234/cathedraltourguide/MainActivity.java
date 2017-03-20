package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageButton;
public class MainActivity extends AppCompatActivity {

    //ImageButton searchButton,achievementButton,galleryButton,quizButton;
    BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (BottomNavigationView) findViewById(R.id.tabMenu);
        BottomNavigationViewHelper.disableShiftMode(menu);

        if (savedInstanceState == null) {
            FragmentTransaction handler = getSupportFragmentManager().beginTransaction();
            handler.add(R.id.mainContent, Search.newInstance());
            handler.commit();
            menu.getMenu().findItem(R.id.search).setChecked(true);
            setTitle("Room Search");
        }

        /*searchButton = (ImageButton)findViewById(R.id.search);
        achievementButton = (ImageButton)findViewById(R.id.achievements);
        galleryButton = (ImageButton)findViewById(R.id.gallery);
        quizButton = (ImageButton)findViewById(R.id.quiz);*/
        //menu.getMenu().findItem(R.id.search).setChecked(true);

        menu.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction handler = getSupportFragmentManager().beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.search:
                                handler.replace(R.id.mainContent, Search.newInstance());
                                menu.setItemBackgroundResource(R.color.Purple);
                                setTitle("Room Search");
                                break;
                            case R.id.achievements:
                                handler.replace(R.id.mainContent, Achievements.newInstance());
                                menu.setItemBackgroundResource(R.color.Yellow);
                                setTitle("Achievements");
                                break;
                            case R.id.gallery:
                                handler.replace(R.id.mainContent, Gallery.newInstance());
                                menu.setItemBackgroundResource(R.color.Green);
                                setTitle("Gallery");
                                break;
                            case R.id.quiz:
                                handler.replace(R.id.mainContent, Quiz.newInstance());
                                menu.setItemBackgroundResource(R.color.Orange);
                                setTitle("Quiz");
                                break;
                        }

                        handler.setTransition(handler.TRANSIT_FRAGMENT_CLOSE);
                        handler.commit();

                        return true;
                    }
                });

        /*searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.beginTransaction().replace(R.id.mainContent, Search.newInstance()).commit();
            }
        });

        achievementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.beginTransaction().replace(R.id.mainContent, Achievements.newInstance()).commit();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.beginTransaction().replace(R.id.mainContent, Gallery.newInstance()).commit();
            }
        });

        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.beginTransaction().replace(R.id.mainContent, Quiz.newInstance()).commit();
            }
        });*/

    }

}
