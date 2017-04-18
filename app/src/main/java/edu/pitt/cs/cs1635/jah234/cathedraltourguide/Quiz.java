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
    Button submitAnswers;
    LinearLayout correctLayout1, incorrectLayout1, correctLayout2, incorrectLayout2, correctLayout3, incorrectLayout3;
    ScrollView questionLayout;
    RadioGroup q1answers, q2answers, q3answers;

    Question[] questions; //array of question objects to randomly choose from
    int[] indices; //array of indices holding which number questions were randomly chosen
    String roomName; //name of room currently focused on
    int roomNum;

    //holds save data
    SharedPreferences keyPair;

    public Quiz() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //initializes arraylist of rooms, current room to null
        roomNum = getArguments().getInt("Position");
        roomName = (getResources().getStringArray(R.array.room_names))[roomNum];

        keyPair = getContext().getSharedPreferences("saved_data", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.activity_quiz, container, false); //creates everything we can see

        correctLayout1 = (LinearLayout) view.findViewById(R.id.correctLayout1);
        incorrectLayout1 = (LinearLayout) view.findViewById(R.id.incorrectLayout1);
        correctLayout2 = (LinearLayout) view.findViewById(R.id.correctLayout2);
        incorrectLayout2 = (LinearLayout) view.findViewById(R.id.incorrectLayout2);
        correctLayout3 = (LinearLayout) view.findViewById(R.id.correctLayout3);
        incorrectLayout3 = (LinearLayout) view.findViewById(R.id.incorrectLayout3);
        questionLayout = (ScrollView) view.findViewById(R.id.linearLayout3);
        q1answers = (RadioGroup)view.findViewById(R.id.q1answers);
        q2answers = (RadioGroup)view.findViewById(R.id.q2answers);
        q3answers = (RadioGroup)view.findViewById(R.id.q3answers);
        submitAnswers = (Button)view.findViewById(R.id.submitAnswers);

        //hides textviews showing correct answers and incorrect chosen answers for now
        correctLayout1.setVisibility(View.GONE);
        incorrectLayout1.setVisibility(View.GONE);
        correctLayout2.setVisibility(View.GONE);
        incorrectLayout2.setVisibility(View.GONE);
        correctLayout3.setVisibility(View.GONE);
        incorrectLayout3.setVisibility(View.GONE);

        loadQuestions();

        submitAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userScore = 0;

                if (submitAnswers.getText().toString().equals("Submit Answers")) {
                    if ((q1answers.getCheckedRadioButtonId() != -1) && (q2answers.getCheckedRadioButtonId() != -1) && (q3answers.getCheckedRadioButtonId() != -1)) {

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
                        } else {
                            ((TextView) view.findViewById(R.id.incorrect1)).setText(choice1);
                            incorrectLayout1.setVisibility(View.VISIBLE);
                        }

                        if (choice2.equals(questions[indices[1]].getAnswer())) {
                            userScore += 10;
                            correctLayout2.setVisibility(View.VISIBLE);
                        } else {
                            ((TextView) view.findViewById(R.id.incorrect2)).setText(choice2);
                            incorrectLayout2.setVisibility(View.VISIBLE);
                        }

                        if (choice3.equals(questions[indices[2]].getAnswer())) {
                            userScore += 10;
                            correctLayout3.setVisibility(View.VISIBLE);
                        } else {
                            ((TextView) view.findViewById(R.id.incorrect3)).setText(choice3);
                            incorrectLayout3.setVisibility(View.VISIBLE);
                        }

                        //store results to save data
                        SharedPreferences.Editor editor = keyPair.edit();
                        if (keyPair.getInt("quiz" + roomNum, -1) == -1) //returning -1 means this is the first time attempting submission of points
                        {
                            Set<String> temp = keyPair.getStringSet("achievementSet", null); //pulls list of achievements if exists
                            if (temp == null) {
                                temp = new ArraySet<>(); //makes new one if not
                            }
                            temp.add("First Try at " + roomName + " Quiz: " + Integer.toString(userScore) + " Points"); //adds to list
                            editor.putStringSet("achievementSet", temp); //puts set in storage

                            if (keyPair.getInt("Total Score", -1) == -1) //checks if total score exists
                            {
                                editor.putInt("Total Score", userScore); //adds new if not
                            } else {
                                editor.putInt("Total Score", (userScore + keyPair.getInt("Total Score", -1))); //adds to total score and puts in storage
                            }
                        }
                        editor.putInt("quiz" + roomNum, (userScore / 10)); //puts recent score in storage

                        if (userScore == 30) {
                            submitAnswers.setEnabled(false);
                            editor.putInt("question1" + roomNum, indices[0]);
                            editor.putInt("question2" + roomNum, indices[1]);
                            editor.putInt("question3" + roomNum, indices[2]);
                        }
                        else
                        {
                            submitAnswers.setText("Take Quiz Again");
                            editor.putInt("question1" + roomNum, -1);
                            editor.putInt("question2" + roomNum, -1);
                            editor.putInt("question3" + roomNum, -1);
                        }

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
                else if (submitAnswers.getText().toString().equals("Take Quiz Again"))
                {
                    loadQuestions();
                    submitAnswers.setText("Submit Answers");
                }

            }
        });

        return view;
    }

    private void loadQuestions() {

        indices = getThreeRandom();
        questions = new Question[10];
        readQuestions();

        if (keyPair.getInt("quiz" + roomNum, 0) == 3) //got all correct last time, just show questions and correct answers
        {
            correctLayout1.setVisibility(View.VISIBLE);
            correctLayout2.setVisibility(View.VISIBLE);
            correctLayout3.setVisibility(View.VISIBLE);
            q1answers.setVisibility(View.INVISIBLE);
            q2answers.setVisibility(View.INVISIBLE);
            q3answers.setVisibility(View.INVISIBLE);
            submitAnswers.setEnabled(false);
        }
        else { //show new set of questions to answer
            correctLayout1.setVisibility(View.GONE);
            incorrectLayout1.setVisibility(View.GONE);
            correctLayout2.setVisibility(View.GONE);
            incorrectLayout2.setVisibility(View.GONE);
            correctLayout3.setVisibility(View.GONE);
            incorrectLayout3.setVisibility(View.GONE);
            q1answers.setVisibility(View.VISIBLE);
            q2answers.setVisibility(View.VISIBLE);
            q3answers.setVisibility(View.VISIBLE);
            submitAnswers.setEnabled(true);
        }

    }

    private void readQuestions() {
        try {
            //read from quiz file, put into question array
            BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(getResources().obtainTypedArray(R.array.room_quiz).getResourceId(roomNum, 0))));
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
        ((TextView) view.findViewById(R.id.q1)).setText(keyPair.getString(roomNum + " Q1", null));
        ((TextView) view.findViewById(R.id.q2)).setText(keyPair.getString(roomNum + " Q2", null));
        ((TextView) view.findViewById(R.id.q3)).setText(keyPair.getString(roomNum + " Q3", null));
        ((TextView) view.findViewById(R.id.correct1)).setText(keyPair.getString(roomNum + " A1", null));
        ((TextView) view.findViewById(R.id.correct2)).setText(keyPair.getString(roomNum + " A2", null));
        ((TextView) view.findViewById(R.id.correct3)).setText(keyPair.getString(roomNum + " A3", null));
    }

    //get a random number, check if already picked earlier, repeat
    private int[] getThreeRandom()
    {
        int[] indices = new int[3];

        if (keyPair.getInt("question1" + roomNum, -1) == -1)
            indices[0] = (int) Math.floor(Math.random() * 10);
        else
            indices[0] = keyPair.getInt("question1" + roomNum, -1);

        if (keyPair.getInt("question2" + roomNum, -1) == -1)
        {
            do
            {
                indices[1] = (int) Math.floor(Math.random() * 10);
            } while (indices[1] == indices[0]);
        }
        else
            indices[1] = keyPair.getInt("question2" + roomNum, -1);

        if (keyPair.getInt("question3" + roomNum, -1) == -1)
        {
            do
            {
                indices[2] = (int) Math.floor(Math.random() * 10);
            } while ((indices[2] == indices[1]) || (indices[2] == indices[0]));
        }
        else
            indices[2] = keyPair.getInt("question3" + roomNum, -1);

        return indices;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (submitAnswers.getText().toString().equals("Submit Answers"))
        {
            SharedPreferences.Editor editor = keyPair.edit();
            editor.putInt("question1" + roomNum, indices[0]);
            editor.putInt("question2" + roomNum, indices[1]);
            editor.putInt("question3" + roomNum, indices[2]);
            editor.commit();
        }
    }
}