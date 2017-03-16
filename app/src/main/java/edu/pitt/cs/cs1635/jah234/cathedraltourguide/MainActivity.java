package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button enterButton = (Button)findViewById(R.id.selectButton);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Spinner roomSpinner = (Spinner)findViewById(R.id.roomSpinner);
                Intent i;
                switch(roomSpinner.getSelectedItem().toString())
                {
                    case "African Heritage":
                        i = new Intent(MainActivity.this, AfricanHeritage.class);
                        startActivity(i);
                        break;
                    /*
                    case "Armenian":
                        //i = new Intent(MainActivity.this, Armenian.class);
                        //startActivity(i);
                        break;
                    case "Austrian":
                        //i = new Intent(MainActivity.this, Austrian.class);
                        //startActivity(i);
                        break;
                    case "Chinese":
                        //i = new Intent(MainActivity.this, Chinese.class);
                        //startActivity(i);
                        break;
                    case "Czechoslovak":
                       // i = new Intent(MainActivity.this, Czechoslovak.class);
                        //startActivity(i);
                        break;
                    case "Early American":
                        //i = new Intent(MainActivity.this, EarlyAmerican.class);
                        //startActivity(i);
                        break;
                    */
                }
            }
        });

        ImageButton searchButton = (ImageButton)findViewById(R.id.search);
        ImageButton achievementButton = (ImageButton)findViewById(R.id.achievements);
        ImageButton galleryButton = (ImageButton)findViewById(R.id.gallery);
        ImageButton quizButton = (ImageButton)findViewById(R.id.quiz);

        /*
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity.class); //CAUSES ERROR
                startActivity(i);
            }
        });

        searchButton.setEnabled(false);
        */
        achievementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Achievements.class);
                startActivity(i);
            }
        });
/*
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Gallery.class);
                startActivity(i);
            }
        });
 */
        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Quiz.class);
                startActivity(i);
            }
        });

    }

}
