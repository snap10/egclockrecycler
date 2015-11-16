package de.birk_home.egclock.egclockrecycler;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    //CreateView
    RelativeLayout overlay;
    NumberPicker nph;
    NumberPicker npm;
    NumberPicker nps;
    ImageView imageView;
    TextView textView;
    Button cancelButton;
    Button saveButton;
    Button startButton;
    TextView ringToneText;
    Uri ringToneUri;
    String picturePath;


    //MainView
    RecyclerView recList;
    LinearLayoutManager llm;
    TimerAdapter tadapter;
    List<MyTimer> timerList;
    FloatingActionButton fab;
    final static int CREATE_TIMER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        timerList = new ArrayList<MyTimer>();
        tadapter = new TimerAdapter(timerList, this);
        recList.setAdapter(tadapter);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTimerView();
                //Intent newTimerIntent = new Intent(getBaseContext(),CreateActivity.class);
                //startActivityForResult(newTimerIntent,CREATE_TIMER);

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        savedInstanceState.putSerializable("timerList", (Serializable) tadapter.getTimerList());
        savedInstanceState.putString("picturePath", picturePath);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<MyTimer> timerList = (ArrayList<MyTimer>) savedInstanceState.getSerializable("timerList");
        tadapter = new TimerAdapter(timerList, this);
        picturePath = savedInstanceState.getString("picturePath");
        recList.setAdapter(tadapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //GalleryIntent
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            picturePath = data.getData().toString();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(picturePath));


                imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //CameraIntent
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            if (picturePath == null) {

                picturePath = data.getData().toString();
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(picturePath));

                imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //NotificationIntent
        } else if (resultCode == Activity.RESULT_OK && requestCode == 3) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);


            if (uri != null) {
                ringToneUri = uri;
                ringToneText.setText(uri.toString());
            }

        }


    }

    /**
     * Creates the Overlay to create an TimerObject
     */
    private void createTimerView() {
        overlay = (RelativeLayout) findViewById(R.id.overlay_layout);
        overlay.setVisibility(View.VISIBLE);
        fab.setVisibility(View.INVISIBLE);
        nph = (NumberPicker) findViewById(R.id.numberPickerHours);
        nph.setMinValue(0);
        nph.setValue(0);
        nph.setMaxValue(99);


        npm = (NumberPicker) findViewById(R.id.numberPickerMinutes);
        npm.setValue(0);
        npm.setMinValue(0);
        npm.setMaxValue(99);

        nps = (NumberPicker) findViewById(R.id.numberPickerSeconds);
        nps.setMinValue(0);
        nps.setMaxValue(99);
        nps.setValue(0);


        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_photo));
        picturePath = Uri.parse("android.resource://de.birk_home.egclock.egclockrecycler/" + R.mipmap.ic_photo).toString();


        textView = (TextView) findViewById(R.id.nameText);
        textView.setText("");

        ringToneUri = Settings.System.DEFAULT_RINGTONE_URI;
        ringToneText = (TextView) findViewById(R.id.ringToneText);
        ringToneText.setText("Default");


        cancelButton = (Button) findViewById(R.id.cancelButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        startButton = (Button) findViewById(R.id.startButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.VISIBLE);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tadapter.addItem((createTimer()));
                overlay.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.VISIBLE);
            }
        });
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTimer timer = createTimer();
                timer.startTimer();
                tadapter.addItem(timer);

                overlay.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.VISIBLE);

            }
        });

    }


    private MyTimer createTimer() {
        MyTimer newTimer = new MyTimer(getTimeInSeconds(), textView.getText().toString(), picturePath, ringToneUri.toString(), tadapter);
        return newTimer;
    }

    private long getTimeInSeconds() {
        long timeInSeconds = (3600 * nph.getValue() + 60 * npm.getValue() + nps.getValue()) * 1000;
        return timeInSeconds;
    }

    /**
     * Method for dispatching OnClick Events from Views
     * @param v
     */
    public void onClickView(View v) {
        if (v.equals(findViewById(R.id.imageView))) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.editimage_menu);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.gallery:
                            getImage(true);
                            return true;
                        case R.id.camera:
                            getImage(false);
                            return true;
                        case R.id.cancel_edit_image:
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        } else if (v.equals(findViewById(R.id.ringToneLayout)) || v.equals(findViewById(R.id.ringToneButton))) {

            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            this.startActivityForResult(intent, 3);
        }

    }

    private void getImage(boolean gallery) {
        if (gallery == true) {
            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (pickImage.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(pickImage, 1);
            }
        } else if (gallery == false) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePicture.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();

                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    picturePath = Uri.fromFile(photoFile).toString();

                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePicture, 0);
                }

            }
        }
    }

    //Create FileStub to get URI for CameraIntent
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }


}
