package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.widget.EditText;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Room extends Fragment {

    TextView intro;
    ImageView flag;
    View view;

    final int itemCode1 = 111;
    final int itemCode2 = 222;
    final int itemCode3 = 333;

    ScrollView scrollview;
    TextView history_info, list_header;
    ImageView item_pic_1, item_pic_2, item_pic_3;
    Button found1, found2, found3;
    Button audio, jump_to_bottom_screen, openCamera;

    int index1 = 0, index2 = 0, index3 = 0;
    InputStream stream;
    BufferedReader input;
    StringBuilder large_text;
    String line;
    Drawable image;
    String selection;
    String[] hint = new String[9];
    Button quiz, hint1, hint2, hint3;

    OnSendDataListener sendData;

    public Room() {
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
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_room, container, false);

        intro = (TextView) view.findViewById(R.id.intro);
        flag = (ImageView) view.findViewById(R.id.flag);
        hint1 = (Button) view.findViewById(R.id.hint1);
        hint2 = (Button) view.findViewById(R.id.hint2);
        hint3 = (Button) view.findViewById(R.id.hint3);
        quiz = (Button) view.findViewById(R.id.take_quiz);
        jump_to_bottom_screen = (Button) view.findViewById(R.id.jumpdown);
        scrollview = (ScrollView) view.findViewById(R.id.scrollbar);
        found1 = (Button) view.findViewById(R.id.found1);
        found2 = (Button) view.findViewById(R.id.found2);
        found3 = (Button) view.findViewById(R.id.found3);
        openCamera = (Button) view.findViewById(R.id.camera);


        selection = getArguments().getString("Selection");

        try
        {
            stream = getContext().getAssets().open(selection + "_intro.txt");
            large_text = new StringBuilder(stream.available());
            input = new BufferedReader(new InputStreamReader(stream));
            while ((line = input.readLine()) != null)
            {
                large_text.append(line).append("\n");
            }
            stream.close();

            stream = getContext().getAssets().open(selection + "_flag.png");
            image = Drawable.createFromStream(stream, null);
            stream.close();

            stream = getContext().getAssets().open(selection + "_hint.txt");
            input = new BufferedReader(new InputStreamReader(stream));
            for (int i=0; i< 9; i++)
            {
                hint[i] = input.readLine();
            }
            stream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        intro.setText(large_text);
        flag.setImageDrawable(image);

        hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index1] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint1.setText(text);
                index1 = (index1 + 1) % 3;
            }
        });
        hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index2 + 3] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint2.setText(text);
                index2 = (index2 + 1) % 3;
            }
        });
        hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index3 + 6] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint3.setText(text);
                index3 = (index3 + 1) % 3;
            }
        });

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("Action", "Room Quiz");
                data.putString("Room Name", selection);
                sendData.send(data);
            }
        });

        jump_to_bottom_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
            }
        });

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
        return view;
    }
}

