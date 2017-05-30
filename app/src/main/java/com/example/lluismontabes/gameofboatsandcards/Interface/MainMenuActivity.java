package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lluismontabes.gameofboatsandcards.R;


public class MainMenuActivity extends AppCompatActivity {

    public static boolean soundActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_menu);

        Button playButton = (Button) findViewById(R.id.playButton);
        //TextView settingsTextView = (TextView) findViewById(R.id.collectionTextView);
        //Button debugButton = (Button) findViewById(R.id.button3);
        TextView collectionTextView = (TextView) findViewById(R.id.collectionTextView);
        final ImageButton soundButton = (ImageButton) findViewById(R.id.soundButton);

        playButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                // Start MatchmakingActivity
                Intent i = new Intent(MainMenuActivity.this, MatchmakingActivity.class);
                startActivity(i);

            }
        });

        collectionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start CollectionActivity (Collection)
                Intent i = new Intent(MainMenuActivity.this, CollectionActivity.class);
                startActivity(i);

            }
        });

        final MediaPlayer mp;
        mp = MediaPlayer.create(getApplicationContext(), R.raw.main_menu_audio);
        mp.setLooping(true);
        mp.start();

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundActive ^= true) {
                    mp.start();
                    soundButton.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                } else {
                    mp.pause();
                    mp.seekTo(0);
                    soundButton.setImageResource(android.R.drawable.ic_lock_silent_mode);
                }
            }
        });

    }

}




