package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lluismontabes.gameofboatsandcards.R;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button playButton = (Button) findViewById(R.id.playButton);
        TextView settingsTextView = (TextView) findViewById(R.id.settingsTextView);
        Button debugButton = (Button) findViewById(R.id.button3);

        playButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                // Start MatchmakingActivity
                Intent i = new Intent(MainMenuActivity.this, MatchmakingActivity.class);
                startActivity(i);

            }
        });

        settingsTextView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                // Start SettingsActivity
                Intent i = new Intent(MainMenuActivity.this, SettingsActivity.class);
                startActivity(i);

            }
        });

        debugButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                // Start GameActivity
                Intent i = new Intent(MainMenuActivity.this,GameActivity.class);
                startActivity(i);

            }
        });

    }

}




