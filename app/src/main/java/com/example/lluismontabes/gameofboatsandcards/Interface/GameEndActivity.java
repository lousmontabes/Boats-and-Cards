package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.content.Intent;
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

import org.w3c.dom.Text;

public class GameEndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        TextView results = (TextView) findViewById(R.id.resultsText);
        TextView localScoreTextView = (TextView) findViewById(R.id.localScoreTextView);
        TextView remoteScoreTextView = (TextView) findViewById(R.id.remoteScoreTextView);
        TextView killsTextView = (TextView) findViewById(R.id.killsTextView);
        TextView deathsTextView = (TextView) findViewById(R.id.deathsTextView);
        TextView shotsFiredTextView = (TextView) findViewById(R.id.shotsFiredTextView);
        TextView cardsUsedTextView = (TextView) findViewById(R.id.cardsUsedTextView);

        Intent intent = getIntent();
        GameActivity.GameState finishState = (GameActivity.GameState) intent.getSerializableExtra("gameState");

        switch (finishState) {
            case LOCAL_WON:
                results.setText(R.string.win);
                results.setTextColor(getResources().getColor(R.color.localCounterActive));
                localScoreTextView.setTextColor(getResources().getColor(R.color.localCounterActive));
                break;
            case REMOTE_WON:
                results.setText(R.string.lose);
                results.setTextColor(getResources().getColor(R.color.remoteCounterActive));
                remoteScoreTextView.setTextColor(getResources().getColor(R.color.remoteCounterActive));
                break;
            case DRAW:
                results.setText(R.string.draw);
                results.setTextColor(getResources().getColor(R.color.draw));
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
