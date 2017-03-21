package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by A on 3/18/2017.
 */

public class Search extends Fragment {

    Button enterButton;
    Spinner roomSpinner;
    View view;
    Intent i;

    OnSendDataListener sendData;

    public Search() {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_search, container, false);

        enterButton = (Button) view.findViewById(R.id.selectButton);
        roomSpinner = (Spinner) view.findViewById(R.id.roomSpinner);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //i = new Intent(view.getContext(), Room.class);
                //i.putExtra("cathedraltourguide.Selection", roomSpinner.getSelectedItem().toString());
                //startActivity(i);
                Bundle data = new Bundle();
                data.putString("Action", "New Room");
                data.putString("Selection", roomSpinner.getSelectedItem().toString());
                sendData.send(data);
            }
        });

        return view;
    }
}
