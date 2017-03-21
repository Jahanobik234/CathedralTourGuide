package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Room extends Fragment {

    TextView intro;
    ImageView flag;
    View view;

    int index1 = 0, index2 = 0, index3 = 0;
    InputStream stream;
    BufferedReader input;
    StringBuilder large_text;
    String line;
    Drawable image;
    String selection;
    String[] hint = new String[9];
    Button quiz, hint1, hint2, hint3;

    OnSendDataListener sendData;

    public Room() {
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            sendData = (OnSendDataListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + " must implement OnSendDataListener");
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_room, container, false);

        intro = (TextView) view.findViewById(R.id.intro);
        flag = (ImageView) view.findViewById(R.id.flag);
        hint1 = (Button) view.findViewById(R.id.hint1);
        hint2 = (Button) view.findViewById(R.id.hint2);
        hint3 = (Button) view.findViewById(R.id.hint3);
        quiz = (Button) view.findViewById(R.id.take_quiz);

        selection = getArguments().getString("Selection");

        try
        {
            stream = getContext().getAssets().open(selection + "_intro.txt");
            large_text = new StringBuilder(stream.available());
            input = new BufferedReader(new InputStreamReader(stream));
            while ((line = input.readLine()) != null)
            {
                large_text.append(line).append("\n");
            }
            stream.close();

            stream = getContext().getAssets().open(selection + "_flag.png");
            image = Drawable.createFromStream(stream, null);
            stream.close();

            stream = getContext().getAssets().open(selection + "_hint.txt");
            input = new BufferedReader(new InputStreamReader(stream));
            for (int i=0; i< 9; i++)
            {
                hint[i] = input.readLine();
            }
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        intro.setText(large_text);
        flag.setImageDrawable(image);

        hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index1] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint1.setText(text);
                index1 = (index1 + 1) % 3;
            }
        });
        hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index2 + 3] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint2.setText(text);
                index2 = (index2 + 1) % 3;
            }
        });
        hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index3 + 6] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint3.setText(text);
                index3 = (index3 + 1) % 3;
            }
        });

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("Action", "Room Quiz");
                data.putString("Room Name", selection);
                sendData.send(data);
            }
        });

        return view;
    }
}

