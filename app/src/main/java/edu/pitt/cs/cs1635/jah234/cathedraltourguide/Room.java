package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStorageDirectory;

public class Room extends Fragment {

    TextView intro;
    ImageView flag;
    View view;
    ImageSwitcher gallery;

    final int itemCode1 = 111;
    final int itemCode2 = 222;
    final int itemCode3 = 333;

    MediaPlayer mpIntro, mpHistory;

    ScrollView scrollview;
    LinearLayout fullScreen, objectInfo, moreInfo;
    TextView history_info;
    Button found1, found2, found3, jump_to_mid_screen, jump_to_bottom_screen, quiz, hint1, hint2, hint3, audio_intro, history_audio;
    ImageButton leftImage, rightImage;

    int index1 = 0, index2 = 0, index3 = 0, imageIndex = 0;
    InputStream stream;
    BufferedReader input;
    StringBuilder large_text;
    String line, selection;
    Drawable image;
    String[] hint = new String[9];
    File imageDir, photoFile;
    ArrayList<Uri> array;

    OnSendDataListener sendData;

    public Room() {
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

        //array holds Uri of images taken with camera
        array = new ArrayList<>();

        //folder to put new images in
        imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CathedralLearningTour/" + selection);

        //checks if exist
        if (!imageDir.exists())
        {
            //checks if make succeeded
            if (!imageDir.mkdirs())
                //should never go here
                Toast.makeText(getContext(), "Error: Failed to Make Save Directory in " + imageDir.getPath(), Toast.LENGTH_LONG).show();
        }

        //checks if can write to folder, which it should if we made the folder, but just in case
        if (imageDir.canWrite())
        {
            File[] files = imageDir.listFiles(); //grabs all images currently in folder, puts in array
            for (int i = 0; i< files.length; i++)
            {
                array.add(getFileUri(files[i]));
            }
        }
        else
            Toast.makeText(getContext(), "Error: Cannot Write to " + imageDir.getPath(), Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_room, container, false); //creates stuff we can see

        //initializes a lot of objects
        intro = (TextView) view.findViewById(R.id.intro);
        flag = (ImageView) view.findViewById(R.id.flag);
        hint1 = (Button) view.findViewById(R.id.hint1);
        hint2 = (Button) view.findViewById(R.id.hint2);
        hint3 = (Button) view.findViewById(R.id.hint3);
        quiz = (Button) view.findViewById(R.id.take_quiz);
        jump_to_mid_screen = (Button) view.findViewById(R.id.jumpmid);
        jump_to_bottom_screen = (Button) view.findViewById(R.id.jumpdown);
        scrollview = (ScrollView) view.findViewById(R.id.scrollbar);
        found1 = (Button) view.findViewById(R.id.found1);
        found2 = (Button) view.findViewById(R.id.found2);
        found3 = (Button) view.findViewById(R.id.found3);
        leftImage = (ImageButton) view.findViewById(R.id.leftButton);
        rightImage = (ImageButton) view.findViewById(R.id.rightButton);
        gallery = (ImageSwitcher) view.findViewById(R.id.imageSwitch);
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
            image = Drawable.createFromStream(stream, null); //creates drawable from stream
            stream.close();

            stream = getContext().getAssets().open(selection + "_hint.txt"); //creates new inputStream
            input = new BufferedReader(new InputStreamReader(stream)); //creates new bufferedreader
            for (int i=0; i< 9; i++) //reads all hints
            {
                hint[i] = input.readLine();
            }
            stream.close();
        }
        catch (Exception e)
        {
            //otherwise, nothing to show
            fullScreen.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Sorry. This Page Isn't Ready Yet", Toast.LENGTH_LONG).show();
        }

        intro.setText(large_text);
        flag.setImageDrawable(image);

        //sets up imageswitcher
        gallery.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
                return myView;
            }
        });

        //checks if any images currently in array = currently in folder
        if (array.size() > 0)
        {
            gallery.setImageURI(array.get(0)); //if so, show first image
        }
        else
        {
            gallery.setImageResource(R.drawable.ic_add); //if not, show the icon indicating take a picture
        }

        //cycle through three hints
        hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index1] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint1.setText(text);
                index1 = (index1 + 1) % 3;
            }
        });

        //cycle through three hints
        hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index2 + 3] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint2.setText(text);
                index2 = (index2 + 1) % 3;
            }
        });

        //cycle through three hints
        hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index3 + 6] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint3.setText(text);
                index3 = (index3 + 1) % 3;
            }
        });

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

        //if gallery image is clicked
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageIndex == array.size()) { //clicked is camera icon
                    if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) { //check if system has camera
                        try {
                            takePicture(); //take a picture
                        } catch (IOException e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show(); //some error happend
                        }
                    } else {
                        Toast.makeText(getContext(), "No Camera Detected", Toast.LENGTH_SHORT).show();
                    }
                }
                else //clicked is saved image
                {
                    //load Image Activity showing this image larger
                    Intent i = new Intent(getContext(), Image.class);
                    i.putExtra("Uri", array.get(imageIndex).toString());
                    i.putExtra("Room", selection);
                    i.putExtra("From", "Room");
                    startActivity(i);
                }
            }
        });

        //scroll to earlier images
        leftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (array.size() != 0 && imageIndex > 0) //array bounds checker
                {
                    imageIndex--;
                    gallery.setImageURI(array.get(imageIndex));
                }
                else
                    Toast.makeText(getContext(), "End of Gallery", Toast.LENGTH_SHORT).show();
            }
        });

        //scroll to later images
        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (array.size() != 0 && imageIndex < array.size()) //array bounds checker
                {
                    imageIndex++;
                    if (imageIndex == array.size())
                    {
                        gallery.setImageResource(R.drawable.ic_add); //if last, show camera icon instead of image
                    }
                    else
                    {
                        gallery.setImageURI(array.get(imageIndex));
                    }
                }
                else
                    Toast.makeText(getContext(), "End of Gallery", Toast.LENGTH_SHORT).show();
            }
        });

        //found first item
        found1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(getContext());
                final View promptUserInputView = li.inflate(R.layout.input_dialog_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                alertDialogBuilder.setView(promptUserInputView);

                final EditText userInput = (EditText) promptUserInputView.findViewById(R.id.userInputItemCode);

                alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                // Show input alert dialog
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Boolean closeDialog = false;

                        int input;
                        try {
                            input = Integer.parseInt(userInput.getText().toString());
                        } catch (NumberFormatException numbEx) {
                            input = 0;
                        }
                        if(input != itemCode1) {
                            TextView promptMessage = (TextView) promptUserInputView.findViewById(R.id.promptInput);
                            promptMessage.setText(R.string.wrongCode);
                        }
                        else {
                            // TODO - Found items aren't saved after the user leaves the room page, might need to edit the text page or add some indicator of found
                            found1.setText(R.string.rightCode);
                            found1.setEnabled(false);

                            hint1.setText("Temporary Item Found Text Disable Button");
                            hint1.setEnabled(false);

                            closeDialog = true;
                        }

                        if(closeDialog)
                            alertDialog.dismiss();
                    }
                });
            }
        });

        //found second item
        found2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(getContext());
                final View promptUserInputView = li.inflate(R.layout.input_dialog_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                alertDialogBuilder.setView(promptUserInputView);

                final EditText userInput = (EditText) promptUserInputView.findViewById(R.id.userInputItemCode);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                // Show input alert dialog
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Boolean closeDialog = false;

                        int input;
                        try {
                            input = Integer.parseInt(userInput.getText().toString());
                        } catch (NumberFormatException numbEx) {
                            input = 0;
                        }
                        if(input != itemCode2) {
                            TextView promptMessage = (TextView) promptUserInputView.findViewById(R.id.promptInput);
                            promptMessage.setText(R.string.wrongCode);
                        }
                        else {
                            // TODO - Found items aren't saved after the user leaves the room page, might need to edit the text page or add some indicator of found
                            found2.setText(R.string.rightCode);
                            found2.setEnabled(false);

                            hint2.setText("Temporary Item Found Text Disable Button");
                            hint2.setEnabled(false);

                            closeDialog = true;
                        }

                        if(closeDialog)
                            alertDialog.dismiss();
                    }
                });
            }
        });

        //found third item
        found3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(getContext());
                final View promptUserInputView = li.inflate(R.layout.input_dialog_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                alertDialogBuilder.setView(promptUserInputView);

                final EditText userInput = (EditText) promptUserInputView.findViewById(R.id.userInputItemCode);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                // Show input alert dialog
                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Boolean closeDialog = false;

                        int input;
                        try {
                            input = Integer.parseInt(userInput.getText().toString());
                        } catch (NumberFormatException numbEx) {
                            input = 0;
                        }
                        if(input != itemCode3) {
                            TextView promptMessage = (TextView) promptUserInputView.findViewById(R.id.promptInput);
                            promptMessage.setText(R.string.wrongCode);
                        }
                        else {
                            // TODO - Found items aren't saved after the user leaves the room page, might need to edit the text page or add some indicator of found
                            found3.setText(R.string.rightCode);
                            found3.setEnabled(false);

                            hint3.setText("Temporary Item Found Text Disable Button");
                            hint3.setEnabled(false);

                            closeDialog = true;
                        }

                        if(closeDialog)
                            alertDialog.dismiss();
                    }
                });
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

    private void takePicture() throws IOException {

        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //create new image file name
        photoFile = new File(imageDir.getPath(), imageFileName + ".jpg"); //create path of new image file

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //call camera activity
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(photoFile));

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        }
    }

    //action complete
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imgUri = getFileUri(photoFile); //create Uri (some identifier for images) of new image file
            gallery.setImageURI(imgUri); //shows in gallery
            array.add(imgUri); //saves in array
        }
    }

    //getUriForFile params: context, authority (just copy exactly), file object
    private Uri getFileUri(File file)
    {
        return FileProvider.getUriForFile(getContext(), "edu.pitt.cs.cs1635.jah234.cathedraltourguide.fileprovider", file);
    }
}

