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
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArraySet;
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

public class Room_Info extends Fragment {

    TextView intro;
    View view;
    ImageView flag;

    MediaPlayer mpIntro, mpHistory;

    ScrollView scrollview;
    LinearLayout fullScreen, objectInfo, moreInfo;
    TextView history_info;
    Button found1, found2, found3, jump_to_mid_screen, jump_to_bottom_screen, quiz, hint1, hint2, hint3, audio_intro, history_audio;

    int index1 = 0, index2 = 0, index3 = 0, imageIndex = 0;
    InputStream stream;
    BufferedReader input;
    StringBuilder large_text;
    String line, selection, itemCode1, itemCode2, itemCode3;
    Drawable flag_image, pic1_image, pic2_image, pic3_image;
    String[] hint = new String[9];

    OnSendDataListener sendData;

    SharedPreferences keyPair; //holds saved data

    public Room_Info() {
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

        //initializes value of current room
        selection = getArguments().getString("Selection");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //keyPair = saved data
        //keyPair.get Params: key to identify value to fetch, value to return if can't find in saved data
        keyPair = getContext().getSharedPreferences("saved_data", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_room, container, false); //creates stuff we can see

        //initializes a lot of objects
        intro = (TextView) view.findViewById(R.id.intro);
        flag = (ImageView) view.findViewById(R.id.flag);
        quiz = (Button) view.findViewById(R.id.take_quiz);
        jump_to_mid_screen = (Button) view.findViewById(R.id.jumpmid);
        jump_to_bottom_screen = (Button) view.findViewById(R.id.jumpdown);
        scrollview = (ScrollView) view.findViewById(R.id.scrollbar);
        fullScreen = (LinearLayout) view.findViewById(R.id.fullScreen);
        objectInfo = (LinearLayout) view.findViewById(R.id.objectInfo);
        moreInfo = (LinearLayout) view.findViewById(R.id.moreInfo);
        audio_intro = (Button) view.findViewById(R.id.audio_intro);
        mpIntro = MediaPlayer.create(getContext(), R.raw.african_heritage_audio_architecture);
        history_audio = (Button) view.findViewById(R.id.history_audio);
        mpHistory = MediaPlayer.create(getContext(), R.raw.african_heritage_audio_display_case);

        //tries to grab relevant info from assets
        try
        {
            stream = getContext().getAssets().open(selection + "_intro.txt"); //creates new inputStream
            large_text = new StringBuilder(stream.available()); //creates stringbuilder to store info from stream
            input = new BufferedReader(new InputStreamReader(stream)); //creates new bufferedreader
            while ((line = input.readLine()) != null) //keep reading lines
            {
                large_text.append(line).append("\n");
            }
            stream.close();

            stream = getContext().getAssets().open(selection + "_flag.png"); //creates new inputStream
            flag_image = Drawable.createFromStream(stream, null); //creates drawable from stream
            stream.close();
        }
        catch (Exception e)
        {
            //otherwise, nothing to show
            fullScreen.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Sorry. This Page Isn't Ready Yet", Toast.LENGTH_LONG).show();
        }

        intro.setText(large_text);
        flag.setImageDrawable(flag_image);

        //tell MainActivity go to Quiz fragment
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("Action", "Room Quiz");
                data.putString("Room Name", selection);
                sendData.send(data);
            }
        });

        //scroll down to notable items
        jump_to_mid_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollview.scrollTo(0, objectInfo.getTop());
            }
        });

        //scroll down to just for fun
        jump_to_bottom_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollview.scrollTo(0, moreInfo.getTop());
            }
        });
        //plays intro audio
        audio_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user taps the audio button and the audio is already playing then pause it.
                // Change the text to play audio.
                if (mpIntro.isPlaying()) {
                    audio_intro.setText(R.string.audio_button_play);
                    mpIntro.pause();
                }
                // If the user taps the audio button and the audio isn't playing then pause any other
                // running audio files then play the audio that the user selected.
                else {
                    if (mpHistory.isPlaying()) {
                        history_audio.setText(R.string.audio_button_play);
                        mpHistory.pause();
                    }
                    audio_intro.setText(R.string.audio_button_pause);
                    mpIntro.start();
                }
            }
        });

        mpIntro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                audio_intro.setText(R.string.audio_button_play);
            }
        });

        //plays history audio
        history_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpHistory.isPlaying()) {
                    history_audio.setText(R.string.audio_button_play);
                    mpHistory.pause();
                }
                else {
                    if (mpIntro.isPlaying()) {
                        audio_intro.setText(R.string.audio_button_play);
                        mpIntro.pause();
                    }
                    history_audio.setText(R.string.audio_button_pause);
                    mpHistory.start();
                }
            }
        });

        mpHistory.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                history_audio.setText(R.string.audio_button_play);
            }
        });

        //always return view
        return view;
    }
}

