package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class NationalitySelector extends Fragment
{
    View view;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<String> roomNames;
    ArrayList<Integer> flagID;

    OnSendDataListener sendData;

    public NationalitySelector() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        //checks that the activity that called this implemented OnSendDataListener, which it should
        //needed to send data back to MainActivity
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_names)));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nationality_selector, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.roomsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        adapter = new RoomAdapter(roomNames, new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String room) {
                Bundle data = new Bundle();
                data.putString("Action", "New Room");
                data.putString("Mode", "Name");
                data.putString("Selection", room);
                sendData.send(data);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(itemDecoration);
        return view;
    }

}
