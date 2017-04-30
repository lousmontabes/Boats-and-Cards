package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.lluismontabes.gameofboatsandcards.R;

import java.util.Timer;
import java.util.TimerTask;

public class CreditsActivity extends AppCompatActivity {

    Timer refreshTimer;
    private long refreshPeriod = 1000 / 60; // Period in milliseconds of each update
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }


}
