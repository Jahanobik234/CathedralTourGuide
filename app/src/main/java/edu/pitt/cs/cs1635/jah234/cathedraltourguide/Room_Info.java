package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Room_Info extends Fragment {

    TextView roomName, roomNum, intro, history, intro_timer, history_timer;
    View view;
    ImageView flag;
    ImageButton audio_intro, history_audio;
    SeekBar intro_progress, history_progress;
    LinearLayout fullScreen;

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
        //keyPair = saved data
        //keyPair.get Params: key to identify value to fetch, value to return if can't find in saved data
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
        history = (TextView) view.findViewById(R.id.history);
        audio_intro = (ImageButton) view.findViewById(R.id.audio_intro);
        intro_progress = (SeekBar) view.findViewById(R.id.intro_seekBar);
        intro_timer = (TextView) view.findViewById(R.id.intro_timer);
        history_audio = (ImageButton) view.findViewById(R.id.history_audio);
        history_progress = (SeekBar) view.findViewById(R.id.history_seekBar);
        history_timer = (TextView) view.findViewById(R.id.history_timer);
        fullScreen = (LinearLayout) view.findViewById(R.id.fullScreen);

        //tries to grab relevant info from assets
        try
        {
            roomName.setText(getResources().obtainTypedArray(R.array.room_names).getString(position) + " Room");
            roomNum.setText("Room " + getResources().obtainTypedArray(R.array.room_numbers).getString(position));

            InputStream stream = getResources().openRawResource(getResources().obtainTypedArray(R.array.room_text).getResourceId(position, 0));
            BufferedReader input = new BufferedReader(new InputStreamReader(stream)); //creates new bufferedreader
            String line;
            //stream = getContext().getAssets().open(selection + "_text.txt"); //creates new inputStream
            //large_text = new StringBuilder(stream.available()); //creates stringbuilder to store info from stream
            //while ((line = input.readLine()) != null) //keep reading lines
            //{
            //    large_text.append(line).append("\n");
            //}
            if ((line = input.readLine()) != null)
            {
                intro.setText(line);
            }
            if ((line = input.readLine()) != null)
            {
                history.setText(line);
            }
            stream.close();

            //stream = getContext().getAssets().open(selection + "_flag.png"); //creates new inputStream
            //flag_image = Drawable.createFromStream(stream, null); //creates drawable from stream
            //flag.setImageDrawable(Drawable.createFromStream(stream, null));
            flag.setImageResource(getResources().obtainTypedArray(R.array.room_flag).getResourceId(position, 0));

            mpIntro = MediaPlayer.create(getContext(), getResources().obtainTypedArray(R.array.room_audio).getResourceId(position * 2, 0));
            mpHistory = MediaPlayer.create(getContext(), getResources().obtainTypedArray(R.array.room_audio).getResourceId(position * 2 + 1, 0));
            //stream.close();

            //Toast.makeText(getContext(), Integer.toString(R.raw.african_heritage_text) + "\t" + Integer.toString(R.drawable.africanheritage_flag), Toast.LENGTH_LONG).show();
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

