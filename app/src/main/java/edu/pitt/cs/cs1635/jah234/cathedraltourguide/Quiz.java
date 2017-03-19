package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Quiz extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_quiz, container, false);
    }

    public static Fragment newInstance() {
        return new Quiz();
    }
}
