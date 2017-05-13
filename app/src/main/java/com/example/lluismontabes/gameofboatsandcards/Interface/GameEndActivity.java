package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lluismontabes.gameofboatsandcards.R;

public class GameEndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        TextView results = (TextView) findViewById(R.id.resultsText);

        switch (GameActivity.getGameState()) {
            case LOCAL_WON:
                results.setText(R.string.win);
                break;
            case REMOTE_WON:
                results.setText(R.string.lose);
                break;
            case DRAW:
                results.setText(R.string.draw);
                break;
        }

        Button currentButton = (Button) findViewById(R.id.newGameButton);

        currentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "TODO", Toast.LENGTH_SHORT).show();
            }
        });

        currentButton = (Button) findViewById(R.id.mainMenuButton);

        currentButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
