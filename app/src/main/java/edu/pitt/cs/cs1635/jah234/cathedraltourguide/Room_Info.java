package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.widget.*;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Room_Info extends Fragment {

    TextView roomName, roomNum, intro, intro_timer, history_timer;
    View view;
    ImageView flag;
    ImageButton audio_intro, history_audio;
    SeekBar intro_progress, history_progress;
    LinearLayout fullScreen;
    Button selectTopic;
    WebView history;
    Spinner topicList;
    String[] listTopics;
    CharSequence[] selectedRoomSpinnerInfo;

    MediaPlayer mpIntro, mpHistory;

    int position;

    OnSendDataListener sendData;

    SharedPreferences keyPair; //holds saved data
    AudioHelper audioHelper;
    Handler handler;

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

        position = getArguments().getInt("Position");
        keyPair = getContext().getSharedPreferences("saved_data", Context.MODE_PRIVATE);
        audioHelper = new AudioHelper();
        handler = new Handler();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_room_info, container, false); //creates stuff we can see

        //initializes a lot of objects
        roomName = (TextView) view.findViewById(R.id.roomName);
        roomNum = (TextView) view.findViewById(R.id.roomNum);
        intro = (TextView) view.findViewById(R.id.intro);
        flag = (ImageView) view.findViewById(R.id.flag);
        audio_intro = (ImageButton) view.findViewById(R.id.audio_intro);
        intro_progress = (SeekBar) view.findViewById(R.id.intro_seekBar);
        intro_timer = (TextView) view.findViewById(R.id.intro_timer);
        history_audio = (ImageButton) view.findViewById(R.id.history_audio);
        history_progress = (SeekBar) view.findViewById(R.id.history_seekBar);
        history_timer = (TextView) view.findViewById(R.id.history_timer);
        fullScreen = (LinearLayout) view.findViewById(R.id.fullScreen);
        selectTopic = (Button) view.findViewById(R.id.selectSection);
        history = (WebView) view.findViewById(R.id.history);

        TypedArray typeArray = getResources().obtainTypedArray(R.array.roomInfoArrays);
        selectedRoomSpinnerInfo = typeArray.getTextArray(position);
        typeArray.recycle();

        topicList = (Spinner) view.findViewById(R.id.topic);

        ArrayAdapter<CharSequence> adapterPackType = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, selectedRoomSpinnerInfo);
        adapterPackType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicList.setAdapter(adapterPackType);

        //tries to grab relevant info from assets
        try
        {
            roomName.setText(getResources().obtainTypedArray(R.array.room_names).getString(position) + " Room");
            roomNum.setText("Room " + getResources().obtainTypedArray(R.array.room_numbers).getString(position));

            intro.setText(getResources().getStringArray(R.array.room_intro)[position]);

            Glide.with(getContext()).load(getResources().obtainTypedArray(R.array.room_flag).getResourceId(position, 0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(flag);

            mpIntro = MediaPlayer.create(getContext(), getResources().obtainTypedArray(R.array.room_audio).getResourceId(position * 2, 0));
            mpHistory = MediaPlayer.create(getContext(), getResources().obtainTypedArray(R.array.room_audio).getResourceId(position * 2 + 1, 0));
        }
        catch (Exception e)
        {
            //otherwise, nothing to show
            fullScreen.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Sorry. This Page isn't Ready Yet", Toast.LENGTH_SHORT).show();
        }


        //plays intro audio
        audio_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user taps the audio button and the audio is already playing then pause it.
                // Change the text to play audio.
                if (mpIntro.isPlaying()) {
                    audio_intro.setImageResource(R.drawable.ic_action_play);
                    mpIntro.pause();
                    handler.removeCallbacks(updateIntroTime);
                }
                // If the user taps the audio button and the audio isn't playing then pause any other
                // running audio files then play the audio that the user selected.
                else {
                    if (mpHistory.isPlaying()) {
                        history_audio.setImageResource(R.drawable.ic_action_play);
                        mpHistory.pause();
                        handler.removeCallbacks(updateHistoryTime);
                    }
                    audio_intro.setImageResource(R.drawable.ic_action_pause);
                    mpIntro.start();
                    intro_progress.setMax(100);
                    handler.postDelayed(updateIntroTime, 100);
                }
            }
        });

        intro_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateIntroTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateIntroTime);

                mpIntro.seekTo(audioHelper.getSeek(seekBar.getProgress(), mpIntro.getDuration()));

                handler.postDelayed(updateIntroTime, 100);
            }
        });

        if (mpIntro != null)
            mpIntro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    audio_intro.setImageResource(R.drawable.ic_action_play);
                    intro_progress.setProgress(0);
                    intro_timer.setText("00:00");
                    handler.removeCallbacks(updateIntroTime);
                }
            });

        //plays history audio
        history_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpHistory.isPlaying()) {
                    history_audio.setImageResource(R.drawable.ic_action_play);
                    mpHistory.pause();
                    handler.removeCallbacks(updateHistoryTime);
                }
                else {
                    if (mpIntro.isPlaying()) {
                        audio_intro.setImageResource(R.drawable.ic_action_play);
                        mpIntro.pause();
                        handler.removeCallbacks(updateIntroTime);
                    }
                    history_audio.setImageResource(R.drawable.ic_action_pause);
                    mpHistory.start();
                    history_progress.setMax(100);
                    handler.postDelayed(updateHistoryTime, 100);
                }
            }
        });

        history_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateHistoryTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateHistoryTime);

                mpHistory.seekTo(audioHelper.getSeek(seekBar.getProgress(), mpHistory.getDuration()));

                handler.postDelayed(updateHistoryTime, 100);
            }
        });

        if (mpHistory != null)
            mpHistory.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    history_audio.setImageResource(R.drawable.ic_action_play);
                    history_progress.setProgress(0);
                    history_timer.setText("00:00");
                    handler.removeCallbacks(updateHistoryTime);
                }
            });

        selectTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topChoice = topicList.getSelectedItem().toString();

                //Notes: Make sure that the file name contains the room name and the specific topic in the html file
                // Example: African Heritage History.html

                if(!topChoice.equals("Tap to select topic")) {
                    history.loadUrl("file:///android_asset/" + getResources().obtainTypedArray(R.array.room_names).getString(position) + " " + topChoice + ".html");
                }

            }
        });

        //always return view*/
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mpIntro != null)
            mpIntro.pause();
        if (mpHistory != null)
            mpHistory.pause();
    }

    private Runnable updateIntroTime = new Runnable() {
        public void run() {
            long totalDuration = mpIntro.getDuration();
            long currentDuration = mpIntro.getCurrentPosition();

            // Displaying time completed playing
            intro_timer.setText(audioHelper.getTime(currentDuration));

            // Updating progress bar
            intro_progress.setProgress(audioHelper.getPercent(currentDuration, totalDuration));

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };

    private Runnable updateHistoryTime = new Runnable() {
        public void run() {
            long totalDuration = mpHistory.getDuration();
            long currentDuration = mpHistory.getCurrentPosition();

            // Displaying time completed playing
            history_timer.setText(audioHelper.getTime(currentDuration));

            // Updating progress bar
            history_progress.setProgress(audioHelper.getPercent(currentDuration, totalDuration));

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };
}
