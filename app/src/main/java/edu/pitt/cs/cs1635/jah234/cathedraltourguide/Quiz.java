package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Quiz extends Fragment {

    View view;
    String answer1, answer2, answer3;
    String roomName;
    Spinner spinner;
    Button enterButton, submitAnswers;
    LinearLayout questions;

    int score;
    ArrayAdapter<String> spinnerArrayAdapt;
    ArrayList<String> roomsAL;

    public Quiz() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            roomsAL = savedInstanceState.getStringArrayList("RemainingQuizzes");
            //roomsAL = (ArrayList<String>) savedInstanceState.getSerializable("RemainingQuizzes");
            roomName = savedInstanceState.getString("LastRoom");
        }
        else
        {
            roomsAL = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_names)));
            roomName = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.activity_quiz, container, false);

        spinner = (Spinner) view.findViewById(R.id.quizSpinner); //Get Spinner
        spinnerArrayAdapt = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, roomsAL); //Create Adapter for Spinner
        spinnerArrayAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //Set Drop Down Resource
        spinner.setAdapter(spinnerArrayAdapt); //Set Adapter
        questions = (LinearLayout) view.findViewById(R.id.linearLayout3);

        if (getArguments() != null)
        {
            roomName = getArguments().getString("Room Name");
            spinner.setSelection(roomsAL.indexOf(roomName));
        }
        else
        {
            spinner.setSelection(0); //Select First Item
        }

        if (roomName == null)
        {
            questions.setVisibility(View.GONE);
        }
        else
        {
            loadQuestions();
        }

        enterButton = (Button)view.findViewById(R.id.quizEnter);
        submitAnswers = (Button)view.findViewById(R.id.submitAnswers);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomName = spinner.getSelectedItem().toString();
                questions.setVisibility(View.VISIBLE);

                //Setting the Appropriate Question/Answer Fields
                loadQuestions();

                if(MainActivity.quizzesTaken.contains(roomName))
                {
                    submitAnswers.setEnabled(false); //Disable Button
                }
                else
                {
                    submitAnswers.setEnabled(true); //Enable Button
                }


            }
        });

        //enterButton.performClick(); //Click To Get First Entry's Fields

        submitAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userScore = 0;

                if(((RadioGroup)view.findViewById(R.id.q1answers)).getCheckedRadioButtonId() != -1 &&
                        ((RadioGroup)view.findViewById(R.id.q2answers)).getCheckedRadioButtonId() != -1 &&
                        ((RadioGroup)view.findViewById(R.id.q3answers)).getCheckedRadioButtonId() != -1) {

                    //Check Answers Upon Submission
                    if (((TextView) view.findViewById(((RadioGroup) view.findViewById(R.id.q1answers)).getCheckedRadioButtonId())).getText().equals(answer1)) {
                        userScore += 10;
                    }

                    if (((TextView) view.findViewById(((RadioGroup) view.findViewById(R.id.q2answers)).getCheckedRadioButtonId())).getText().equals(answer2)) {
                        userScore += 10;
                    }

                    if (((TextView) view.findViewById(((RadioGroup) view.findViewById(R.id.q3answers)).getCheckedRadioButtonId())).getText().equals(answer3)) {
                        userScore += 10;
                    }

                    AlertDialog.Builder userMessage = new AlertDialog.Builder(view.getContext());
                    userMessage.setTitle("Results for " + roomName + " Quiz");
                    userMessage.setMessage("You've earned " + userScore + " points for this quiz. Check out " +
                            "the Achievements page for your progress!");
                    userMessage.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    MainActivity.quizzesTaken.add(roomName); //Add To Quizzes Taken

                    try {
                        FileOutputStream fileout = getContext().openFileOutput("userAchievements.txt", getContext().MODE_APPEND);
                        OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                        outputWriter.append("Completed " + roomName + " Quiz - Earned " + userScore + " Points\n");
                        outputWriter.close(); //Close Writer
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    AlertDialog userDisplay = userMessage.create();
                    userDisplay.show();
                    MainActivity.alterScore(userScore);

                    roomsAL.remove(roomName); //Remove Name From Room So User Cannot Take Again
                    spinnerArrayAdapt = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, roomsAL);
                    spinnerArrayAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapt);
                    spinner.setSelection(0);
                    ((RadioGroup) view.findViewById(R.id.q1answers)).clearCheck();
                    ((RadioGroup) view.findViewById(R.id.q2answers)).clearCheck();
                    ((RadioGroup) view.findViewById(R.id.q3answers)).clearCheck();
                    enterButton.performClick();
                }

                else //Not All Questions Answered, Alert User
                {
                    AlertDialog.Builder errorMessage = new AlertDialog.Builder(view.getContext());
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

        return view;
    }

    private void loadQuestions() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(view.getContext().getAssets().open(roomName + "_quiz.txt")));
            score = Integer.parseInt(reader.readLine());
            ((TextView) view.findViewById(R.id.q1)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q1a1)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q1a2)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q1a3)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q2)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q2a1)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q2a2)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q2a3)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q3)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q3a1)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q3a2)).setText(reader.readLine());
            ((TextView) view.findViewById(R.id.q3a3)).setText(reader.readLine());
            answer1 = reader.readLine();
            answer2 = reader.readLine();
            answer3 = reader.readLine();
            reader.close(); //Close Reader
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("RemainingQuizzes", roomsAL); //Save Array List
        //outState.putSerializable("RemainingQuizzes", roomsAL);
        outState.putString("LastRoom", roomName);
    }

    /*public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        roomsAL = savedInstanceState.getStringArrayList("RemainingQuizzes");
    }*/
}