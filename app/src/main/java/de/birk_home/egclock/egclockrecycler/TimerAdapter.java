package de.birk_home.egclock.egclockrecycler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Snap10 on 13/11/15.
 */
public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder> {

    private List<MyTimer> timerList;
    private Context context;
    private List<Bitmap> imageList;


    /**
     *Sets the Timerlist and Preloads every Picture of the Timers in an ImageList for better Performance
     *Context is needed for further Actions
     * @param timerList
     * @param context
     */
    public TimerAdapter(List<MyTimer> timerList, Context context) {
        this.timerList = timerList;
        this.context = context;
        this.imageList = new ArrayList<>();
        for (MyTimer timer : timerList) {
            timer.setTadapter(this);
            if (timer.picturePath != null) {
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(timer.getPicturePath()));
                    imageList.add(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_photo);
                image = getResizedBitmap(image, 800);
                imageList.add(image);
            }
        }
    }

    public List<MyTimer> getTimerList() {
        return timerList;
    }

    /**
     * Sets the Timerlist and Preloads every Picture of the Timers in an ImageList for better Performance
     * @param timerList
     */
    public void setTimerList(List<MyTimer> timerList) {
        this.timerList = timerList;
        this.imageList = new ArrayList<>();
        for (MyTimer timer : timerList) {
            if (timer.picturePath != null) {
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(timer.getPicturePath()));
                    imageList.add(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_photo);
                image = getResizedBitmap(image, 800);
                imageList.add(image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return timerList.size();
    }

    /**
     *
     * @param timerViewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final TimerViewHolder timerViewHolder, int i) {
        MyTimer ti = timerList.get(i);
        timerViewHolder.setIndex(i);
        timerViewHolder.vName.setText(ti.getName());
        timerViewHolder.vTime.setText(ti.getTimeInString());
        timerViewHolder.vProgress.setMax((int) (ti.getTimeInMillis() / 1000));
        timerViewHolder.vProgress.setProgress((int) (ti.getProgress() / 1000));
        Bitmap image = null;


        timerViewHolder.vImage.setImageBitmap(imageList.get(i));
        if (ti.paused) {
            timerViewHolder.vButtonPlayPause.setImageResource(R.drawable.ic_play_arrow_24dp);
        } else {
            timerViewHolder.vButtonPlayPause.setImageResource(R.drawable.ic_pause_24dp);
        }
        if (ti.finished) {
            timerViewHolder.alertDialog = new AlertDialog.Builder(context).create();
            timerViewHolder.alertDialog.setMessage(ti.getName() + " has finished!");
            timerViewHolder.alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            timerList.get(timerViewHolder.getIndex()).confirmFinished();
                        }
                    });


            timerViewHolder.alertDialog.show();
            ti.finished = false;
        }

        timerViewHolder.vButtonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTimer ti = timerList.get(timerViewHolder.getIndex());
                if (ti.paused) {
                    ti.startTimer();
                    ImageButton button = (ImageButton) v;
                    button.setImageResource(R.drawable.ic_pause_24dp);
                } else {
                    ti.pauseTimer();
                    ImageButton button = (ImageButton) v;
                    button.setImageResource(R.drawable.ic_play_arrow_24dp);
                }
                notifyDataSetChanged();
            }
        });

        timerViewHolder.vButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTimer ti = timerList.get(timerViewHolder.getIndex());
                timerViewHolder.vButtonPlayPause.setImageResource(R.drawable.ic_play_arrow_24dp);
                ti.reset();
                notifyDataSetChanged();
            }
        });
        timerViewHolder.vButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerList.remove(timerViewHolder.getIndex());
                imageList.remove(timerViewHolder.getIndex());
                notifyDataSetChanged();

            }
        });
        timerViewHolder.vButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTimer ti = timerList.get(timerViewHolder.getIndex());
                ti.pauseTimer();
                Intent editIntent = new Intent(timerViewHolder.context, EditActivity.class);
                editIntent.putExtra("index", timerViewHolder.getIndex());
                editIntent.putExtra("name", ti.getName());
                editIntent.putExtra("notification", ti.getNotification().toString());
                editIntent.putExtra("picturePath", ti.getPicturePath().toString());
                editIntent.putExtra("hms", ti.getTimeInHMS());
                context.startActivity(editIntent);
            }
        });

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public TimerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);


        return new TimerViewHolder(itemView, i, context);
    }

    /**
     * Add new Timer and preload Picture in MaxSize 800
     * @param timer
     */
    public void addItem(MyTimer timer) {
        timerList.add(timer);
        if (timer.picturePath != null) {
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(timer.getPicturePath()));
                image = getResizedBitmap(image, 800);
                imageList.add(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_photo);
            imageList.add(image);
        }

        notifyDataSetChanged();


    }

    /**
     * Calculates a given MaxSize of a Picture to resize it with maintained AspectRatio
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * InnerClass TimerViewHolder extends RecyclerView.ViewHolder
     */
    public static class TimerViewHolder extends RecyclerView.ViewHolder {

        protected TextView vName;
        protected ProgressBar vProgress;
        protected TextView vTime;
        protected ImageButton vButtonReset;
        protected ImageButton vButtonPlayPause;
        protected int index;
        protected ImageView vImage;
        protected AlertDialog alertDialog;
        protected ImageButton vButtonDelete;
        protected ImageButton vButtonEdit;
        protected Context context;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public TimerViewHolder(View v, int index, Context context) {

            super(v);
            vName = (TextView) v.findViewById(R.id.title);
            vProgress = (ProgressBar) v.findViewById(R.id.progressBar);
            vTime = (TextView) v.findViewById(R.id.timeView);
            vButtonPlayPause = (ImageButton) v.findViewById(R.id.buttonPlayPause);
            vButtonReset = (ImageButton) v.findViewById(R.id.buttonReset);
            vImage = (ImageView) v.findViewById(R.id.imageViewCard);
            vButtonDelete = (ImageButton) v.findViewById(R.id.deleteButton);
            vButtonEdit = (ImageButton) v.findViewById(R.id.editButton);
            this.index = index;
            this.context = context;


        }

    }


}

