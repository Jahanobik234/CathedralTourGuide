package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Quiz extends AppCompatActivity {

    String answer1, answer2, answer3;
    String roomName;
    Spinner spinner;
    Button enterButton, submitAnswers;
    ArrayAdapter<String> spinnerArrayAdapt;
    String[] rooms = {"African Heritage", "Armenian", "Chinese", "Czechoslovak", "Early American"};
    ArrayList<String> roomsAL = new ArrayList<String>(Arrays.asList(rooms));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        spinner = (Spinner)findViewById(R.id.quizSpinner); //Get Spinner
        spinnerArrayAdapt = new ArrayAdapter<String>(Quiz.this, android.R.layout.simple_spinner_item, rooms); //Create Adapter for Spinner
        spinnerArrayAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //Set Drop Down Resource
        spinner.setAdapter(spinnerArrayAdapt); //Set Adapter
        spinner.setSelection(0); //Select First Item

        enterButton = (Button)findViewById(R.id.quizEnter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomName = spinner.getSelectedItem().toString();

                //Setting the Appropriate Question/Answer Fields
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(roomName + "_quiz.txt")));
                    ((TextView) findViewById(R.id.q1)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q1a1)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q1a2)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q1a3)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q2)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q2a1)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q2a2)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q2a3)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q3)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q3a1)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q3a2)).setText(reader.readLine());
                    ((TextView) findViewById(R.id.q3a3)).setText(reader.readLine());
                    answer1 = reader.readLine();
                    answer2 = reader.readLine();
                    answer3 = reader.readLine();
                    reader.close(); //Close Reader
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        enterButton.performClick(); //Click To Get First Entry's Fields

        submitAnswers = (Button)findViewById(R.id.submitAnswers);
        submitAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userScore = 0;

                if(((RadioGroup)findViewById(R.id.q1answers)).getCheckedRadioButtonId() != -1 &&
                        ((RadioGroup)findViewById(R.id.q2answers)).getCheckedRadioButtonId() != -1 &&
                        ((RadioGroup)findViewById(R.id.q3answers)).getCheckedRadioButtonId() != -1) {

                    //Check Answers Upon Submission
                    if (((TextView) findViewById(((RadioGroup) findViewById(R.id.q1answers)).getCheckedRadioButtonId())).getText().equals(answer1)) {
                        userScore += 10;
                    }

                    if (((TextView) findViewById(((RadioGroup) findViewById(R.id.q2answers)).getCheckedRadioButtonId())).getText().equals(answer2)) {
                        userScore += 10;
                    }

                    if (((TextView) findViewById(((RadioGroup) findViewById(R.id.q3answers)).getCheckedRadioButtonId())).getText().equals(answer3)) {
                        userScore += 10;
                    }

                    AlertDialog.Builder userMessage = new AlertDialog.Builder(Quiz.this);
                    userMessage.setTitle("Results for " + roomName + " Quiz");
                    userMessage.setMessage("You've earned " + userScore + " points for this quiz. Check out " +
                            "the Achievements page for your progress!");
                    userMessage.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog userDisplay = userMessage.create();
                    userDisplay.show();

                    roomsAL.remove(roomName); //Remove Name From Room So User Cannot Take Again
                    spinnerArrayAdapt = new ArrayAdapter<String>(Quiz.this, android.R.layout.simple_spinner_item, roomsAL);
                    spinnerArrayAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapt);
                    spinner.setSelection(0);
                    ((RadioGroup) findViewById(R.id.q1answers)).clearCheck();
                    ((RadioGroup) findViewById(R.id.q2answers)).clearCheck();
                    ((RadioGroup) findViewById(R.id.q3answers)).clearCheck();
                    enterButton.performClick();
                }

                else //Not All Questions Answered, Alert User
                {
                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(Quiz.this);
                    errorMessage.setTitle("Please Answer All Questions");
                    errorMessage.setMessage("You must answer all questions before submitting for points!");
                    errorMessage.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog userDisplay = errorMessage.create();
                    userDisplay.show();
                }

            }
        });


    }

    public void onSaveInstanceState(Bundle outState)
    {
        outState.putStringArrayList("RemainingQuizzes", roomsAL); //Save Array List
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        roomsAL = savedInstanceState.getStringArrayList("RemainingQuizzes");
    }
}
