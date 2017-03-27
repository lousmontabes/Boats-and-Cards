package com.example.lluismontabes.gameofboatsandcards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    /** GAME LOOP **/
    Timer refreshTimer;
    final static int fps = 60; // Frames per second
    final private long refreshPeriod = 1000 / fps; // Period in milliseconds of each update

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

    // Players
    Player player1;
    Player player2;

    // Statistics
    int counter1 = 100;
    int counter2 = 100;
    byte framesUntilTick1 = fps;
    byte framesUntilTick2 = fps;
    boolean player1Inside = false;
    boolean player2Inside = false;

    // Props
    ArrayList<Trace> activeTraces = new ArrayList<>();
    ArrayList<Projectile> activeProjectiles = new ArrayList<>();
    ArrayList<Collider> activeColliders = new ArrayList<>();
    ArrayList<Player> activePlayers = new ArrayList<>();

    //Layout
    static RelativeLayout layout;

    //Island Domain
    IslandDomain islandDomain;

    //Chronometer
    ChronometerPausable chronometerPausable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        layout = (RelativeLayout) findViewById(R.id.gameLayout);
        log = (TextView) findViewById(R.id.log);
        joystick = (Joystick) findViewById(R.id.joystick);

        //test
        chronometerPausable = new ChronometerPausable(this);
        layout.addView(chronometerPausable);

        spawnPlayers();
        spawnIslandDomain(100);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float pW = player1.getWidth();
                float pH = player1.getHeight();
                float oX = player1.getX() + pW / 2;
                float oY = player1.getY() + pH / 2;

                System.out.println(oX + ", " + oY);
                Projectile projectile = new Projectile(GameActivity.this, joystick.getCurrentAngle(), oX, oY);
                GameActivity.this.activeProjectiles.add(projectile);
                GameActivity.this.activeColliders.add(projectile);

                layout.addView(projectile);
                return false;

            }
        });


        // Start game loop
        startRefreshTimer();

        // Link controls to buttons
        //setControlsTouchListeners();

    }

    private void spawnIslandDomain(float radius){
        islandDomain = new IslandDomain(this,radius);
        layout.addView(islandDomain);
    }

    private void spawnPlayers(){

        player1 = new Player(this, null);
        player2 = new Player(this, null);

        player1.setImageDrawable(getResources().getDrawable(R.drawable.basicboat));
        player2.setImageDrawable(getResources().getDrawable(R.drawable.basicboat));

        final float scale = this.getResources().getDisplayMetrics().density;

        ViewGroup.LayoutParams playerParams = new ViewGroup.LayoutParams((int) (50 * scale + 0.5f),
                                                                         (int) (80 * scale + 0.5f));

        player1.setLayoutParams(playerParams);
        player2.setLayoutParams(playerParams);

        activePlayers.add(player1);
        activePlayers.add(player2);

        layout.addView(player1);
        layout.addView(player2);

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

    private void showBoatTrace(){
        float oX = player1.getX() + 30; // Compensem per l'espai buit de l'imatge
        float oY = player1.getY() + 76; // Compensem per l'espai buit de l'imatge

        float angle = (float) Math.toDegrees(joystick.getCurrentAngle()) + 90;

        Trace trace = new Trace(GameActivity.this, oX, oY, angle);
        activeTraces.add(trace);
        layout.addView(trace);

        if(activeTraces.size() > 20){
            layout.removeView(activeTraces.get(0));
            activeTraces.remove(0);
        }

        player1.bringToFront();
    }

    private void checkProjectileCollisions(){
        Iterator<Projectile> projectileIterator = activeProjectiles.iterator();

        while (projectileIterator.hasNext()) {

            Projectile p = projectileIterator.next();

            if (p.isColliding(player2)){

                player2.setColorFilter(getResources().getColor(R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                player2.damage(20);

                layout.removeView(p);
                projectileIterator.remove();

            }

            else player2.setColorFilter(null);

        }
    }

    private void checkIslandDomainCollisions(){

        if (player1.isColliding(islandDomain)) {
            // Player 1 is colliding with islandDomain.

            if (!player1Inside){
                // Player 1 was not inside before this collision.

                islandDomain.toggleInvadedStatus();
                player1Inside = !player1Inside;
            }

        }else{
            // Player 1 is not colliding with islandDomain.

            if (player1Inside){
                // Player 1 was inside before this collision.

                islandDomain.toggleInvadedStatus();
                player1Inside = !player1Inside;
            }

        }

    }

    private void startRefreshTimer(){
        this.refreshTimer = new Timer();
        refreshTimer.schedule(new RefreshTask(), 0, refreshPeriod);
    }

    // Method that gets called every frame.
    private void refresh(){
        runOnUiThread(new Runnable() {
            public void run()
            {

                // Point-and-click controls
                if (player1.getX() == destX && player1.getY() == destY) moving = false;
                if (moving) player1.moveTo(destX, destY);

                // Control buttons (currently unused)
                if(upPressed) player1.moveUp();
                if(leftPressed) player1.moveLeft();
                if(rightPressed) player1.moveRight();
                if(downPressed) player1.moveDown();

                // Joystick controls
                player1.move(joystick.getCurrentAngle(), joystick.getCurrentIntensity());

                // Projectile movement
                for (Projectile p:activeProjectiles) p.move();

                // Boat trace
                showBoatTrace();

                // Collisions
                checkProjectileCollisions();

                // Health
                for (Player pl:activePlayers){
                    if (pl.getHealth() <= 0) pl.die();
                }

                //Collision player1 - domain island
                checkIslandDomainCollisions();

                advanceCounter();

            }
        });
    }

    private void advanceCounter() {

        TextView tv = (TextView) findViewById(R.id.textViewScore1);

        if (player1Inside) {

            log("Player inside");

            if (--framesUntilTick1 == 0) {
                counter1--;
                framesUntilTick1 = fps / 2;
                tv.setText(Integer.toString(counter1));
            }

            tv.setTextColor(getResources().getColor(R.color.counterTicking));

        } else {

            log("Player outside");

            framesUntilTick1 = fps / 2;
            tv.setTextColor(getResources().getColor(R.color.counterStopped));

        }
    }

    public class RefreshTask extends TimerTask {
        @Override
        public void run() {
            refresh();
            layout.postInvalidate();
        }
    }

}
