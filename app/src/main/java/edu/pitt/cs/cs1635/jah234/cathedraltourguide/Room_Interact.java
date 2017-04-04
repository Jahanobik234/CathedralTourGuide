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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Set;

public class Room_Interact extends Fragment {

    ImageView item_pic1, item_pic2, item_pic3;
    View view;
    ImageSwitcher gallery;

    LinearLayout objectInfo;

    Button found1, found2, found3, quiz, hint1, hint2, hint3;
    ImageButton leftImage, rightImage;

    int imageIndex = 0;
    InputStream stream;
    BufferedReader input;
    StringBuilder large_text;
    String line, selection, itemCode1, itemCode2, itemCode3;
    Drawable pic1_image, pic2_image, pic3_image;
    String[] hint = new String[9];
    File imageDir, photoFile;
    LinkedList<Uri> array;

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
        selection = getArguments().getString("Selection");

        //array holds Uri of images taken with camera
        /*array = new LinkedList<>();

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
            Toast.makeText(getContext(), "Error: Cannot Write to " + imageDir.getPath(), Toast.LENGTH_LONG).show();*/
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

        view = inflater.inflate(R.layout.activity_room_interact, container, false); //creates stuff we can see

        //initializes a lot of objects
        hint1 = (Button) view.findViewById(R.id.hint1);
        hint2 = (Button) view.findViewById(R.id.hint2);
        hint3 = (Button) view.findViewById(R.id.hint3);
        item_pic1 = (ImageView)view.findViewById(R.id.item_pic_1);
        item_pic2 = (ImageView)view.findViewById(R.id.item_pic_2);
        item_pic3 = (ImageView)view.findViewById(R.id.item_pic_3);
        //quiz = (Button) view.findViewById(R.id.take_quiz);
        found1 = (Button) view.findViewById(R.id.found1);
        found2 = (Button) view.findViewById(R.id.found2);
        found3 = (Button) view.findViewById(R.id.found3);
        objectInfo = (LinearLayout) view.findViewById(R.id.objectInfo);
        //leftImage = (ImageButton) view.findViewById(R.id.leftButton);
        //rightImage = (ImageButton) view.findViewById(R.id.rightButton);
        //gallery = (ImageSwitcher) view.findViewById(R.id.imageSwitch);

        //tries to grab relevant info from assets
        try
        {
            stream = getContext().getAssets().open(selection + "_hint.txt"); //creates new inputStream
            input = new BufferedReader(new InputStreamReader(stream)); //creates new bufferedreader
            itemCode1 = input.readLine();
            itemCode2 = input.readLine();
            itemCode3 = input.readLine();
            for (int i=0; i< 9; i++) //reads all hints
            {
                hint[i] = input.readLine();
            }
            stream.close();
        }
        catch (Exception e)
        {
            //otherwise, nothing to show
            objectInfo.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Sorry. This Page isn't Ready Yet", Toast.LENGTH_SHORT).show();
        }

        //item_pic1.setImageDrawable(pic1_image);
        //item_pic2.setImageDrawable(pic2_image);
        //item_pic3.setImageDrawable(pic3_image);


        //sets up imageswitcher
        /*gallery.setFactory(new ViewSwitcher.ViewFactory() {
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
        }*/

        if (keyPair.getInt(selection + "Item1", -1) != -1)
        {
            found1.setText(R.string.rightCode);
            found1.setEnabled(false);
            hint1.setEnabled(false);
        }

        if (keyPair.getInt(selection + "Item2", -1) != -1)
        {
            found2.setText(R.string.rightCode);
            found2.setEnabled(false);
            hint2.setEnabled(false);
        }

        if (keyPair.getInt(selection + "Item3", -1) != -1)
        {
            found3.setText(R.string.rightCode);
            found3.setEnabled(false);
            hint3.setEnabled(false);
        }

        //cycle through three hints
        hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = keyPair.getInt(selection + "Hint1", 0);
                String text = hint[index % 3] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint1.setText(text);
                index++;
                SharedPreferences.Editor editor = keyPair.edit();
                editor.putInt(selection + "Hint1", index);
                editor.commit();
            }
        });

        //cycle through three hints
        hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = keyPair.getInt(selection + "Hint2", 0);
                String text = hint[(index % 3) + 3] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint2.setText(text);
                index++;
                SharedPreferences.Editor editor = keyPair.edit();
                editor.putInt(selection + "Hint2", index);
                editor.commit();
            }
        });

        //cycle through three hints
        hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = keyPair.getInt(selection + "Hint3", 0);
                String text = hint[(index % 3) + 6] + "\n\nStill Can't Find it?\nTap for Another Hint!";
                hint3.setText(text);
                index++;
                SharedPreferences.Editor editor = keyPair.edit();
                editor.putInt(selection + "Hint3", index);
                editor.commit();
            }
        });

        //tell MainActivity go to Quiz fragment
        /*quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("Action", "Room Quiz");
                data.putString("Room Name", selection);
                sendData.send(data);
            }
        });*/

        /*//if gallery image is clicked
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
                    startActivityForResult(i, 2);
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
        });*/

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
                                    int score = 10 - 3 * keyPair.getInt(selection + "Hint1", 0);
                                    if (score < 0)
                                        score = 0;
                                    editor.putInt(selection + "Item1", score);
                                    if (keyPair.getInt("Total Score", -1) == -1) //checks if total score exists
                                    {
                                        editor.putInt("Total Score", score); //adds new if not
                                    }
                                    else
                                    {
                                        editor.putInt("Total Score", (score + keyPair.getInt("Total Score", -1))); //adds to total score and puts in storage
                                    }
                                    Set<String> temp = keyPair.getStringSet("achievementSet", null); //pulls list of achievements if exists
                                    if (temp == null)
                                    {
                                        temp = new ArraySet<>(); //makes new one if not
                                    }
                                    temp.add("Found Item1 of " + selection + " Room: " + Integer.toString(score) + " Points"); //adds to list
                                    editor.putStringSet("achievementSet", temp); //puts set in storage
                                    editor.apply();

                                    dialog.dismiss();

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Item Found!");
                                    confirm.setMessage("You used " + keyPair.getInt(selection + "Hint1", 0) + " hints and earned " + keyPair.getInt(selection + "Item1", 10) + " points");
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
                                    int score = 10 - 3 * keyPair.getInt(selection + "Hint2", 0);
                                    if (score < 0)
                                        score = 0;
                                    editor.putInt(selection + "Item2", score);
                                    if (keyPair.getInt("Total Score", -1) == -1) //checks if total score exists
                                    {
                                        editor.putInt("Total Score", score); //adds new if not
                                    }
                                    else
                                    {
                                        editor.putInt("Total Score", (score + keyPair.getInt("Total Score", -1))); //adds to total score and puts in storage
                                    }
                                    Set<String> temp = keyPair.getStringSet("achievementSet", null); //pulls list of achievements if exists
                                    if (temp == null)
                                    {
                                        temp = new ArraySet<>(); //makes new one if not
                                    }
                                    temp.add("Found Item2 of " + selection + " Room: " + Integer.toString(score) + " Points"); //adds to list
                                    editor.putStringSet("achievementSet", temp); //puts set in storage
                                    editor.apply();

                                    dialog.dismiss();

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Item Found!");
                                    confirm.setMessage("You used " + keyPair.getInt(selection + "Hint2", 0) + " hints and earned " + keyPair.getInt(selection + "Item2", 10) + " points");
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
                                    int score = 10 - 3 * keyPair.getInt(selection + "Hint3", 0);
                                    if (score < 0)
                                        score = 0;
                                    editor.putInt(selection + "Item3", score);
                                    if (keyPair.getInt("Total Score", -1) == -1) //checks if total score exists
                                    {
                                        editor.putInt("Total Score", score); //adds new if not
                                    }
                                    else
                                    {
                                        editor.putInt("Total Score", (score + keyPair.getInt("Total Score", -1))); //adds to total score and puts in storage
                                    }
                                    Set<String> temp = keyPair.getStringSet("achievementSet", null); //pulls list of achievements if exists
                                    if (temp == null)
                                    {
                                        temp = new ArraySet<>(); //makes new one if not
                                    }
                                    temp.add("Found Item3 of " + selection + " Room: " + Integer.toString(score) + " Points"); //adds to list
                                    editor.putStringSet("achievementSet", temp); //puts set in storage
                                    editor.apply();

                                    dialog.dismiss();

                                    AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                                    confirm.setTitle("Item Found!");
                                    confirm.setMessage("You used " + keyPair.getInt(selection + "Hint3", 0) + " hints and earned " + keyPair.getInt(selection + "Item3", 10) + " points");
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

        //always return view*/
        return view;
    }

    /*private void takePicture() throws IOException {

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
        if (requestCode == 2 && resultCode == 2)
        {
            File temp = new File(imageDir, array.remove(imageIndex).getLastPathSegment());
            if (!temp.delete())
                Toast.makeText(getContext(), temp.getPath(), Toast.LENGTH_SHORT).show();
            if (imageIndex == array.size())
            {
                gallery.setImageResource(R.drawable.ic_add); //if last, show camera icon instead of image
            }
            else
            {
                gallery.setImageURI(array.get(imageIndex));
            }
        }
    }

    //getUriForFile params: context, authority (just copy exactly), file object
    private Uri getFileUri(File file)
    {
        return FileProvider.getUriForFile(getContext(), "edu.pitt.cs.cs1635.jah234.cathedraltourguide.fileprovider", file);
    }*/
}

