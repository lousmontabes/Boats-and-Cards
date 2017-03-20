package com.example.lluismontabes.gameofboatsandcards;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    /** GAME LOOP **/
    Timer refreshTimer;
    static int fps = 60; // Frames per second
    private long refreshPeriod = 1000 / fps; // Period in milliseconds of each update

    /** DEBUGGING **/
    // Log index and TextView
    int logIndex = 0;
    TextView log;

    /** GAME DATA **/
    Data data = new Data();

    /** LOGIC VARIABLES **/
    // Game values
    //float velocity = 10; // Pixel units per frame

    // Control button boolean variables
    private boolean upPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean downPressed = false;

    // Point-and-click control variables
    private boolean moving = false;
    private float destX, destY;

    /** VIEWS **/
    // Control buttons
    Button bttnUp;
    Button bttnLeft;
    Button bttnRight;
    Button bttnDown;
    Joystick joystick;

    // Main characters
    Player player;

    // Props
    ArrayList<Trace> activeTraces = new ArrayList<>();
    ArrayList<Projectile> activeProjectiles = new ArrayList<>();

    //Layout
    static RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        layout = (RelativeLayout) findViewById(R.id.activity_main);
        log = (TextView) findViewById(R.id.log);
        joystick = (Joystick) findViewById(R.id.joystick);

        player = (Player) findViewById(R.id.player);

        IslandDomain islandDomain = new IslandDomain(this);
        //layout.addView(islandDomain);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                log("Firing");

                float pW = player.getWidth();
                float pH = player.getHeight();
                float oX = player.getX() + pW / 2;
                float oY = player.getY() + pH / 2;

                System.out.println(oX + ", " + oY);
                Projectile projectile = new Projectile(GameActivity.this, joystick.getCurrentAngle(), oX, oY);
                GameActivity.this.activeProjectiles.add(projectile);

                layout.addView(projectile);
                return false;

            }
        });

        // Start game loop
        startRefreshTimer();

        // Link controls to buttons
        //setControlsTouchListeners();

    }

    // Displays a message on the game log
    private void log(String message){
        this.log.setText(Float.toString(this.logIndex) + ": " + message);
        this.logIndex++;
    }

    private void moveObjectTo(View object, float x, float y){
        this.destX = x;
        this.destY = y;
        this.moving = true;
    }

    private void startRefreshTimer(){
        this.refreshTimer = new Timer();
        refreshTimer.schedule(new RefreshTask(), 0, refreshPeriod);
    }

    // Method that gets called every frame.
    public void refresh(){
        runOnUiThread(new Runnable() {
            public void run()
            {

                // Point-and-click controls
                if (player.getX() == destX && player.getY() == destY) moving = false;
                if (moving) player.moveTo(destX, destY);

                // Control buttons (currently unused)
                if(upPressed) player.moveUp();
                if(leftPressed) player.moveLeft();
                if(rightPressed) player.moveRight();
                if(downPressed) player.moveDown();

                // Joystick controls
                //log(Float.toString(joystick.getCurrentIntensity()));
                player.move(joystick.getCurrentAngle(), joystick.getCurrentIntensity());

                // Projectile movement
                for (Projectile p:activeProjectiles) p.move();

                // Boat trace
                float pW = player.getWidth();
                float pH = player.getHeight();
                float oX = player.getX() + 30; // Compensem per l'espai buit de l'imatge
                float oY = player.getY() + 76; // Compensem per l'espai buit de l'imatge

                log(Float.toString((float) Math.toDegrees(joystick.getCurrentAngle())));
                float angle = (float) Math.toDegrees(joystick.getCurrentAngle()) + 90;

                Trace trace = new Trace(GameActivity.this, oX, oY, angle);
                activeTraces.add(trace);
                layout.addView(trace);

                if(activeTraces.size() > 20){
                    layout.removeView(activeTraces.get(0));
                    activeTraces.remove(0);
                }

                //player.bringToFront();

            }
        });
    }

    public class RefreshTask extends TimerTask {
        @Override
        public void run() {
            refresh();
            layout.postInvalidate();
        }
    }

}
