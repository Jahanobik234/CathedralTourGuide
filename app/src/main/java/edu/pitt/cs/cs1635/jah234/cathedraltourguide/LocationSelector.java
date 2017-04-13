package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by A on 4/13/2017.
 */

public class LocationSelector extends AppCompatActivity {

    EditText searchRoom;
    ImageView searchIcon;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ArrayList<RoomCardInfo> rooms, fRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_nationality_selector);

        rooms = new ArrayList<>();

        String[] names = getResources().getStringArray(R.array.room_names);
        String[] nums = getResources().getStringArray(R.array.room_numbers);
        TypedArray idArray = getResources().obtainTypedArray(R.array.room_flag);

        for (int i = 0; i < names.length; i++)
        {
            rooms.add(new RoomCardInfo(names[i], nums[i], idArray.getResourceId(i, 0), i + 1));
        }
        rooms.add(0, new RoomCardInfo("(Cancel)", "", 0, 0));
        fRooms = new ArrayList<>(rooms);

        searchRoom = (EditText) findViewById(R.id.searchRoom);
        searchIcon = (ImageView) findViewById(R.id.searchIcon);
        recyclerView = (RecyclerView) findViewById(R.id.roomsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        adapter = new RoomAdapter(this, fRooms, new RoomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent result = new Intent("RESULT_ACTION");
                result.putExtra("Name", rooms.get(position).getName());
                result.putExtra("Flag", rooms.get(position).getFlagID());
                result.putExtra("Position", rooms.get(position).getPosition() - 1);
                setResult(RESULT_OK, result);
                finish();
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
