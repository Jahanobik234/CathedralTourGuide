package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
import java.util.Random;
import java.util.Set;



public class Quiz extends Fragment {

    View view; //everything you can see

    //objects in view
    Spinner spinner;
    Button enterButton, submitAnswers;
    LinearLayout correctLayout1, incorrectLayout1, correctLayout2, incorrectLayout2, correctLayout3, incorrectLayout3;
    ScrollView questionLayout;
    RadioGroup q1answers, q2answers, q3answers;
    TextView quizIntro;

    Question[] questions; //array of question objects to randomly choose from
    int[] indices; //array of indices holding which number questions were randomly chosen
    String roomName; //name of room currently focused on

    //Array to Spinner things
    ArrayAdapter<String> spinnerArrayAdapt;
    ArrayList<String> roomsAL;

    //holds save data
    SharedPreferences keyPair;

    public Quiz() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //initializes arraylist of rooms, current room to null
        roomsAL = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_names)));
        roomName = null;

        //keyPair = save data
        //keyPair.get Params: key to identify value to fetch, value to return if can't find in saved data
        keyPair = getContext().getSharedPreferences("saved_data", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.activity_quiz, container, false); //creates everything we can see

        //(a lot of) objects initialized
        spinner = (Spinner) view.findViewById(R.id.quizSpinner); //Get Spinner
        spinnerArrayAdapt = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, roomsAL); //Create Adapter for Spinner
        spinnerArrayAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //Set Drop Down Resource
        spinner.setAdapter(spinnerArrayAdapt); //Set Adapter
        questionLayout = (ScrollView) view.findViewById(R.id.linearLayout3);
        correctLayout1 = (LinearLayout) view.findViewById(R.id.correctLayout1);
        incorrectLayout1 = (LinearLayout) view.findViewById(R.id.incorrectLayout1);
        correctLayout2 = (LinearLayout) view.findViewById(R.id.correctLayout2);
        incorrectLayout2 = (LinearLayout) view.findViewById(R.id.incorrectLayout2);
        correctLayout3 = (LinearLayout) view.findViewById(R.id.correctLayout3);
        incorrectLayout3 = (LinearLayout) view.findViewById(R.id.incorrectLayout3);
        q1answers = (RadioGroup)view.findViewById(R.id.q1answers);
        q2answers = (RadioGroup)view.findViewById(R.id.q2answers);
        q3answers = (RadioGroup)view.findViewById(R.id.q3answers);
        enterButton = (Button)view.findViewById(R.id.quizEnter);
        submitAnswers = (Button)view.findViewById(R.id.submitAnswers);
        quizIntro = (TextView) view.findViewById(R.id.quizIntro);

        //hides textviews showing correct answers and incorrect chosen answers for now
        correctLayout1.setVisibility(View.GONE);
        incorrectLayout1.setVisibility(View.GONE);
        correctLayout2.setVisibility(View.GONE);
        incorrectLayout2.setVisibility(View.GONE);
        correctLayout3.setVisibility(View.GONE);
        incorrectLayout3.setVisibility(View.GONE);

        //quiz was opened from Room fragment
        if (getArguments() != null)
        {
            roomName = getArguments().getString("Room Name");
            spinner.setSelection(roomsAL.indexOf(roomName));
        }
        else //quiz was opened from navigation menu
        {
            spinner.setSelection(0); //Select First Item
        }

        //quiz was opened from navigation menu
        if (roomName == null)
        {
            questionLayout.setVisibility(View.GONE);
            quizIntro.setVisibility(View.VISIBLE);
        }
        else //quiz was opened from Room fragment
        {
            quizIntro.setVisibility(View.GONE);
            loadQuestions();
        }

        //set value of roomName, make questions and choices visible, make intro info invisible, loadQuestions method below
        enterButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                roomName = spinner.getSelectedItem().toString();
                questionLayout.setVisibility(View.VISIBLE);
                quizIntro.setVisibility(View.GONE);
                loadQuestions();

            }
        });

        submitAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userScore = 0;

                if((q1answers.getCheckedRadioButtonId() != -1) && (q2answers.getCheckedRadioButtonId() != -1) && (q3answers.getCheckedRadioButtonId() != -1)) {

                    String choice1 = ((TextView) view.findViewById(q1answers.getCheckedRadioButtonId())).getText().toString();
                    String choice2 = ((TextView) view.findViewById(q2answers.getCheckedRadioButtonId())).getText().toString();
                    String choice3 = ((TextView) view.findViewById(q3answers.getCheckedRadioButtonId())).getText().toString();

                    //make choices invisible
                    q1answers.setVisibility(View.INVISIBLE);
                    q2answers.setVisibility(View.INVISIBLE);
                    q3answers.setVisibility(View.INVISIBLE);

                    //Show correct answer or incorrect choice and increase points
                    if (choice1.equals(questions[indices[0]].getAnswer())) {
                        userScore += 10;
                        correctLayout1.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        ((TextView) view.findViewById(R.id.incorrect1)).setText(choice1);
                        incorrectLayout1.setVisibility(View.VISIBLE);
                    }

                    if (choice2.equals(questions[indices[1]].getAnswer())) {
                        userScore += 10;
                        correctLayout2.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        ((TextView) view.findViewById(R.id.incorrect2)).setText(choice2);
                        incorrectLayout2.setVisibility(View.VISIBLE);
                    }

                    if (choice3.equals(questions[indices[2]].getAnswer())) {
                        userScore += 10;
                        correctLayout3.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        ((TextView) view.findViewById(R.id.incorrect3)).setText(choice3);
                        incorrectLayout3.setVisibility(View.VISIBLE);
                    }

                    //store results to save data
                    SharedPreferences.Editor editor = keyPair.edit();
                    if (keyPair.getInt(roomName, -1) == -1) //returning -1 means this is the first time attempting submission of points
                    {
                        Set<String> temp = keyPair.getStringSet("achievementSet", null); //pulls list of achievements if exists
                        if (temp == null)
                        {
                            temp = new ArraySet<>(); //makes new one if not
                        }
                        temp.add("First Try at " + roomName + " Quiz: " + Integer.toString(userScore) + " Points"); //adds to list
                        editor.putStringSet("achievementSet", temp); //puts set in storage
                    }
                    editor.putInt(roomName, (userScore / 10)); //puts recent score in storage
                    if (keyPair.getInt("Total Score", -1) == -1) //checks if total score exists
                    {
                        editor.putInt("Total Score", userScore); //adds new if not
                    }
                    else
                    {
                        editor.putInt("Total Score", (userScore + keyPair.getInt("Total Score", -1))); //adds to total score and puts in storage
                    }
                    //puts questions and answers into storage, only relevant if got all correct
                    editor.putString(roomName + " Q1", questions[indices[0]].getQuestion());
                    editor.putString(roomName + " A1", questions[indices[0]].getAnswer());
                    editor.putString(roomName + " Q2", questions[indices[1]].getQuestion());
                    editor.putString(roomName + " A2", questions[indices[1]].getAnswer());
                    editor.putString(roomName + " Q3", questions[indices[2]].getQuestion());
                    editor.putString(roomName + " A3", questions[indices[2]].getAnswer());
                    editor.commit(); //finalize saves

                    //make dialog box to notify user
                    AlertDialog.Builder userMessage = new AlertDialog.Builder(getContext());
                    userMessage.setTitle("Results for " + roomName + " Quiz");
                    userMessage.setMessage("You've earned " + userScore + " points for this quiz. Check out " +
                            "the Achievements page for your progress!");
                    userMessage.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            q1answers.clearCheck();
                            q2answers.clearCheck();
                            q3answers.clearCheck();
                            quizIntro.setVisibility(View.VISIBLE); //makes intro visible again
                            questionLayout.setVisibility(View.GONE); //makes questions invisible again
                        }
                    });

                    AlertDialog userDisplay = userMessage.create();
                    userDisplay.show();
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

        if (keyPair.getInt(roomName, 0) == 3) //got all correct last time, just show questions and correct answers
        {
            correctLayout1.setVisibility(View.VISIBLE);
            correctLayout2.setVisibility(View.VISIBLE);
            correctLayout3.setVisibility(View.VISIBLE);
            q1answers.setVisibility(View.INVISIBLE);
            q2answers.setVisibility(View.INVISIBLE);
            q3answers.setVisibility(View.INVISIBLE);
            readAnswers();
            submitAnswers.setEnabled(false);
        }
        else { //show new set of questions to answer
            indices = getThreeRandom();
            questions = new Question[10];
            correctLayout1.setVisibility(View.GONE);
            incorrectLayout1.setVisibility(View.GONE);
            correctLayout2.setVisibility(View.GONE);
            incorrectLayout2.setVisibility(View.GONE);
            correctLayout3.setVisibility(View.GONE);
            incorrectLayout3.setVisibility(View.GONE);
            q1answers.setVisibility(View.VISIBLE);
            q2answers.setVisibility(View.VISIBLE);
            q3answers.setVisibility(View.VISIBLE);
            readQuestions();
            submitAnswers.setEnabled(true);
        }

    }

    private void readQuestions() {
        try {
            //read from quiz file, put into question array
            BufferedReader reader = new BufferedReader(new InputStreamReader(view.getContext().getAssets().open(roomName + "_quiz.txt")));
            for (int i=0; i< 10; i++)
            {
                String newQuestion = reader.readLine();
                String choice1 = reader.readLine();
                String choice2 = reader.readLine();
                String choice3 = reader.readLine();
                String answer = reader.readLine();
                questions[i] = new Question(newQuestion, choice1, choice2, choice3, answer);
            }
            reader.close(); //Close Reader

            //shows three random questions
            ((TextView) view.findViewById(R.id.q1)).setText(questions[indices[0]].getQuestion());
            ((TextView) view.findViewById(R.id.q1a1)).setText(questions[indices[0]].getChoice1());
            ((TextView) view.findViewById(R.id.q1a2)).setText(questions[indices[0]].getChoice2());
            ((TextView) view.findViewById(R.id.q1a3)).setText(questions[indices[0]].getChoice3());
            ((TextView) view.findViewById(R.id.q2)).setText(questions[indices[1]].getQuestion());
            ((TextView) view.findViewById(R.id.q2a1)).setText(questions[indices[1]].getChoice1());
            ((TextView) view.findViewById(R.id.q2a2)).setText(questions[indices[1]].getChoice2());
            ((TextView) view.findViewById(R.id.q2a3)).setText(questions[indices[1]].getChoice3());
            ((TextView) view.findViewById(R.id.q3)).setText(questions[indices[2]].getQuestion());
            ((TextView) view.findViewById(R.id.q3a1)).setText(questions[indices[2]].getChoice1());
            ((TextView) view.findViewById(R.id.q3a2)).setText(questions[indices[2]].getChoice2());
            ((TextView) view.findViewById(R.id.q3a3)).setText(questions[indices[2]].getChoice3());

            ((TextView) view.findViewById(R.id.correct1)).setText(questions[indices[0]].getAnswer());
            ((TextView) view.findViewById(R.id.correct2)).setText(questions[indices[1]].getAnswer());
            ((TextView) view.findViewById(R.id.correct3)).setText(questions[indices[2]].getAnswer());
        } catch (Exception e) {
            questionLayout.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Sorry. These Questions Aren't Ready Yet", Toast.LENGTH_LONG).show();
        }
    }

    private void readAnswers() {
        ((TextView) view.findViewById(R.id.q1)).setText(keyPair.getString(roomName + " Q1", null));
        ((TextView) view.findViewById(R.id.q2)).setText(keyPair.getString(roomName + " Q2", null));
        ((TextView) view.findViewById(R.id.q3)).setText(keyPair.getString(roomName + " Q3", null));
        ((TextView) view.findViewById(R.id.correct1)).setText(keyPair.getString(roomName + " A1", null));
        ((TextView) view.findViewById(R.id.correct2)).setText(keyPair.getString(roomName + " A2", null));
        ((TextView) view.findViewById(R.id.correct3)).setText(keyPair.getString(roomName + " A3", null));
    }

    //not sure this actually does anything anymore, but doesn't cause problems so for now
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("RemainingQuizzes", roomsAL); //Save Array List
        //outState.putSerializable("RemainingQuizzes", roomsAL);
        outState.putString("LastRoom", roomName);
    }

    //get a random number, check if already picked earlier, repeat
    private int[] getThreeRandom()
    {
        int[] indices = new int[3];


        indices[0] = (int) Math.floor(Math.random() * 10);

        do
        {
            indices[1] = (int) Math.floor(Math.random() * 10);
        } while (indices[1] == indices[0]);

        do
        {
            indices[2] = (int) Math.floor(Math.random() * 10);
        } while ((indices[2] == indices[1]) || (indices[2] == indices[0]));

        return indices;
    }
}