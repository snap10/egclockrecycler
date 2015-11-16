package de.birk_home.egclock.egclockrecycler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Activity for Editing an already initialized Timer
 */
public class EditActivity extends AppCompatActivity {

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

    int index;
    int[] timeHMS;
    Bitmap picture;
    String name;
    Uri notification;
    Uri picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent incomming = getIntent();
        timeHMS= incomming.getIntArrayExtra("hms");
        picturePath = Uri.parse(incomming.getStringExtra("picturePath"));
        notification = Uri.parse(incomming.getStringExtra("notification"));
        name = incomming.getStringExtra("name");
        index = incomming.getIntExtra("index", -1);


        nph = (NumberPicker) findViewById(R.id.numberPickerHours);
        nph.setMinValue(0);
        nph.setMaxValue(99);
        nph.setValue(timeHMS[0]);


        npm = (NumberPicker) findViewById(R.id.numberPickerMinutes);
        npm.setMinValue(0);
        npm.setMaxValue(99);
        npm.setValue(timeHMS[1]);

        nps = (NumberPicker) findViewById(R.id.numberPickerSeconds);
        nps.setMinValue(0);
        nps.setMaxValue(99);
        nps.setValue(timeHMS[2]);




        imageView = (ImageView) findViewById(R.id.imageView);
        try {
            picture = MediaStore.Images.Media.getBitmap(getContentResolver(),picturePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Sorry Not Implemented Yet", Toast.LENGTH_SHORT).show();
                //TODO
            }
        });
        imageView.setImageBitmap(picture);


        textView = (TextView) findViewById(R.id.nameText);
        textView.setText(name);

        ringToneUri = Settings.System.DEFAULT_RINGTONE_URI;
        ringToneText = (TextView) findViewById(R.id.ringToneText);
        ringToneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO
                Toast.makeText(v.getContext(), "Sorry Not Implemented Yet", Toast.LENGTH_SHORT).show();

            }
        });
        ringToneText.setText(notification.toString());


        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(v.getContext(), "Sorry Not Implemented Yet", Toast.LENGTH_SHORT).show();
            }
        });
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(v.getContext(), "Sorry not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
