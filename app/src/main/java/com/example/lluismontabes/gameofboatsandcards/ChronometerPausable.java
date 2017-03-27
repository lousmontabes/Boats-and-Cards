package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by JorgeTB on 24/03/2017.
 */

public class ChronometerPausable extends Chronometer {

    private long timeWhenStopped;


    public ChronometerPausable(Context context) {
        super(context);
        this.setEnabled(true);
        this.setBase(SystemClock.elapsedRealtime());
        this.timeWhenStopped = 0;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1400,2400);
        this.setX(context.getResources().getDisplayMetrics().widthPixels - 30);
        this.setY(context.getResources().getDisplayMetrics().heightPixels - 30);
        this.setLayoutParams(layoutParams);
        this.setOnChronometerTickListener(new OnChronometerTickListener() {
            @Override
            public void onChronometerTick(android.widget.Chronometer chronometer) {
              //Tick listener, callback when chronometer change
            }
        });
    }

    public void stop_remember(){
        this.timeWhenStopped = this.getBase() - SystemClock.elapsedRealtime();
        this.stop();
    }

    public void start_remember(){
        this.setBase(SystemClock.elapsedRealtime() + this.timeWhenStopped);
        this.start();
    }

    public void reset(){
        this.timeWhenStopped = 0;
    }








}

