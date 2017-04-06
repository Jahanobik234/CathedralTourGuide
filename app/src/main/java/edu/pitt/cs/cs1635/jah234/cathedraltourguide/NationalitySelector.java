package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class NationalitySelector extends Fragment
{
    View view;
    EditText searchRoom;
    ImageView searchIcon;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<RoomCardInfo> rooms, fRooms;
    //ArrayList<String> roomNames;

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

        rooms = new ArrayList<>();

        String[] names = getResources().getStringArray(R.array.room_names);
        String[] nums = getResources().getStringArray(R.array.room_numbers);
        TypedArray idArray = getResources().obtainTypedArray(R.array.room_flag);

        for (int i = 0; i < names.length; i++)
        {
            rooms.add(new RoomCardInfo(names[i], nums[i], idArray.getResourceId(i, 0)));
        }
        fRooms = new ArrayList<>(rooms);
        //roomNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.room_names)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_nationality_selector, container, false);

        searchRoom = (EditText) view.findViewById(R.id.searchRoom);
        searchIcon = (ImageView) view.findViewById(R.id.searchIcon);
        recyclerView = (RecyclerView) view.findViewById(R.id.roomsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);

        adapter = new RoomAdapter(fRooms, new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle data = new Bundle();
                data.putString("Action", "New Room");
                data.putInt("Position", position);
                sendData.send(data);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(itemDecoration);

        searchRoom.addTextChangedListener( new TextWatcher() {
            public void onTextChanged(CharSequence str, int start, int before, int count)
            {
                if (searchRoom.getText().toString().length() == 0)
                {
                    searchIcon.setVisibility(View.VISIBLE);
                }
                else
                {
                    searchIcon.setVisibility(View.GONE);
                }

                filterResults(searchRoom.getText().toString());
            }

            public void beforeTextChanged(CharSequence str, int start, int count, int after)
            {}

            public void afterTextChanged(Editable str)
            {}
        });
        return view;
    }

    private void filterResults(String text)
    {
        fRooms.clear();

        if (text.length() == 0)
        {
            fRooms.addAll(rooms);
        }
        else
        {
            for (int i = 0; i < rooms.size(); i++)
            {
                if (rooms.get(i).contains(text))
                {
                    fRooms.add(rooms.get(i));
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

}
