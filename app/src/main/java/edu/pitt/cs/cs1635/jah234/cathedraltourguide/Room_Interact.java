package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Set;

public class Room_Interact extends Fragment {

    ImageView item_pic1, item_pic2, item_pic3;
    TextView item_name1, item_name2, item_name3;
    View view;

    LinearLayout objectInfo;

    Button found1, found2, found3, quiz, hint1, hint2, hint3;

    int position, index1, index2, index3, imageIndex = 0;
    InputStream stream;
    BufferedReader input;
    String room, itemCode1, itemCode2, itemCode3;
    String[] hint = new String[9];

    OnSendDataListener sendData;

    SharedPreferences keyPair; //holds saved data

    public Room_Interact() {
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
        position = getArguments().getInt("Position");

        keyPair = getContext().getSharedPreferences("saved_data", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_room_interact, container, false); //creates stuff we can see

        //initializes a lot of objects
        hint1 = (Button) view.findViewById(R.id.hint1);
        hint2 = (Button) view.findViewById(R.id.hint2);
        hint3 = (Button) view.findViewById(R.id.hint3);
        item_name1 = (TextView) view.findViewById(R.id.name1);
        item_name2 = (TextView) view.findViewById(R.id.name2);
        item_name3 = (TextView) view.findViewById(R.id.name3);
        item_pic1 = (ImageView)view.findViewById(R.id.item_pic_1);
        item_pic2 = (ImageView)view.findViewById(R.id.item_pic_2);
        item_pic3 = (ImageView)view.findViewById(R.id.item_pic_3);
        //quiz = (Button) view.findViewById(R.id.take_quiz);
        found1 = (Button) view.findViewById(R.id.found1);
        found2 = (Button) view.findViewById(R.id.found2);
        found3 = (Button) view.findViewById(R.id.found3);
        objectInfo = (LinearLayout) view.findViewById(R.id.objectInfo);

        //tries to grab relevant info from assets
        try
        {
            room = getResources().obtainTypedArray(R.array.room_names).getString(position);

            InputStream stream = getResources().openRawResource(getResources().obtainTypedArray(R.array.room_hint).getResourceId(position, 0)); //creates new inputStream
            input = new BufferedReader(new InputStreamReader(stream)); //creates new bufferedreader
            item_name1.setText(input.readLine());
            item_name2.setText(input.readLine());
            item_name3.setText(input.readLine());
            itemCode1 = input.readLine();
            itemCode2 = input.readLine();
            itemCode3 = input.readLine();
            for (int i=0; i< 9; i++) //reads all hints
            {
                hint[i] = input.readLine();
            }
            stream.close();

            Glide.with(getContext()).load(getResources().obtainTypedArray(R.array.room_object).getResourceId(position * 3, 0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(item_pic1);
            Glide.with(getContext()).load(getResources().obtainTypedArray(R.array.room_object).getResourceId(position * 3 + 1, 0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(item_pic2);
            Glide.with(getContext()).load(getResources().obtainTypedArray(R.array.room_object).getResourceId(position * 3 + 2, 0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(item_pic3);
        }
        catch (Exception e)
        {
            //otherwise, nothing to show
            objectInfo.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Sorry. This Page isn't Ready Yet", Toast.LENGTH_SHORT).show();
        }

        if (keyPair.getInt(position + "Item1", -1) != -1)
        {
            found1.setText(R.string.rightCode);
            found1.setEnabled(false);
            hint1.setEnabled(false);
        }

        if (keyPair.getInt(position + "Item2", -1) != -1)
        {
            found2.setText(R.string.rightCode);
            found2.setEnabled(false);
            hint2.setEnabled(false);
        }

        if (keyPair.getInt(position + "Item3", -1) != -1)
        {
            found3.setText(R.string.rightCode);
            found3.setEnabled(false);
            hint3.setEnabled(false);
        }

        //cycle through three hints
        hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[index1 % 3] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint1.setText(text);
                index1++;
            }
        });

        //cycle through three hints
        hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[(index2 % 3) + 3] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint2.setText(text);
                index2++;
            }
        });

        //cycle through three hints
        hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = hint[(index3 % 3) + 6] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint3.setText(text);
                index3++;
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

                final TextView promptMessage = (TextView) promptUserInputView.findViewById(R.id.promptInput);
                final EditText userInput = (EditText) promptUserInputView.findViewById(R.id.userInputItemCode);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(userInput.getText().toString().equals(itemCode1)) {
                                    found1.setText(R.string.rightCode);
                                    found1.setEnabled(false);
                                    hint1.setEnabled(false);

                                    SharedPreferences.Editor editor = keyPair.edit();

                                    editor.putInt(position + "Item1", 10);
                                    if (keyPair.getInt("Total Score", -1) == -1) //checks if total score exists
                                    {
                                        editor.putInt("Total Score", 10); //adds new if not
                                    }
                                    else
                                    {
                                        editor.putInt("Total Score", (10 + keyPair.getInt("Total Score", -1))); //adds to total score and puts in storage
                                    }
                                    Set<String> temp = keyPair.getStringSet("achievementSet", null); //pulls list of achievements if exists
                                    if (temp == null)
                                    {
                                        temp = new ArraySet<>(); //makes new one if not
                                    }
                                    temp.add("Found Item1 of " + room + " Room: 10 Points"); //adds to list
                                    editor.putStringSet("achievementSet", temp); //puts set in storage
                                    editor.apply();

                                    dialog.dismiss();

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Item Found!");
                                    confirm.setMessage("You earned 10 points");
                                    confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog confirmDisplay = confirm.create();
                                    confirmDisplay.show();
                                }
                                else
                                {
                                    promptMessage.setText(R.string.wrongCode);

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Incorrect");
                                    confirm.setMessage("Sorry, that is not the correct code for this item");
                                    confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog confirmDisplay = confirm.create();
                                    confirmDisplay.show();
                                }
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

                final TextView promptMessage = (TextView) promptUserInputView.findViewById(R.id.promptInput);
                final EditText userInput = (EditText) promptUserInputView.findViewById(R.id.userInputItemCode);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                if(userInput.getText().toString().equals(itemCode2)) {
                                    found2.setText(R.string.rightCode);
                                    found2.setEnabled(false);
                                    hint2.setEnabled(false);

                                    SharedPreferences.Editor editor = keyPair.edit();

                                    editor.putInt(position + "Item2", 10);
                                    if (keyPair.getInt("Total Score", -1) == -1) //checks if total score exists
                                    {
                                        editor.putInt("Total Score", 10); //adds new if not
                                    }
                                    else
                                    {
                                        editor.putInt("Total Score", (10 + keyPair.getInt("Total Score", -1))); //adds to total score and puts in storage
                                    }
                                    Set<String> temp = keyPair.getStringSet("achievementSet", null); //pulls list of achievements if exists
                                    if (temp == null)
                                    {
                                        temp = new ArraySet<>(); //makes new one if not
                                    }
                                    temp.add("Found Item2 of " + room + " Room: 10 Points"); //adds to list
                                    editor.putStringSet("achievementSet", temp); //puts set in storage
                                    editor.apply();

                                    dialog.dismiss();

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Item Found!");
                                    confirm.setMessage("You earned 10 points");
                                    confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog confirmDisplay = confirm.create();
                                    confirmDisplay.show();
                                }
                                else
                                {
                                    promptMessage.setText(R.string.wrongCode);

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Incorrect");
                                    confirm.setMessage("Sorry, that is not the correct code for this item");
                                    confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog confirmDisplay = confirm.create();
                                    confirmDisplay.show();
                                }
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

                final TextView promptMessage = (TextView) promptUserInputView.findViewById(R.id.promptInput);
                final EditText userInput = (EditText) promptUserInputView.findViewById(R.id.userInputItemCode);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(userInput.getText().toString().equals(itemCode3)) {
                                    found3.setText(R.string.rightCode);
                                    found3.setEnabled(false);
                                    hint3.setEnabled(false);

                                    SharedPreferences.Editor editor = keyPair.edit();

                                    editor.putInt(position + "Item3", 10);
                                    if (keyPair.getInt("Total Score", -1) == -1) //checks if total score exists
                                    {
                                        editor.putInt("Total Score", 10); //adds new if not
                                    }
                                    else
                                    {
                                        editor.putInt("Total Score", (10 + keyPair.getInt("Total Score", -1))); //adds to total score and puts in storage
                                    }
                                    Set<String> temp = keyPair.getStringSet("achievementSet", null); //pulls list of achievements if exists
                                    if (temp == null)
                                    {
                                        temp = new ArraySet<>(); //makes new one if not
                                    }
                                    temp.add("Found Item3 of " + room + " Room: 10 Points"); //adds to list
                                    editor.putStringSet("achievementSet", temp); //puts set in storage
                                    editor.apply();

                                    dialog.dismiss();

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Item Found!");
                                    confirm.setMessage("You earned 10 points");
                                    confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog confirmDisplay = confirm.create();
                                    confirmDisplay.show();
                                }
                                else
                                {
                                    promptMessage.setText(R.string.wrongCode);

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Incorrect");
                                    confirm.setMessage("Sorry, that is not the correct code for this item");
                                    confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog confirmDisplay = confirm.create();
                                    confirmDisplay.show();
                                }
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
            }
        });

        //always return view
        return view;
    }
}

