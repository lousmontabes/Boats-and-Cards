package com.example.lluismontabes.gameofboatsandcards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    // Main characters
    Player player;
    Player player2;

    // Props
    ArrayList<Trace> activeTraces = new ArrayList<>();
    ArrayList<Projectile> activeProjectiles = new ArrayList<>();
    ArrayList<Collider> activeColliders = new ArrayList<>();
    ArrayList<Player> activePlayers = new ArrayList<>();

    //Layout
    static RelativeLayout layout;

    //Island Domain
    IslandDomain islandDomain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        layout = (RelativeLayout) findViewById(R.id.activity_main);
        log = (TextView) findViewById(R.id.log);
        joystick = (Joystick) findViewById(R.id.joystick);

        spawnPlayers();
        spawnIslandDomain(300);

        //IslandDomain islandDomain = new IslandDomain(GameActivity.this, 300);
        //layout.addView(islandDomain);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float pW = player.getWidth();
                float pH = player.getHeight();
                float oX = player.getX() + pW / 2;
                float oY = player.getY() + pH / 2;

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
        player = new Player(this, null);
        player2 = new Player(this, null);

        player.setImageDrawable(getResources().getDrawable(R.drawable.basicboat));
        player2.setImageDrawable(getResources().getDrawable(R.drawable.basicboat));

        final float scale = this.getResources().getDisplayMetrics().density;

        ViewGroup.LayoutParams playerParams = new ViewGroup.LayoutParams((int) (50 * scale + 0.5f),
                                                                         (int) (80 * scale + 0.5f));

        player.setLayoutParams(playerParams);
        player2.setLayoutParams(playerParams);

        activePlayers.add(player);
        activePlayers.add(player2);

        layout.addView(player);
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
        float oX = player.getX() + 30; // Compensem per l'espai buit de l'imatge
        float oY = player.getY() + 76; // Compensem per l'espai buit de l'imatge

        float angle = (float) Math.toDegrees(joystick.getCurrentAngle()) + 90;

        Trace trace = new Trace(GameActivity.this, oX, oY, angle);
        activeTraces.add(trace);
        layout.addView(trace);

        if(activeTraces.size() > 20){
            layout.removeView(activeTraces.get(0));
            activeTraces.remove(0);
        }

        player.bringToFront();
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

    private void startRefreshTimer(){
        this.refreshTimer = new Timer();
        refreshTimer.schedule(new RefreshTask(), 0, refreshPeriod);
    }

    boolean player_inside = false;
    // Method that gets called every frame.
    private void refresh(){
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
                player.move(joystick.getCurrentAngle(), joystick.getCurrentIntensity());

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

                //Collision player - domain island
                if (player.isColliding(islandDomain,"islandDomain")){
                    System.out.println("Estic dins de l'area de domini de la illa!!!");
                    if (player_inside == false) {
                        islandDomain.changeStatus();
                        player_inside = true;
                    }
                }else{
                    if (player_inside){
                        islandDomain.changeStatus();
                        player_inside = false;
                    }
                }

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
