package com.example.lluismontabes.gameofboatsandcards;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class GameActivity extends AppCompatActivity {

    /** GAME LOOP **/
    Timer refreshTimer;
    final static private int fps = 30; // Frames per second
    final static private long refreshPeriod = 1000 / fps; // Period in milliseconds of each update

    /** ONLINE **/
    RemoteDataTask remoteTask;
    boolean connectionActive = true;
    float remoteX = 0;
    float remoteY = 0;
    float localX = 0;
    float localY = 0;

    /** DEBUGGING **/
    // Log index and TextView
    int logIndex = 0;
    TextView log;
    TextView frameLog;

    /** GAME DATA **/
    Data data = new Data();

    /** LOGIC VARIABLES **/
    // Gameplay
    boolean player1Inside = false;
    boolean player2Inside = false;

    // Control button boolean variables
    private boolean upPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean downPressed = false;

    // Point-and-click control variables
    private boolean moving = false;
    private float destX, destY;

    /** VIEWS **/
    // Controls
    Joystick joystick;

    // Players
    Player player1;
    Player player2;

    //Island Domain
    IslandDomain islandDomain;

    // Timer and scoreboard
    TextView timer;
    TextView textViewCounter1;
    TextView textViewCounter2;

    /** STATISTICS **/
    // Scores
    int score1 = 0;
    int score2 = 0;

    // Time
    int currentFrame = 0;
    int seconds = 0;
    int secondsLeft = 120;

    // Counters
    byte framesUntilTick1 = fps;
    byte framesUntilTick2 = fps;

    /** COLLECTIONS **/
    // Props
    ArrayList<Trace> activeTraces = new ArrayList<>();
    ArrayList<Projectile> activeProjectiles = new ArrayList<>();
    ArrayList<Collider> activeColliders = new ArrayList<>();
    ArrayList<Player> activePlayers = new ArrayList<>();

    /** LAYOUT **/
    // Layout
    static RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        layout = (RelativeLayout) findViewById(R.id.gameLayout);

        log = (TextView) findViewById(R.id.log);
        frameLog = (TextView) findViewById(R.id.frameLog);

        joystick = (Joystick) findViewById(R.id.joystick);

        timer = (TextView) findViewById(R.id.timer);
        textViewCounter1 = (TextView) findViewById(R.id.textViewCounter1);
        textViewCounter2 = (TextView) findViewById(R.id.textViewCounter2);

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

        // Create asynchronous online data gatherer task
        remoteTask = new RemoteDataTask();
        remoteTask.execute();

        // Start game loop
        startRefreshTimer();

        // Link controls to buttons
        // setControlsTouchListeners();

    }

    private void startRefreshTimer(){
        this.refreshTimer = new Timer();
        refreshTimer.schedule(new RefreshTask(), 0, refreshPeriod);
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

    private void finishGame(){
        if (score1 > score2) log("You win!");
        else if (score1 < score2) log("You lose");
        else if (score1 == score2) log("Draw!");
    }

    private void advanceCounter() {

        frameLog.setText(Integer.toString(currentFrame));

        if (currentFrame % 60 == 0){
            seconds++;

            if(secondsLeft > 0){
                secondsLeft--;

                int m = secondsLeft / 60;
                int s = secondsLeft % 60;

                timer.setText(Integer.toString(m) + ":" + String.format("%02d", s));

            }else{
                // Time ran out.
                finishGame();
            }
        }

        if (player1Inside) {

            if (--framesUntilTick1 == 0) {

                if (score1 < 100){
                    score1++;
                    framesUntilTick1 = fps / 2;
                    textViewCounter1.setText(Integer.toString(score1) + "%");

                }else{
                    // Player 1 reached 100% score.
                    finishGame();
                }
            }

            textViewCounter1.setTextColor(getResources().getColor(R.color.counterTicking));

        } else {
            framesUntilTick1 = fps / 2;
            textViewCounter1.setTextColor(getResources().getColor(R.color.counterStopped));
        }
    }

    // Retrieve online player's last action.
    private void retrieveRemoteAction(){

    }

    // Method that gets called every frame.
    private void refresh(){
        runOnUiThread(new Runnable() {
            public void run()
            {

                // Advance current frame
                currentFrame++;

                // Point-and-click controls
                if (player2.getX() == destX && player2.getY() == destY) moving = false;
                if (moving) player2.moveTo(destX, destY);

                // Control buttons (currently unused)
                if(upPressed) player1.moveUp();
                if(leftPressed) player1.moveLeft();
                if(rightPressed) player1.moveRight();
                if(downPressed) player1.moveDown();

                // Joystick controls
                player1.move(joystick.getCurrentAngle(), joystick.getCurrentIntensity());

                // Player 2 controls
                //retrieveRemoteAction();

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

                // islandDomain collisions
                checkIslandDomainCollisions();

                // Scoreboard and timer counter
                advanceCounter();

                // Prepare local data to send to server
                localX = player1.getX();
                localY = player1.getY();

                // Apply remote data to player2
                moveObjectTo(player2, remoteX, remoteY);

            }
        });
    }

    /** BACKGROUND TASKS **/
    // Timer
    public class RefreshTask extends TimerTask {
        @Override
        public void run() {
            refresh();
            layout.postInvalidate();
        }
    }

    // Asynchronous task that retrieves the remote player's actions
    public class RemoteDataTask extends AsyncTask<String, String, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(String... params) {

            while(connectionActive){

                /* SEND DATA */
                getJSON("https://pis04-ub.herokuapp.com/send_local_action.php?x=" + localX + "&y=" + localY, 2000);

                /* RETRIEVE DATA */
                //This returns a JSON object with a {"x": x,"y": y} pattern.
                String data = getJSON("https://pis04-ub.herokuapp.com/retrieve_remote_action.php", 2000);

                // Parse the JSON information into a Point object.
                Point p = new Gson().fromJson(data, Point.class);

                // Set X and Y coordinates retrieved from JSON to the remoteX and remoteY global
                // variables. These variables will be used to position player2 on the next frame.
                remoteX = p.x;
                remoteY = p.y;

            }


            return null;

        }

    }

    /**
     * Get JSON response as String from URL.
     * @param url URL to retrieve JSON from.
     * @param timeout Time available to establish connection.
     * @return JSON response as String.
     */
    public String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

}
