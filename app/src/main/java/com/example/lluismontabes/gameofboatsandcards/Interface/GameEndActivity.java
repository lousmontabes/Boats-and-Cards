package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lluismontabes.gameofboatsandcards.R;

import org.w3c.dom.Text;

import static com.example.lluismontabes.gameofboatsandcards.Interface.MainMenuActivity.soundActive;

public class GameEndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_end);

        TextView resultTextView = (TextView) findViewById(R.id.resultsText);

        TextView localScoreTextView = (TextView) findViewById(R.id.localScoreTextView);
        TextView remoteScoreTextView = (TextView) findViewById(R.id.remoteScoreTextView);

        TextView finishConditionTextView = (TextView) findViewById(R.id.finishConditionTextView);

        TextView killsTextView = (TextView) findViewById(R.id.killsTextView);
        TextView deathsTextView = (TextView) findViewById(R.id.deathsTextView);
        TextView shotsFiredTextView = (TextView) findViewById(R.id.shotsFiredTextView);
        TextView cardsUsedTextView = (TextView) findViewById(R.id.cardsUsedTextView);

        Button newGameButton = (Button) findViewById(R.id.newGameButton);
        Button mainMenuButton = (Button) findViewById(R.id.mainMenuButton);

        Intent intent = getIntent();
        GameActivity.FinishCondition finishCondition = (GameActivity.FinishCondition) intent.getSerializableExtra("finishCondition");
        GameActivity.GameState finishState = (GameActivity.GameState) intent.getSerializableExtra("gameState");

        int localScoreStats = intent.getIntExtra("localScoreStats", 0);
        int remoteScoreStats = intent.getIntExtra("remoteScoreStats", 0);
        int killsStats = intent.getIntExtra("killsStats", 0);
        int deathsStats = intent.getIntExtra("deathsStats", 0);
        int shotsFiredStats = intent.getIntExtra("shotsFiredStats", 0);
        int shotsHitStats = intent.getIntExtra("shotsHitStats", 0);
        int cardsUsedStats = intent.getIntExtra("cardsUsedStats", 0);

        MediaPlayer mp;

        switch (finishState) {
            case LOCAL_WON:
                resultTextView.setText(R.string.victory);
                resultTextView.setTextColor(getResources().getColor(R.color.localCounterActive));
                localScoreTextView.setTextColor(getResources().getColor(R.color.localCounterActive));

                if (soundActive) {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.victory);
                    mp.start();
                }

                break;

            case REMOTE_WON:
                resultTextView.setText(R.string.defeat);
                resultTextView.setTextColor(getResources().getColor(R.color.remoteCounterActive));
                remoteScoreTextView.setTextColor(getResources().getColor(R.color.remoteCounterActive));

                if (soundActive) {
                    mp = MediaPlayer.create(getApplicationContext(), R.raw.defeat);
                    mp.start();
                }

                break;

            case DRAW:
                resultTextView.setText(R.string.draw);
                resultTextView.setTextColor(getResources().getColor(R.color.draw));
                break;
        }

        switch (finishCondition){

            case TIME_OUT:
                finishConditionTextView.setText(R.string.timeRanOut);
                break;

            case LOCAL_MAX_SCORE:
                finishConditionTextView.setText(R.string.localReachedMaxScore);
                break;

            case REMOTE_MAX_SCORE:
                finishConditionTextView.setText(R.string.remoteReachedMaxScore);
                break;

            case REMOTE_PLAYER_LEFT:
                finishConditionTextView.setText(R.string.remoteLeftMatch);
                break;

        }

        localScoreTextView.setText(Integer.toString(localScoreStats) + "%");
        remoteScoreTextView.setText(Integer.toString(remoteScoreStats) + "%");
        killsTextView.setText(Integer.toString(killsStats));
        deathsTextView.setText(Integer.toString(deathsStats));
        shotsFiredTextView.setText(Integer.toString(shotsFiredStats));
        cardsUsedTextView.setText(Integer.toString(cardsUsedStats));

        newGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start MatchmakingActivity
                Intent i = new Intent(GameEndActivity.this, MatchmakingActivity.class);
                startActivity(i);
            }
        });

        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start MainMenuActivity
                Intent i = new Intent(GameEndActivity.this, MainMenuActivity.class);
                startActivity(i);
            }
        });

        Animation anim = new ScaleAnimation(
                3f, 1f, // Start and end values for the X axis scaling
                3f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(400);
        resultTextView.startAnimation(anim);

    }

}
