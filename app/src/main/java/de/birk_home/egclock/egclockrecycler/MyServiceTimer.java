package de.birk_home.egclock.egclockrecycler;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.os.IBinder;

import java.util.concurrent.TimeUnit;

public class MyServiceTimer extends Service {

    CountDownTimer timer;
    String name;
    Bitmap picture;
    Boolean paused;
    TimerAdapter tadapter;
    String timeInString="";

    long pausedTime;
    long timeInMillis;
    long progress=0;

    public MyServiceTimer(long timeInMillis, String name, Bitmap picture, TimerAdapter tadapter) {
        this.timeInMillis=timeInMillis;
        this.pausedTime = timeInMillis;
        this.name = name;
        this.picture = picture;
        paused=true;
        this.tadapter=tadapter;

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

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
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

    public void startTimer(){
        this.timer = getCDT(pausedTime);
        timer.start();
        paused=false;
    }

    private CountDownTimer getCDT(long pausedTime) {
        CountDownTimer cdt= new CountDownTimer(pausedTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                progress=timeInMillis-millisUntilFinished;
                timeInString=calculateDifference(millisUntilFinished);
                tadapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {

                finished();

            }
        };
        return cdt;
    }

    public void pauseTimer(){
        pausedTime = timeInMillis-progress;
        timer.cancel();
        paused=true;

    }




    private void finished(){

        //TODO Play Sound
        progress=timeInMillis;
        timeInString="Done!";
        tadapter.notifyDataSetChanged();
    }

    private String calculateDifference(long timeInMillis){
        long millis = timeInMillis;
        String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return time;
    }



    public void reset() {
        timer.cancel();
        pausedTime = timeInMillis;
        progress=0;
        tadapter.notifyDataSetChanged();

    }

    public MyServiceTimer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
