package com.example.lluismontabes.gameofboatsandcards;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

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

    /** VIEWS **/
    // Control buttons
    Button bttnUp;
    Button bttnLeft;
    Button bttnRight;
    Button bttnDown;
    Joystick joystick;

    // Main characters
    Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        log = (TextView) findViewById(R.id.log);
        player = (Player) findViewById(R.id.player);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);

        joystick = (Joystick) findViewById(R.id.joystick);
        IslandDomain iD = new IslandDomain(this);
        layout.addView(iD);

        // Start game loop
        startRefreshTimer();

        // Link controls to buttons
        //setControlsTouchListeners();

    }

    private void setControlsTouchListeners(){
        /*bttnUp.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    // Button has been pressed
                    log("Pressing up");
                    MainActivity.this.upPressed = true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    // Button has been released
                    MainActivity.this.upPressed = false;
                }
                return true;
            }

        });

        bttnLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    // Button has been pressed
                    log("Pressing left");
                    MainActivity.this.leftPressed = true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    // Button has been released
                    MainActivity.this.leftPressed = false;
                }
                return true;
            }

        });

        bttnRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    // Button has been pressed
                    log("Pressing right");
                    MainActivity.this.rightPressed = true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    // Button has been released
                    MainActivity.this.rightPressed = false;
                }
                return true;
            }

        });

        bttnDown.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    // Button has been pressed
                    log("Pressing down");
                    MainActivity.this.downPressed = true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    // Button has been released
                    MainActivity.this.downPressed = false;
                }
                return true;
            }

        });*/
    }

    // Displays a message on the game log
    private void log(String message){
        this.log.setText(Float.toString(this.logIndex) + ": " + message);
        this.logIndex++;
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

                // Control buttons
                if(upPressed) player.moveUp();
                if(leftPressed) player.moveLeft();
                if(rightPressed) player.moveRight();
                if(downPressed) player.moveDown();

                // Joystick
                log(Float.toString(joystick.getCurrentDistance()));
                player.move(joystick.getCurrentAngle(), joystick.getCurrentIntensity());

            }
        });
    }

    public class RefreshTask extends TimerTask {
        @Override
        public void run() {
            refresh();
            //postInvalidate();
        }
    }

}
