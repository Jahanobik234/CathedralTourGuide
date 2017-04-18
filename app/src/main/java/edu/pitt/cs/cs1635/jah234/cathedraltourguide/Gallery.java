package edu.pitt.cs.cs1635.jah234.cathedraltourguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.widget.GridLayout.VERTICAL;

public class Gallery extends Fragment {

    View view;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    ImageButton cameraButton;
    Spinner category, direction;
    Button enter;

    File imageDir, photoFile;
    int current = -1;
    boolean dateOrder = true, downOrder = true;

    LinkedList<GalleryCardInfo> images, fImages;
    //LinkedList<String> headers;

    OnSendDataListener sendData;

    SharedPreferences keyPair;

    public Gallery() {
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

        keyPair = getContext().getSharedPreferences("saved_data", MODE_PRIVATE);

        images = new LinkedList<>();
        fImages = new LinkedList<>();
        //headers = new LinkedList<>();

        //folder to put new images in
        imageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CathedralLearningTour");

        //checks if exist
        if (!imageDir.exists())
        {
            //checks if make succeeded
            if (!imageDir.mkdirs())
                //should never go here
                Toast.makeText(getContext(), "Error: Write Permission Required to make Save Directory", Toast.LENGTH_LONG).show();
        }

        //checks if can write to folder, which it should if we made the folder, but just in case
        if (imageDir.canRead())
        {
            File[] files = imageDir.listFiles(); //grabs all images currently in folder, puts in array
            for (int i = 0; i < files.length; i++)
            {
                if (!files[i].isDirectory())
                    images.add(new GalleryCardInfo(getFileUri(files[i]), i, keyPair));
            }
            //fImages = new LinkedList<>(images);
            orderResults();
        }
        else
            Toast.makeText(getContext(), "Error: Read Permission Required to Load Images", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view != null) {
            category.setSelection(keyPair.getInt("CategorySpinner", 0));
            direction.setSelection(keyPair.getInt("DirectionSpinner", 0));
            enter.performClick();
            //orderResults();
            //adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.activity_gallery, container, false);

        cameraButton = (ImageButton) view.findViewById(R.id.cameraButton);
        category = (Spinner) view.findViewById(R.id.spinnerCategory);
        direction = (Spinner) view.findViewById(R.id.spinnerDirection);
        enter = (Button) view.findViewById(R.id.orderEnter);
        recyclerView = (RecyclerView) view.findViewById(R.id.galleryContent);
        layoutManager = new GridLayoutManager(getContext(), 3, VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new GalleryAdapter(getContext(), fImages, new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onImageClick(int position) {
                Intent i = new Intent(getContext(), Image.class);
                //Bitmap image = getBitmapFromFile(options, path, 512, 512);
                i.putExtra("Uri", images.get(position).getUri().toString());
                current = position;
                startActivityForResult(i, 2);
            }
        });
        recyclerView.setAdapter(adapter);

        ((GridLayoutManager)layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (fImages.get(position).getHeader() == null)
                    return 1;
                else
                    return 3;
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))  //check if system has camera
                    Toast.makeText(getContext(), "No Camera Detected", Toast.LENGTH_SHORT).show();
                else if (!imageDir.canWrite())
                    Toast.makeText(getContext(), "Write Permission Required to Take Pictures", Toast.LENGTH_SHORT).show();
                else
                {
                    try {
                        takePicture(); //take a picture
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "File Location Error", Toast.LENGTH_SHORT).show(); //some error happend
                    }
                }
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateOrder = (category.getSelectedItemPosition() == 0);
                downOrder = (direction.getSelectedItemPosition() == 0);
                orderResults();
                adapter.notifyDataSetChanged();
                SharedPreferences.Editor editor = keyPair.edit();
                editor.putInt("CategorySpinner", category.getSelectedItemPosition());
                editor.putInt("DirectionSpinner", direction.getSelectedItemPosition());
                editor.commit();
            }
        });

        return view;
    }

    private void takePicture() throws IOException {

        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //create new image file name
        photoFile = new File(imageDir.getPath(), imageFileName + ".png"); //create path of new image file

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
            current = images.size();
            images.add(new GalleryCardInfo(getFileUri(photoFile), current, keyPair)); //saves in array
            orderResults();
            adapter.notifyDataSetChanged();
            Intent i = new Intent(getContext(), Image.class);
            i.putExtra("Uri", getFileUri(photoFile).toString());
            startActivityForResult(i, 2);
        }
        if (requestCode == 2 && resultCode == RESULT_OK)
        {
            //Toast.makeText(getContext(), Uri.parse(data.getStringExtra("Uri")).getLastPathSegment(), Toast.LENGTH_SHORT).show();
            if (current == -1)
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            else
            {
                File temp = new File(imageDir, images.get(current).getUri().getLastPathSegment());
                if (!temp.delete())
                    Toast.makeText(getContext(), temp.getPath(), Toast.LENGTH_SHORT).show();
                else
                {
                    images.remove(current);
                    orderResults();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //getUriForFile params: context, authority (just copy exactly), file object
    private Uri getFileUri(File file)
    {
        return FileProvider.getUriForFile(getContext(), "edu.pitt.cs.cs1635.jah234.cathedraltourguide.fileprovider", file);
    }

    private void orderResults()
    {
        ArrayList<GalleryCardInfo> list;

        fImages.clear();

        if (!dateOrder) {
            GalleryCardInfo[] temp = images.toArray(new GalleryCardInfo[0]);
            Arrays.sort(temp, new locationComparator());
            list = new ArrayList<>(Arrays.asList(temp));
        }
        else
            list = new ArrayList<>(images);

        for (int i=0; i < list.size(); i++)
        {
            if (!downOrder) {
                fImages.add(0, list.get(i));
                if (i == list.size() - 1) {
                    fImages.add(0, new GalleryCardInfo(list.get(i).getHeader(dateOrder)));
                }
                else if (!list.get(i + 1).getHeader(dateOrder).equals(list.get(i).getHeader(dateOrder))) {
                    fImages.add(0, new GalleryCardInfo(list.get(i).getHeader(dateOrder)));
                }
            }
            else {
                if (i == 0) {
                    fImages.add(new GalleryCardInfo(list.get(0).getHeader(dateOrder)));
                }
                else if (!list.get(i).getHeader(dateOrder).equals(list.get(i - 1).getHeader(dateOrder))) {
                    fImages.add(new GalleryCardInfo(list.get(i).getHeader(dateOrder)));
                }
                fImages.add(list.get(i));
            }
        }
    }

    private class locationComparator implements Comparator<GalleryCardInfo> {

        @Override
        public int compare(GalleryCardInfo e1, GalleryCardInfo e2) {
            return e1.compare(e2);
        }
    }
}
