package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArraySet;
import android.support.v4.view.ViewPager;
import android.widget.EditText;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

public class Room extends Fragment{

    View view;
    ViewPager viewPager;
    RoomPagerAdapter adapter;

    OnSendDataListener sendData;

    String selection, number;

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

        //initializes value of current room
        //selection = getArguments().getString("Selection");
        //number = getArguments().getString("Number");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.viewpager_room, container, false); //creates stuff we can see

        viewPager = (ViewPager) view.findViewById(R.id.roomContent);
        adapter = new RoomPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        return view;
    }

    private class RoomPagerAdapter extends FragmentPagerAdapter {

        private RoomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment newFragment;

            switch (i) {
                case 0:
                    newFragment = new Room_Info();
                    break;
                case 1:
                    newFragment = new Room_Interact();
                    break;
                default:
                    newFragment = new Room_Info();

            }

            newFragment.setArguments(getArguments());
            return newFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Information";
                case 1:
                    return "Notable Items";

            }
            return selection + " Room";
        }


    }
}

