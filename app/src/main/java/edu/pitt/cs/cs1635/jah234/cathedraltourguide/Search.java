package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by A on 3/18/2017.
 */

public class Search extends Fragment {

    Button enterButton;
    Spinner roomSpinner;
    View view;
    Intent i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_search, container, false);

        enterButton = (Button) view.findViewById(R.id.selectButton);
        roomSpinner = (Spinner) view.findViewById(R.id.roomSpinner);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                i = new Intent(view.getContext(), Room.class);
                i.putExtra("cathedraltourguide.Selection", roomSpinner.getSelectedItem().toString());
                startActivity(i);
            }
        });

        return view;
    }

    public static Fragment newInstance() {
        return new Search();
    }
}
