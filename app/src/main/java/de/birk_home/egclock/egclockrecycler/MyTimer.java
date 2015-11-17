package de.birk_home.egclock.egclockrecycler;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.CountDownTimer;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Snap10 on 13/11/15.
 * Model for a MyTimer
 */
public class MyTimer implements Serializable {

    transient CountDownTimer timer;
    String name;
    String picturePath;
    String notification;
    boolean paused;
    boolean finished = false;
    transient TimerAdapter tadapter;
    String timeInString = "Not Started Yet";
    transient MediaPlayer mMediaPlayer;
    transient Ringtone r;

    long pausedTime;
    long timeInMillis;
    long progress = 0;
    long millisUF;

    public MyTimer(long timeInMillis, String name, String picturePath, String notification, TimerAdapter tadapter) {
        this.timeInMillis = timeInMillis;
        this.pausedTime = timeInMillis;
        this.millisUF = timeInMillis;
        this.notification = notification;
        if (name.equals("")) {
            this.name = "egTimer";
        } else {
            this.name = name;
        }
        this.picturePath = picturePath;
        paused = true;
        this.tadapter = tadapter;

    }

    public String getTimeInString() {
        return timeInString;
    }

    public void setTimeInString(String timeInString) {
        this.timeInString = timeInString;
    }

    public CountDownTimer getTimer() {
        return timer;
    }

    public void setTimer(CountDownTimer timer) {
        this.timer = timer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Boolean getPaused() {
        return paused;
    }

    public void setPaused(Boolean paused) {
        this.paused = paused;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public TimerAdapter getTadapter() {
        return tadapter;
    }

    public void setTadapter(TimerAdapter tadapter) {
        this.tadapter = tadapter;
    }

    public void startTimer() {
        this.timer = getCDT(pausedTime);
        timer.start();
        paused = false;
    }


    /**
     * gets a CountDownTimer Object and starts the Timer with the last PausedTime
     * In case the TimerObject was never started, the PausedTime is equal to the SetupTime
     * @param tadapter
     */
    public void startTimer(TimerAdapter tadapter) {
        this.tadapter = tadapter;
        this.timer = getCDT(pausedTime);
        timer.start();
        paused = false;
    }

    /**
     * CountDownTimer Factory
     * notifys the tadapter Object in the onTick method every 1000ms.
     * @param pausedTime
     * @return
     */
    private CountDownTimer getCDT(long pausedTime) {
        CountDownTimer cdt = new CountDownTimer(pausedTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUF = millisUntilFinished;
                progress = timeInMillis - millisUntilFinished;
                timeInString = calculateHMS(millisUntilFinished);

                tadapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {

                finished();

            }
        };
        return cdt;
    }
    /*
    Cancels the CountDownTimer Object and saves the milliseconds until finished as new PausedTime
    If there is no CountDownTimer Object it is not canceled.
     */
    public void pauseTimer() {
        pausedTime = timeInMillis - progress;
        timeInString = "Paused";
        if (timer != null) {

            timer.cancel();
        }
        paused = true;

    }


    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public long getPausedTime() {
        return pausedTime;
    }

    public void setPausedTime(long pausedTime) {
        this.pausedTime = pausedTime;
    }

    /**
     * Called from the onFinish Method of the CountDownTimer Object.
     * Sets up a MediaPlayer Object to play the RingTone specified in the notification String which represents an Uri to a RingTone
     * Player sets Looping to True
     * finished Variable is set to True and String is chanced.
     * adapter is notified, that dataset has changed
     */
    private void finished() {

        //TODO Play Sound
        finished = true;
        Intent intent = ((Activity)tadapter.getContext()).getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        ((Activity)tadapter.getContext()).startActivity(intent);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(tadapter.getContext().getApplicationContext(), Uri.parse(notification));
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progress = timeInMillis;
        timeInString = "Done!";
        tadapter.notifyDataSetChanged();
    }

    /**
     * stops the mediaplayer and sets finisehd to false
     */
    public void confirmFinished() {
        mMediaPlayer.stop();
        // r.stop();
        finished = false;
    }

    /**
     *Calculates the the Hour Minuites and Seconds of a given value of milliseconds
     * @param timeInMillis
     * @return
     */
    private String calculateHMS(long timeInMillis) {
        long millis = timeInMillis;
        String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return time;
    }

    /**
     * resets the TimerValues and notifys the adapter
     */
    public void reset() {
        timer.cancel();
        pausedTime = timeInMillis;
        progress = 0;
        paused = true;
        timeInString = calculateHMS(pausedTime);
        tadapter.notifyDataSetChanged();

    }

    /**
     * Retunrs the time until finished in Hours, Minuites and Seconds as an IntegerArray of Size 3
     * Index 0 = Hours
     * Index 1 = minuites
     * Index 2 = Seconds
     * @return
     */
    public int[] getTimeInHMS() {
        int[] timeHMSArray = new int[3];
        timeHMSArray[0] = (int) TimeUnit.MILLISECONDS.toHours(millisUF);
        timeHMSArray[1] = (int) (TimeUnit.MILLISECONDS.toMinutes(millisUF) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUF)));
        timeHMSArray[2] = (int) (TimeUnit.MILLISECONDS.toSeconds(millisUF) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUF)));
        return timeHMSArray;
    }
}
