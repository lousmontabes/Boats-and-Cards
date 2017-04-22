package com.example.lluismontabes.gameofboatsandcards;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameActivity extends AppCompatActivity {

    /**
     * GAME LOOP
     **/
    Timer refreshTimer;
    final static private int fps = 30; // Frames per second
    final static private long refreshPeriod = 1000 / fps; // Period in milliseconds of each update

    /**
     * ONLINE
     **/
    RemoteDataTask remoteTask;
    boolean connectionActive = true;
    float remoteX = 0;
    float remoteY = 0;
    float localX = 0;
    float localY = 0;
    int matchId;
    int assignedPlayer;
    int oppositePlayer;

    /**
     * DEBUGGING
     **/
    // Log index and TextView
    int logIndex = 0;
    TextView log;
    TextView frameLog;

    /**
     * GAME DATA
     **/
    Data data = new Data();

    /**
     * LOGIC VARIABLES
     **/
    // Gameplay
    boolean player1Inside = false;
    boolean player2Inside = false;
    boolean running = false;


    // Control button boolean variables
    private boolean upPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean downPressed = false;

    // Point-and-click control variables
    private boolean moving = false;
    private float destX, destY;

    /**
     * VIEWS
     **/
    // Controls
    Joystick joystick;

    // Players
    Player localPlayer;
    Player remotePlayer;

    //Island Domain
    IslandDomain islandDomain;

    // Timer and scoreboard
    TextView timer;
    TextView textViewCounter1;
    TextView textViewCounter2;

    // Images
    ImageView treasureImageView;

    /**
     * STATISTICS
     **/
    // Scores
    int score1 = 0;
    int score2 = 0;

    // Time
    int currentFrame = 0;
    int seconds = 0;
    int secondsLeft = 120;

    // Counters
    byte framesUntilTick1 = fps / 2;
    byte framesUntilTick2 = fps / 2;

    /**
     * COLLECTIONS
     **/
    // Props
    ArrayList<Trace> activeTraces = new ArrayList<>();
    ArrayList<Projectile> activeProjectiles = new ArrayList<>();
    ArrayList<Collider> activeColliders = new ArrayList<>();
    ArrayList<Player> activePlayers = new ArrayList<>();
    ArrayList<TextView> activePopups = new ArrayList<>();

    /**
     * LAYOUT
     **/
    // Layout
    RelativeLayout layout;
    LinearLayout cardLayout;

    //CardZone & cardContainers
    CardZone cardZone;
    ImageView containerCard1;
    ImageView containerCard2;
    ImageView containerCard3;
    int cardUsed = 0;

    /**
     * SOUND
     **/
    MediaPlayer pointSound;
    MediaPlayer backgroundMusic;

    /**
     * VISUAL EFFECTS
     **/
    Canvas fxCanvas;
    boolean canvasCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullscreen();
        setContentView(R.layout.activity_game);

        running = true;

        /**
         * INITIALIZATION
         */
        initializeOnlineParameters();
        initializeLayoutViews();
        initializeCards();
        initializeAudio();
        initializePlayers();
        initializeIslandDomain(100);
        initializeListeners();

    }

    @Override
    protected void onStart(){
        super.onStart();

        /**
         * ASYNCHRONOUS TASKS
         **/
        // Online data gatherer task
        startRemoteTask();

        // Start game loop
        startRefreshTimer();

    }

    private void startRemoteTask() {

        remoteTask = new RemoteDataTask();
        //remoteTask.execute();

    }

    private void initializeListeners() {

        // Layout onTouchListener
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                localPlayerShoot();
                // UNIMPLEMENTED: Point-and-click movement
                //moveObjectTo(localPlayer, event.getX(), event.getY());
                return false;

            }
        });

        // Layout dimensions listener
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                // We can get layout dimensions at this point.

                int w = layout.getWidth();
                int h = layout.getHeight();

                Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                fxCanvas = new Canvas(b);

                canvasCreated = true;

            }
        });

    }

    private void initializeLayoutViews() {

        // Layout
        layout = (RelativeLayout) findViewById(R.id.gameLayout);
        cardLayout = (LinearLayout) findViewById(R.id.cardLayout);

        // Debugging logs
        log = (TextView) findViewById(R.id.log);
        frameLog = (TextView) findViewById(R.id.frameLog);

        // Joystick
        joystick = (Joystick) findViewById(R.id.joystick);

        // Timer and scoreboard
        timer = (TextView) findViewById(R.id.timer);
        textViewCounter1 = (TextView) findViewById(R.id.textViewCounter1);
        textViewCounter2 = (TextView) findViewById(R.id.textViewCounter2);

        // Images
        treasureImageView = (ImageView) findViewById(R.id.treasureImageView);

    }

    private void setFullscreen() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initializeOnlineParameters() {

        Intent intent = getIntent();
        this.matchId = intent.getIntExtra("matchId", -1);
        this.assignedPlayer = intent.getIntExtra("assignedPlayer", -1);

        if (assignedPlayer == 1) this.oppositePlayer = 2;
        else if (assignedPlayer == 2) this.oppositePlayer = 1;

        System.out.println("Match ID: " + matchId);
        System.out.println("Assigned player: " + assignedPlayer);

    }

    private void initializeCards() {

        containerCard1 = (ImageView) findViewById(R.id.card1);
        containerCard2 = (ImageView) findViewById(R.id.card2);
        containerCard3 = (ImageView) findViewById(R.id.card3);
        containerCard1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cardUsed = 1;
                return false;
            }
        });
        containerCard2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cardUsed = 2;
                return false;
            }
        });
        containerCard3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cardUsed = 3;
                return false;
            }
        });
        cardZone = new CardZone(cardLayout, containerCard1, containerCard2, containerCard3);

    }

    private void initializeIslandDomain(float radius) {

        islandDomain = new IslandDomain(this, radius);
        layout.addView(islandDomain);

        treasureImageView.bringToFront();

    }

    private void initializePlayers() {

        localPlayer = new Player(GameActivity.this, null);
        localPlayer.setCardZone(cardZone);
        remotePlayer = new Player(GameActivity.this, null);

        //localPlayer.showHitbox();

        ViewGroup.LayoutParams playerParams = new ViewGroup.LayoutParams((int) Graphics.toPixels(this, 60),
                                                                         (int) Graphics.toPixels(this, 90));

        localPlayer.setLayoutParams(playerParams);
        remotePlayer.setLayoutParams(playerParams);

        activePlayers.add(localPlayer);
        activePlayers.add(remotePlayer);

        layout.addView(localPlayer);
        layout.addView(remotePlayer);

        System.out.println(activePlayers);
    }

    private void initializeAudio() {

        // Initialize score sound
        pointSound = MediaPlayer.create(getApplicationContext(), R.raw.point);

        // Initialize background music
        backgroundMusic = MediaPlayer.create(getApplicationContext(), R.raw.background_music);

        // Start background music
        // TODO: Add condition in case the user has turned off sound in the settings.
        backgroundMusic.start();

    }

    private void localPlayerShoot() {

        if (localPlayer.canShoot()) {
            float pW = localPlayer.getWidth();
            float pH = localPlayer.getHeight();
            float oX = localPlayer.getX() + pW / 2;
            float oY = localPlayer.getY() + pH / 2;
            int baseDamage = 20;

            if (localPlayer.isEffectActive(Card.Effect.ATTACK_UP)) {
                baseDamage *= 2;
            }

            System.out.println(oX + ", " + oY);
            Projectile projectile = new Projectile(GameActivity.this, joystick.getCurrentAngle(), oX, oY, baseDamage);
            GameActivity.this.activeProjectiles.add(projectile);
            GameActivity.this.activeColliders.add(projectile);
            layout.addView(projectile);

            if (localPlayer.isEffectActive(Card.Effect.TRIPLE_SHOT)) {

                Projectile projectileL = new Projectile(GameActivity.this, joystick.getCurrentAngle() - .3f, oX, oY, baseDamage);
                GameActivity.this.activeProjectiles.add(projectileL);
                GameActivity.this.activeColliders.add(projectileL);
                layout.addView(projectileL);

                Projectile projectileR = new Projectile(GameActivity.this, joystick.getCurrentAngle() + .3f, oX, oY, baseDamage);
                GameActivity.this.activeProjectiles.add(projectileR);
                GameActivity.this.activeColliders.add(projectileR);
                layout.addView(projectileR);
            }

            localPlayer.setDelay(20);
        }

    }

    @Override
    protected void onPause() {
        running = false;
        backgroundMusic.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        running = true;
        backgroundMusic.start();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // TODO: Notify server that the local player has left the match.
    }

    private void startRefreshTimer() {
        this.refreshTimer = new Timer();
        refreshTimer.schedule(new RefreshTask(), 0, refreshPeriod);
    }

    // Displays a message on the game log
    private void log(String message) {
        this.log.setText(Float.toString(this.logIndex) + ": " + message);
        this.logIndex++;
    }

    private void moveObjectTo(View object, float x, float y) {
        this.destX = x;
        this.destY = y;
        this.moving = true;
    }

    private void showBoatTrace() {
        float oX = localPlayer.getX() + 30; // Compensem per l'espai buit de l'imatge
        float oY = localPlayer.getY() + 76; // Compensem per l'espai buit de l'imatge

        float angle = (float) Math.toDegrees(joystick.getCurrentAngle()) + 90;

        Trace trace = new Trace(GameActivity.this, oX, oY, angle);
        activeTraces.add(trace);
        layout.addView(trace);

        if (activeTraces.size() > 20) {
            layout.removeView(activeTraces.get(0));
            activeTraces.remove(0);
        }

        localPlayer.bringToFront();
    }

    /**
     * Shows text pop-up above player position
     * @param p     Player to show the pop-up above.
     * @param msg   Message to display.
     * @param time  Time in milliseconds to display the message for.
     */
    private void showPlayerPopup(Player p, String msg, int time, boolean disperse) {

        TextView popup = new TextView(this);
        popup.setText(msg);

        float oX = p.getX() - popup.getWidth() / 2;
        float oY = p.getY() - 35;

        float detourX, detourY;

        if(disperse){
            detourX = (float) (p.getWidth() / 2 * Math.random());
            detourY = (float) (20 * Math.random());
        }else{
            detourX = p.getWidth() / 2;
            detourY = 0;
        }

        popup.setX(oX + detourX);
        popup.setY(oY + detourY);

        activePopups.add(popup);

        layout.addView(popup);
        popup.animate().setStartDelay(time).alpha(0).y(oY - 100).setDuration(1000);

    }

    private void checkProjectileCollisions() {
        Iterator<Projectile> projectileIterator = activeProjectiles.iterator();

        while (projectileIterator.hasNext()) {

            Projectile p = projectileIterator.next();

            if (p.isColliding(remotePlayer)) {

                //remotePlayer.setColorFilter(getResources().getColor(R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                remotePlayer.damage(p.getDamage());

                layout.removeView(p);
                projectileIterator.remove();

            } //else remotePlayer.setColorFilter(null);

        }
    }

    private void checkIslandDomainCollisions() {

        if (localPlayer.isColliding(islandDomain)) {
            // Player 1 is colliding with islandDomain.
            if (!player1Inside) {
                // Player 1 was not inside before this collision.
                islandDomain.toggleInvadedStatus();
                player1Inside = !player1Inside;
            }

        } else {
            // Player 1 is not colliding with islandDomain.
            if (player1Inside) {
                // Player 1 was inside before this collision.
                islandDomain.toggleInvadedStatus();
                player1Inside = !player1Inside;
            }

        }

    }

    private void destroyExcessiveViews() {

        // Popups: max 4
        if (activePopups.size() > 4) activePopups.remove(0);

    }

    private void finishGame() {
        if (score1 > score2) log("You win!");
        else if (score1 < score2) log("You lose");
        else if (score1 == score2) log("Draw!");
    }

    private void advanceCounter() {

        frameLog.setText(Integer.toString(currentFrame));

        if (currentFrame % fps == 0) {
            seconds++;

            if (secondsLeft > 0) {
                secondsLeft--;

                int m = secondsLeft / 60;
                int s = secondsLeft % 60;

                timer.setText(Integer.toString(m) + ":" + String.format("%02d", s));

            } else {
                // Time ran out.
                finishGame();
            }
        }

        if (player1Inside) {

            if (--framesUntilTick1 == 0) {

                if (score1 < 100) {

                    score1++;
                    framesUntilTick1 = fps / 2;
                    textViewCounter1.setText(Integer.toString(score1) + "%");

                    showPlayerPopup(localPlayer, "+1", 250, true);
                    if (running) {
                        pointSound.start();
                    }
                } else {
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
    private void retrieveRemoteAction() {

    }

    // Method that gets called every frame.
    private void refresh() {
        runOnUiThread(new Runnable() {
            public void run() {

                // Advance current frame
                currentFrame++;

                // Garbage collector
                destroyExcessiveViews();

                // Point-and-click controls
                if (remotePlayer.getX() == destX && remotePlayer.getY() == destY) moving = false;
                if (moving) remotePlayer.moveTo(destX, destY);
                remotePlayer.postInvalidate();

                // Control buttons (currently unused)
                if (upPressed) localPlayer.moveUp();
                if (leftPressed) localPlayer.moveLeft();
                if (rightPressed) localPlayer.moveRight();
                if (downPressed) localPlayer.moveDown();

                // Joystick controls
                // IMPORTANT: Block joystick on first frame to avoid disappearing player bug.
                if (currentFrame > 10) localPlayer.move(joystick.getCurrentAngle(), joystick.getCurrentIntensity());

                // Player 2 controls
                //retrieveRemoteAction();

                // Projectile movement
                for (Projectile p : activeProjectiles) p.move();

                // Boat trace
                showBoatTrace();

                // Collisions
                checkProjectileCollisions();

                // Decrease delay to shoot again
                localPlayer.decreaseDelay();

                // islandDomain collisions
                checkIslandDomainCollisions();

                // Card collecting
                // TODO: canviar per condició de col·lisió
                if (secondsLeft % 8 == 5 && currentFrame % fps == 0) drawCard(localPlayer);

                // Card usage
                if (cardUsed != 0) useCard(localPlayer, cardUsed);

                // Effect management
                localPlayer.handleEffects();
                localPlayer.decreaseEffectsDuration();

                // Scoreboard and timer counter
                advanceCounter();

                // Prepare local data to send to server
                localX = localPlayer.getX();
                localY = localPlayer.getY();

                // Apply remote data to remotePlayer
                remotePlayer.moveTo(remoteX, remoteY);

                //test CardZone
                localPlayer.improveVisibilityCardZone(180, 140, 90);

                // Environmental effects
                showDripplets();

            }
        });
    }

    private void showDripplets() {

        if(canvasCreated){

            Paint drippletPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            drippletPaint.setColor(getResources().getColor(R.color.uninvadedIsland));
            drippletPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            float drippletX = layout.getWidth() * ((float) Math.random());
            float drippletY = layout.getHeight() * ((float) Math.random());

            fxCanvas.drawCircle(drippletX, drippletY, 10, drippletPaint);

        }

    }

    private void drawCard(Player player) {
        if (player.handSize() < 3) {
            Card card = new Card();
            cardZone.addCard(card);
            log(Integer.toString(card.getId()));
        }
    }

    private void useCard(Player player, int n) {
        Card usedCard = cardZone.popCard(n);
        int effect = usedCard.getEffect();
        int duration = usedCard.getDuration();
        switch (usedCard.getTarget()) {
            case Card.Target.RANDOM:
                if (Math.random() < .5) {
                    remotePlayer.addEffect(effect, duration);
                    break;
                }
            case Card.Target.SELF:
                localPlayer.addEffect(effect, duration);
                break;
            case Card.Target.ALL:
                localPlayer.addEffect(effect, duration);
            case Card.Target.OPPONENT:
                remotePlayer.addEffect(effect, duration);
                break;
            case Card.Target.TRAP:
                //TODO: implemantar trampes
                break;
        }

        showPlayerPopup(localPlayer, usedCard.getName(), 1000, false);
        cardUsed = 0;

    }

    /**
     * BACKGROUND TASKS
     **/
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

            while (connectionActive) {
                /* SEND DATA */
                getJSON("https://pis04-ub.herokuapp.com/send_local_action.php?matchId=" + matchId
                        + "&player=" + assignedPlayer
                        + "&x=" + localX
                        + "&y=" + localY, 2000);

                /* RETRIEVE DATA */
                //This returns a JSON object with a {"x": x,"y": y} pattern.
                String data = getJSON("https://pis04-ub.herokuapp.com/retrieve_remote_action.php?matchId=" + matchId
                        + "&player=" + oppositePlayer, 2000);

                System.out.println(currentFrame + ": " + data);

                // Parse the JSON information into a Point object.
                Point p = new Gson().fromJson(data, Point.class);

                // Set X and Y coordinates retrieved from JSON to the remoteX and remoteY global
                // variables. These variables will be used to position remotePlayer on the next frame.
                remoteX = p.x;
                remoteY = p.y;
            }
            return null;
        }
    }

    /**
     * Get JSON response as String from URL.
     *
     * @param url     URL to retrieve JSON from.
     * @param timeout Time available to establish connection.
     * @return        JSON response as String.
     */
    @Nullable
    private String getJSON(String url, int timeout) {
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
                        sb.append(line);
                        sb.append("\n");
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
