package com.example.lluismontabes.gameofboatsandcards.Interface;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.example.lluismontabes.gameofboatsandcards.Model.CubicBezierCurve;
import com.example.lluismontabes.gameofboatsandcards.Views.Card;
import com.example.lluismontabes.gameofboatsandcards.Views.CardSpawn;
import com.example.lluismontabes.gameofboatsandcards.Views.CardZone;
import com.example.lluismontabes.gameofboatsandcards.Model.Collider;
import com.example.lluismontabes.gameofboatsandcards.Model.Data;
import com.example.lluismontabes.gameofboatsandcards.Model.Graphics;
import com.example.lluismontabes.gameofboatsandcards.Views.IslandDomain;
import com.example.lluismontabes.gameofboatsandcards.Views.Joystick;
import com.example.lluismontabes.gameofboatsandcards.Views.Player;
import com.example.lluismontabes.gameofboatsandcards.Views.Projectile;
import com.example.lluismontabes.gameofboatsandcards.R;
import com.example.lluismontabes.gameofboatsandcards.Views.Trace;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

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

import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.*;

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
    // Connection
    RemoteDataTask remoteTask;
    boolean connectionActive = true;
    int connectionFrequency = 1;
    int lastFrameChecked = 0;
    float latency;
    boolean lastCheckSuccessful = false;

    // Synchronization
    boolean localPlayerReady = false;
    boolean remotePlayerReady = false;

    // Online player positioning
    Point lastReadRemotePosition = new Point(0, 0);
    Point remotePosition = new Point(0, 0);
    Point localPosition = new Point(0, 0);
    float lastReadRemoteAngle = 0;
    float remoteAngle = 0;
    float localAngle = 0;

    // Remote player curve path
    CubicBezierCurve remoteCurve = new CubicBezierCurve();

    // Online player and match IDs
    int matchId;
    int assignedPlayer;
    int oppositePlayer;

    // Online match events
    // IMPORTANT: @SerializedName("n") makes it possible for Gson to parse the incoming
    // JSON into an EventIndexPair properly, as the Event is received as an integer.
    private enum Event {
        @SerializedName("0") NONE,
        @SerializedName("1") LOCAL_PLAYER_DIED,
        @SerializedName("2") REMOTE_PLAYER_DIED,
        @SerializedName("3") LOCAL_PLAYER_RESPAWNED,
        @SerializedName("4") REMOTE_PLAYER_RESPAWNED,
        @SerializedName("5") LOCAL_PLAYER_FIRED,
        @SerializedName("6") REMOTE_PLAYER_FIRED,
        @SerializedName("7") LOCAL_PLAYER_DAMAGED,
        @SerializedName("8") REMOTE_PLAYER_DAMAGED,
        @SerializedName("9") LOCAL_PLAYER_USED_CARD,
        @SerializedName("10") REMOTE_PLAYER_USED_CARD,
        @SerializedName("11") LOCAL_PLAYER_WON
    }
    private Event localActiveEvent = Event.NONE;
    private int localEventIndex = 0;
    private int lastSentLocalEventIndex = -1;

    private Event remoteActiveEvent = Event.NONE;
    private int remoteEventIndex = 0;
    private int lastReadRemoteEventIndex = 0;

    // Online invasion and winning statuses
    public enum Invader {NONE, LOCAL_PLAYER, REMOTE_PLAYER}
    public enum GameState {UNFINISHED, TIME_OUT, LOCAL_WON, REMOTE_WON, DRAW}
    private GameState currentGameState = GameState.UNFINISHED;

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
    boolean localPlayerInside = false;
    boolean remotePlayerInside = false;
    boolean running = false;

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
    TextView textViewCounterLocal;
    TextView textViewCounterRemote;

    // Images
    ImageView treasureImageView;

    /**
     * STATISTICS
     **/
    // Scores
    int localScore = 0;
    boolean localPlayerScoring = false;
    int remoteScore = 0;

    // Time
    int currentFrame = 0;
    int secondsElapsed = 0;
    int secondsLeft = 120;
    boolean gameFinished;

    // Counters
    static byte MAX_FRAMES_UNTIL_TICK = fps / 2;
    byte framesUntilTickLocal = MAX_FRAMES_UNTIL_TICK;
    byte framesUntilTickRemote = MAX_FRAMES_UNTIL_TICK;

    // Misc
    int killsStats = 0;
    int deathsStats = 0;
    int shotsFiredStats = 0;
    int shotsHitStats = 0;
    int shotsReceivedStats = 0;
    int cardsUsedStats = 0;

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

    CardSpawn cardSpawn;
    Card cardSpawned;
    boolean caught = true;
    int cardUsed = 0;
    int cardSpawnCooldown;
    int cardVisibilityTimer;
    boolean cardHasSpawned = true;

    private static final int MAX_CARD_COOLDOWN = 500;
    private static final int MIN_CARD_COOLDOWN = 100;
    private static final int CARD_VISIBLE_TIME = 300;

    private Card.Effect effectToSend;

    /**
     * SOUND
     **/
    MediaPlayer backgroundMusic;
    MediaPlayer pointSound;
    MediaPlayer fireSound;
    MediaPlayer hitSound;

    /**
     * VISUAL EFFECTS
     **/
    Canvas fxCanvas;
    boolean canvasCreated = false;

    /**
     * CARDS & CARD EFFECTS
     **/
    private short[] cardEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestFullscreen();
        setContentView(R.layout.activity_game);

        running = true;
        gameFinished = false;

        /**
         * INITIALIZATION
         **/
        initializeOnlineParameters();
        initializeLayoutViews();
        initializeCards();
        initializeAudio();
        initializePlayers();
        initializeIslandDomain(100);
        initializeListeners();
        initializeCardEffects();

    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         * ASYNCHRONOUS TASKS
         **/
        // Online data gatherer task
        startRemoteTask();

        // Start game loop
        startRefreshTimer();

        localPlayerReady = true;

    }

    private void startRemoteTask() {

        remoteTask = new RemoteDataTask();
        remoteTask.execute();

    }

    private void initializeCardEffects() {
        cardEffects = new short[Card.TOTAL_CARD_NUMBER * 2];
    }

    private void initializeListeners() {

        // Layout onTouchListener
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cardUsed == 0) playerShoot(localPlayer);
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
        textViewCounterLocal = (TextView) findViewById(R.id.textViewCounterLocal);
        textViewCounterRemote = (TextView) findViewById(R.id.textViewCounterRemote);

        // Images
        treasureImageView = (ImageView) findViewById(R.id.treasureImageView);

    }

    private void requestFullscreen() {
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

        cardSpawn = new CardSpawn(this);
        cardSpawn.setLayoutParams(new ViewGroup.LayoutParams((int) Graphics.toPixels(this, 15),
                (int) Graphics.toPixels(this, 20)));
        cardSpawnCooldown = (int) (Math.random() * (MAX_CARD_COOLDOWN - MIN_CARD_COOLDOWN)) + MIN_CARD_COOLDOWN;
        cardVisibilityTimer = 0;
        layout.addView(cardSpawn);

    }

    private void initializeIslandDomain(float radius) {

        islandDomain = new IslandDomain(this, radius);
        layout.addView(islandDomain);

        treasureImageView.bringToFront();

    }

    private void initializePlayers() {

        localPlayer = new Player(GameActivity.this);
        localPlayer.setMoving(true);

        remotePlayer = new Player(GameActivity.this);
        remotePlayer.setMaxVelocity();

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

        // Initialize sound effects
        pointSound = MediaPlayer.create(getApplicationContext(), R.raw.point);
        fireSound = MediaPlayer.create(getApplicationContext(), R.raw.fire);
        hitSound = MediaPlayer.create(getApplicationContext(), R.raw.hit);

        // Initialize background music
        backgroundMusic = MediaPlayer.create(getApplicationContext(), R.raw.background_music);

        // Start background music
        // TODO: Add condition in case the user has turned off sound in the settings.
        backgroundMusic.start();

    }

    private void playerShoot(Player player) {

        if (player.canShoot()) {

            float angle = player.getAngle(); // Radians

            float pW = player.getWidth();
            float pH = player.getHeight();
            float oX = player.getX() + pW * Math.max(0.5f, (float) Math.cos(angle));
            float oY = player.getY() + pH * Math.max(0.5f, (float) Math.sin(angle));
            int baseDamage = 20;
            boolean firedByLocal = player == localPlayer;

            if (isEffectActive(Card.Effect.ATTACK_UP)) {
                baseDamage *= 2;
            }

            Projectile projectile = new Projectile(GameActivity.this, firedByLocal, angle, oX, oY, baseDamage);
            GameActivity.this.activeProjectiles.add(projectile);
            GameActivity.this.activeColliders.add(projectile);

            layout.addView(projectile);

            if (firedByLocal){
                activateEventFlag(Event.LOCAL_PLAYER_FIRED);
                shotsFiredStats++;
                player.restoreFireCooldown();
            }

            /*if (fireSound.isPlaying()) fireSound.stop();
            fireSound.start();*/

            if (isEffectActive(Card.Effect.TRIPLE_SHOT)) {

                Projectile projectileL = new Projectile(GameActivity.this, firedByLocal, angle - .3f, oX, oY, baseDamage);
                GameActivity.this.activeProjectiles.add(projectileL);
                GameActivity.this.activeColliders.add(projectileL);

                Projectile projectileR = new Projectile(GameActivity.this, firedByLocal, angle + .3f, oX, oY, baseDamage);
                GameActivity.this.activeProjectiles.add(projectileR);
                GameActivity.this.activeColliders.add(projectileR);

                layout.addView(projectileL);
                layout.addView(projectileR);

                if (player == localPlayer) shotsFiredStats += 2;
            }

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
        new AlertDialog.Builder(GameActivity.this)
                .setTitle(getString(R.string.exitGameDialogTitle))
                .setMessage(getString(R.string.exitGameWarning))
                .setNegativeButton(getString(R.string.exitGameNegativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setPositiveButton(getString(R.string.exitGamePositiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameFinished = true;
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

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
     *
     * @param p    Player to show the pop-up above.
     * @param msg  Message to display.
     * @param time Time in milliseconds to display the message for.
     */
    private void showPlayerPopup(Player p, String msg, int time, boolean disperse) {

        TextView popup = new TextView(this);
        popup.setText(msg);

        float oX = Math.min(Math.max(p.getX() - popup.getWidth() / 2, 0), layout.getWidth() - popup.getWidth());
        float oY = Math.min(Math.max(p.getY() - 35, 0), layout.getHeight() - popup.getHeight());

        float detourX, detourY;

        if (disperse) {
            detourX = (float) (p.getWidth() / 2 * Math.random());
            detourY = (float) (20 * Math.random());
        } else {
            detourX = 0;
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
            Player playerToCheck;
            if (p.isFiredByLocal()) {
                playerToCheck = remotePlayer;
            } else {
                playerToCheck = localPlayer;
            }

            if (p.isColliding(playerToCheck)) {

                playerToCheck.boatImageView.setColorFilter(getResources().getColor(R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                playerToCheck.damage(p.getDamage());
                showPlayerPopup(remotePlayer, "-" + p.getDamage() + " ♡", 300, true);

                layout.removeView(p);
                projectileIterator.remove();

                if (hitSound.isPlaying()) hitSound.stop();
                hitSound.start();

            } else playerToCheck.boatImageView.setColorFilter(null);

        }
    }

    /**
     * Check if any player is colliding with the IslandDomain.
     */
    private void checkIslandDomainCollisions() {

        boolean localPlayerColliding = localPlayer.isColliding(islandDomain);
        boolean remotePlayerColliding = remotePlayer.isColliding(islandDomain);

        if (localPlayerColliding && remotePlayerColliding) {

            islandDomain.setInvadedBy(Invader.NONE);

            if (!localPlayerInside) {
                // localPlayer was not inside before the current state.
                localPlayerInside = true;
            }

            if (!remotePlayerInside) {
                // remotePlayer was not inside before the current state.
                remotePlayerInside = true;
            }

        } else if (localPlayerColliding) {

            // localPlayer is colliding with islandDomain.
            //if (!localPlayerInside) {

                // localPlayer was not inside before this collision.
                islandDomain.setInvadedBy(Invader.LOCAL_PLAYER);
                localPlayerInside = true;
            remotePlayerInside = false;
            //}

        } else if (remotePlayerColliding) {

            // remotePlayer is colliding with islandDomain.
            //if (!remotePlayerInside) {

                // remotePlayer was not inside before this collision.
                islandDomain.setInvadedBy(Invader.REMOTE_PLAYER);
                remotePlayerInside = true;
            localPlayerInside = false;
            //}

        } else {

            // No one is colliding with islandDomain.
            islandDomain.setInvadedBy(Invader.NONE);

            if (localPlayerInside) {
                // localPlayer was inside before the current state.
                localPlayerInside = false;
            }

            if (remotePlayerInside) {
                // remotePlayer was inside before the current state.
                remotePlayerInside = false;
            }

        }

    }

    private void destroyExcessiveViews() {

        // Popups: max 4
        if (activePopups.size() > 4) layout.removeView(activePopups.remove(0));

        // Projectiles: max 10
        if (activeProjectiles.size() > 18) layout.removeView(activeProjectiles.remove(0));

    }

    private void finishGame() {

        if (!gameFinished) {

            gameFinished = true;
            Intent i = new Intent(GameActivity.this, GameEndActivity.class);

            if (localScore > remoteScore) {
                currentGameState = GameState.LOCAL_WON;
            } else if (localScore < remoteScore) {
                currentGameState = GameState.REMOTE_WON;
            } else {
                currentGameState = GameState.DRAW;
            }

            i.putExtra("gameState", currentGameState);
            i.putExtra("localScoreStats", localScore);
            i.putExtra("remoteScoreStats", remoteScore);
            i.putExtra("killsStats", killsStats);
            i.putExtra("deathsStats", deathsStats);
            i.putExtra("shotsFiredStats", shotsFiredStats);
            i.putExtra("cardsUsedStats", cardsUsedStats);
            i.putExtra("shotsHitStats", shotsHitStats);

            startActivity(i);

            connectionActive = false;
            finish();
        }
    }

    private void advanceCounter() {

        // CLOCK
        frameLog.setText(Integer.toString(currentFrame));

        if (currentFrame % fps == 0) {
            secondsElapsed++;

            if (secondsLeft > 0) {
                secondsLeft--;

                int m = secondsLeft / 60;
                int s = secondsLeft % 60;

                timer.setText(Integer.toString(m) + ":" + String.format("%02d", s));

            }
        }

        // LOCAL PLAYER SCORING
        if (localPlayerInside && !remotePlayerInside) {

            if (--framesUntilTickLocal == 0) {

                if (localScore < 100) {

                    localScore++;
                    framesUntilTickLocal = MAX_FRAMES_UNTIL_TICK;

                    showPlayerPopup(localPlayer, "+1", 250, true);

                    if (running) {
                        pointSound.start();
                    }

                }

            }

            localPlayerScoring = true;
            textViewCounterLocal.setTextColor(getResources().getColor(R.color.localCounterActive));

        } else {

            localPlayerScoring = false;
            framesUntilTickLocal = MAX_FRAMES_UNTIL_TICK;
            textViewCounterLocal.setTextColor(getResources().getColor(R.color.counterStopped));

        }

        // SCORE COUNTERS
        textViewCounterLocal.setText(Integer.toString(localScore) + "%");
        textViewCounterRemote.setText(Integer.toString(remoteScore) + "%");

    }

    // Method that gets called every frame.
    private void refresh() {
        runOnUiThread(new Runnable() {
            public void run() {

                // Advance current frame
                currentFrame++;

                // Garbage collector
                destroyExcessiveViews();

                // Starting position
                if (currentFrame <= 10) setStartPositions();

                // Joystick controls
                // IMPORTANT: Block joystick on first frame to avoid disappearing player bug.
                else moveLocalPlayer();

                // Projectile movement
                for (Projectile p : activeProjectiles) p.move();

                // Boat trace
                showBoatTrace();

                // Collisions
                checkProjectileCollisions();

                // Death check
                checkPlayerHealth();

                // localPlayer respawn check
                checkLocalPlayerRespawn();

                // Decrease cooldown to shoot again
                localPlayer.decreaseFireCooldown();

                // islandDomain collisions
                checkIslandDomainCollisions();

                // Card usage
                if (cardUsed == 0) {
                    effectToSend = NONE;
                } else {
                    useCard(localPlayer, cardUsed);
                }

                //Card spawning
                spawnCard();
                checkCollisionCardSpawn();

                // Effect management
                handleEffects();
                decreaseEffectsDuration();

                // Scoreboard and timer counter
                advanceCounter();

                // Prepare local data to send to server
                localPosition.set((int) localPlayer.getX(), (int) localPlayer.getY());
                localAngle = (float) Math.toDegrees(localPlayer.getAngle());

                if (currentFrame % 6 == 5) remoteCurve.set(lastReadRemotePosition, remotePosition, lastReadRemoteAngle, remoteAngle);

                // Move remotePlayer to the retrieved position
                if (remotePosition != lastReadRemotePosition){
                    System.out.println("New remote position detected");
                    lastReadRemotePosition = remotePosition;
                }
                if (remoteAngle != lastReadRemoteAngle){
                    System.out.println("New remote angle detected");
                    remotePlayer.restoreMovement();
                    remotePlayer.setMoving(true);
                    lastReadRemoteAngle = remoteAngle;
                }
                //remoteCurve.set(remotePlayer.getPosition(), remotePosition, (float) Math.toDegrees(remotePlayer.getRotation()), remoteAngle);
                if(remotePlayer.isMoving())
                    remotePlayer.moveInCurve(remoteCurve);

                //remotePlayer.moveTo(remotePosition);

                //test CardZone
                improveVisibilityCardZone(180, 140, 90);

                // Process remote events
                if (remoteActiveEvent != null) handleRemoteEvent();

                // Check game finish conditions
                checkGameFinish();

            }
        });
    }

    private void checkGameFinish() {

        if (secondsLeft <= 0) finishGame(); // Time ran out
        else if (localScore >= 100){
            activateEventFlag(Event.LOCAL_PLAYER_WON);
            finishGame(); // localPlayer reached 100%
        }
        else if (remoteScore >= 100){
            finishGame(); // remotePlayer reached 100%
        }

    }

    /**
     * If localPlayer is dead, check if localPlayer should respawn on this frame or decrease
     * the counter until it respawns.
     */
    private void checkLocalPlayerRespawn() {

        if (!localPlayer.isAlive()){

            System.out.println("localPlayer is dead");

            // Calculate frames left for player respawn.
            if (localPlayer.getTimeToRespawn() == 0){

                // If there are 0 frames left, respawn.
                localPlayer.respawn();

                // Activate flag to notify server of localPlayer respawn
                activateEventFlag(Event.LOCAL_PLAYER_RESPAWNED);
            }

        }

    }

    /**
     * Handle remote event previously retrieved from server.
     */
    private void handleRemoteEvent() {

        if (remoteEventIndex > lastReadRemoteEventIndex){

            log("Remote event: (" + remoteEventIndex + ") " + remoteActiveEvent);

            switch(remoteActiveEvent){

                // NOTICE: These Events are retrieved from the server, sent by the other player.
                // Therefore, every LOCAL event will refer to the sender, that is, the remotePlayer
                // from the point of view of this method.

                case REMOTE_PLAYER_DIED:
                    // remotePlayer killed localPlayer
                    localPlayer.die(isEffectActive(Card.Effect.QUICK_REVIVE));
                    deathsStats++;
                    break;

                case REMOTE_PLAYER_DAMAGED:
                    // TODO: Check real damage inflicted.
                    // remotePlayer damaged localPlayer
                    localPlayer.damage(20);
                    break;

                case LOCAL_PLAYER_RESPAWNED:
                    // remotePlayer respawned
                    remotePlayer.respawn();
                    remotePlayer.setMaxVelocity();
                    break;

                case LOCAL_PLAYER_FIRED:
                    // remotePlayer fired
                    playerShoot(remotePlayer);
                    break;

                case LOCAL_PLAYER_USED_CARD:
                    // remotePlayer used card
                    // TODO
                    break;

                case LOCAL_PLAYER_WON:
                    // remotePlayer won the game
                    remoteScore = 100;
                    finishGame();
                    break;

                case NONE:
                default:
                    break;

            }

            lastReadRemoteEventIndex = remoteEventIndex;

        }

    }

    /**
     * Move local player according to joystick input.
     */
    private void moveLocalPlayer() {

        if (localPlayer.isAlive()) {

            if (joystick.getCurrentIntensity() != 0) {
                localPlayer.accelerate();
                localPlayer.move(joystick.getCurrentAngle(), joystick.getCurrentIntensity());
            } else {
                localPlayer.decelerate();
                localPlayer.move(joystick.getCurrentAngle(), 0.4f);
            }

            keepInBounds(localPlayer);

        } else {
            localPlayer.setX((layout.getWidth() - localPlayer.getWidth()) / 2);
            localPlayer.setY(layout.getHeight() - localPlayer.getHeight());
            joystick.resetCurrentAngle();
        }

    }

    /**
     * Check if a player has died.
     */
    private void checkPlayerHealth() {

        /*if (localPlayer.isAlive()){
            if (localPlayer.getHealth() <= 0) {

                localPlayer.die(isEffectActive(QUICK_REVIVE));
                activateEventFlag(Event.LOCAL_PLAYER_DIED);

                deathsStats++;
            }
        }*/

        if (remotePlayer.isAlive()){
            if (remotePlayer.getHealth() <= 0) {

                remotePlayer.die(isEffectActive(QUICK_REVIVE));
                activateEventFlag(Event.REMOTE_PLAYER_DIED);

                killsStats++;
            }
        }

    }

    /**
     * Activate an event flag and increase the event index.
     * @param event Event to activate.
     */
    private void activateEventFlag(Event event) {
        localActiveEvent = event;
        localEventIndex++;
        log ("Activated event flag: (" + localEventIndex + ")" + localActiveEvent);
    }

    /**
     * Set starting position of players. (Bottom for player 1, top for player 2)
     */
    private void setStartPositions() {

        int centerX = (layout.getWidth() - localPlayer.getWidth()) / 2;

        if (assignedPlayer == 1 || assignedPlayer == -1) {

            // Spawn local player at bottom position
            localPlayer.setStartPosition(new Point(centerX, layout.getHeight() - localPlayer.getHeight()));
            localPlayer.toStartPosition();

            // Spawn remote player at top position
            remotePlayer.setStartPosition(new Point(centerX, 0));
            remotePlayer.toStartPosition();

        } else if (assignedPlayer == 2) {

            // Spawn local player at bottom position
            remotePlayer.setStartPosition(new Point(centerX, layout.getHeight() - localPlayer.getHeight()));
            remotePlayer.toStartPosition();

            // Spawn remote player at top position
            localPlayer.setStartPosition(new Point(centerX, 0));
            localPlayer.toStartPosition();

        }

    }

    /**
     * UNUSED
     */
    private void showDripplets() {

        if (canvasCreated) {

            Paint drippletPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            drippletPaint.setColor(getResources().getColor(R.color.uninvadedIsland));
            drippletPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            float drippletX = layout.getWidth() * ((float) Math.random());
            float drippletY = layout.getHeight() * ((float) Math.random());

            fxCanvas.drawCircle(drippletX, drippletY, 10, drippletPaint);

        }
    }

    /**
     * Spawns card at random position.
     */
    private void spawnCard() {
        if (!cardHasSpawned) {
            cardSpawned = new Card();
            ImageView im = (ImageView) cardSpawn.findViewById(R.id.cardSpawn);
            Drawable d = ContextCompat.getDrawable(this, cardSpawned.getResourceID());
            im.setImageDrawable(d);
            cardSpawn.setVisibility(View.VISIBLE);
            cardHasSpawned = true;
            caught = false;
            cardVisibilityTimer = 0;
            do {
                cardSpawn.setX((float) (Math.random() * (layout.getWidth() - cardSpawn.getWidth())));
                cardSpawn.setY((float) (Math.random() * (layout.getHeight() - cardSpawn.getHeight())));
            } while (islandDomain.isColliding(cardSpawn) || localPlayer.isColliding(cardSpawn) || remotePlayer.isColliding(cardSpawn));
        } else if (caught || cardVisibilityTimer > CARD_VISIBLE_TIME) {
            cardSpawnCooldown--;
            cardSpawn.setVisibility(View.GONE);
            if (cardSpawnCooldown == 0) {
                cardSpawnCooldown = (int) (Math.random() * (MAX_CARD_COOLDOWN - MIN_CARD_COOLDOWN)) + MIN_CARD_COOLDOWN;
                cardHasSpawned = false;
            }
            caught = true;
        } else {
            if (cardVisibilityTimer > CARD_VISIBLE_TIME - 48) {
                if (cardVisibilityTimer % 6 < 3) {
                    cardSpawn.setVisibility(View.INVISIBLE);
                } else {
                    cardSpawn.setVisibility(View.VISIBLE);
                }
            }
            cardVisibilityTimer++;
        }
    }

    /**
     * Keeps specified Player object inside the screen's dimensions.
     * @param p Player to keep in bounds.
     */
    private void keepInBounds(Player p) {
        p.setX(Math.min(Math.max(p.getX(), 0), layout.getWidth() - p.getWidth()));
        p.setY(Math.min(Math.max(p.getY(), 0), layout.getHeight() - p.getHeight()));
    }

    /**
     * Check if localPlayer is colliding with currently spawned card, therefore picking it up.
     */
    private void checkCollisionCardSpawn() {
        if (localPlayer.isColliding(cardSpawn) && !caught && cardZone.getCardList().size() < 3) {
            drawCard(localPlayer);
            caught = true;
        }
    }

    /**
     * Add picked up cards to the localPlayer's deck.
     * @param player
     */
    private void drawCard(Player player) {
        //TODO: use Deck class for card drawing
        // (int) (Math.random() * (MAX_CARD_COOLDOWN - MIN_CARD_COOLDOWN)) + MIN_CARD_COOLDOWN;
        cardZone.addCard(cardSpawned);
        log(Integer.toString(cardSpawned.getId()));

    }

    /**
     * Use specified card in the Player's deck.
     * @param player Player whose deck to add to.
     * @param n
     */
    private void useCard(Player player, int n) {
        if (player.isAlive()) {
            Card usedCard = cardZone.removeCard(n);

            Card.Effect effect = usedCard.getEffect();

            switch (usedCard.getTarget()) {

                case SELF:
                    addEffect(effect);
                    break;

                case ALL:
                    addEffect(effect);

                case OPPONENT:
                    effectToSend = effect;
                    break;

            }

            if (usedCard.getEffect() != FULL_RESTORATION) { // Case handled separately in handleEffects()
                showPlayerPopup(localPlayer, usedCard.getEffect().getName(), 1000, false);
            }

            cardsUsedStats++;

        }

        cardUsed = 0;

    }

    private void handleEffects() {
        cardZone.reverseCards(isEffectActive(REVERSED_HAND));
        if (isEffectActive(DISCARD_ONE)) cardZone.discardOne();
        if (isEffectActive(KO)) {
            localPlayer.die(isEffectActive(QUICK_REVIVE));
            removeEffect(QUICK_REVIVE);
            activateEventFlag(Event.LOCAL_PLAYER_DIED);
        }
        if (isEffectActive(FULL_RESTORATION)) {
            showPlayerPopup(localPlayer, "+" + (Player.MAX_HEALTH - localPlayer.getHealth()) + " ♡", 500, false);
            localPlayer.restoreHealth();
        }
        localPlayer.setStunned(isEffectActive(STUNNED));
        localPlayer.setBackwards(isEffectActive(REVERSED_CONTROLS));
        localPlayer.setSpeedUp(isEffectActive(SPEED_UP));
        if (isEffectActive(RANDOM_WARP)) {
            localPlayer.setX((float) (Math.random() * (layout.getWidth() - localPlayer.getWidth())));
            localPlayer.setY((float) (Math.random() * (layout.getHeight() - localPlayer.getHeight())));
        }
        if (isEffectActive(DISPEL)) removeAllEffects();
    }

    private void decreaseEffectsDuration() {
        for (int pos = 0; pos < cardEffects.length; pos++) {
            if (cardEffects[pos] > 0) cardEffects[pos]--;
        }
    }

    private void removeAllEffects() {
        for (int pos = 0; pos < cardEffects.length; pos++) {
            cardEffects[pos] = 0;
        }
    }

    private void addEffect(Card.Effect effect) {
        if (effect != NONE) {
            cardEffects[effect.ordinal()] = effect.getDuration();
        }
    }

    private void removeEffect(Card.Effect effect) {
        cardEffects[effect.ordinal()] = 0;
    }

    private boolean isEffectActive(Card.Effect effect) {
        return cardEffects[effect.ordinal()] != 0;
    }

    private void improveVisibilityCardZone(float maxDistance, float minDistance, int minAlpha) {
        cardZone.improveVisibility(localPlayer, maxDistance, minDistance, minAlpha);
    }

    /**
     * BACKGROUND TASKS
     **/
    // Timer
    public class RefreshTask extends TimerTask {
        @Override
        public void run() {
            if (localPlayerReady && remotePlayerReady){

                // Start the game loop only once both players have posted their
                // ready signals to the server and these signals have been retrieved.

                refresh();
                layout.postInvalidate();
            }
        }
    }

    // Asynchronous task that retrieves the remote player's actions
    public class RemoteDataTask extends AsyncTask<String, String, Void> {

        private boolean running = true;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(String... params) {

            notifyLocalPlayerReady();

            while (connectionActive) {

                if (!remotePlayerReady){

                    // Ask the server if the remote player is ready yet.
                    checkRemotePlayerReady();

                }else {

                    if ((currentFrame % connectionFrequency == 0) && (lastFrameChecked != currentFrame)) {

                        lastFrameChecked = currentFrame;

                        // Send local scoring data
                        sendLocalScoreData();

                        // Retrieve both players' scoring data
                        retrieveScoringData();

                        // Send position & angle data
                        sendLocalPositionData();

                        // Retrieve position & angle data
                        retrieveRemotePositionData();

                        // Send event flag data
                        sendLocalEventData();

                        // Retrieve event flag data
                        retrieveRemoteEventData();

                    }

                }

            }

            return null;

        }

        private void sendLocalScoreData() {

            getJSON("https://pis04-ub.herokuapp.com/send_local_score.php?matchId=" + matchId
                    + "&player=" + assignedPlayer
                    + "&score=" + localScore, 2000);

        }

        private void retrieveScoringData() {

            //This returns a JSON object with a {"score1": int, "score2": int} pattern.
            String data = getJSON("https://pis04-ub.herokuapp.com/retrieve_scores.php?matchId=" + matchId, 2000);

            // Parse the JSON information into a ScorePair object.
            ScorePair p = new Gson().fromJson(data, ScorePair.class);

            // Set score1 and score2 variables retrieved from JSON to the localScore and
            // remoteScore global variables.
            if (p != null){
                if(assignedPlayer == 1 || assignedPlayer == -1){
                    remoteScore = p.score2;
                }else{
                    remoteScore = p.score1;
                }
            }
        }

        private void notifyLocalPlayerReady(){

            // Set the playerX_ready column to 1 on the server database.
            getJSON("https://pis04-ub.herokuapp.com/send_local_ready.php?matchId=" + matchId
                    + "&player=" + assignedPlayer
                    + "&ready=" + 1, 2000);

        }

        private void checkRemotePlayerReady(){

            // This returns a string containing either 1 or 0, representing a boolean value.
            String data = getJSON("https://pis04-ub.herokuapp.com/retrieve_remote_ready.php?matchId=" + matchId
                    + "&player=" + oppositePlayer, 2000);

            System.out.println("Ready: " + data);

            // Get first character, as, for some reason, it returns one extra character.
            int ready = Integer.parseInt(data.substring(0, 1));

            if (ready == 1){
                remotePlayerReady = true;
            }

        }

        private void sendLocalEventData() {

            if (localEventIndex > lastSentLocalEventIndex){

                int eventNumber = localActiveEvent.ordinal();

                getJSON("https://pis04-ub.herokuapp.com/send_local_event.php?matchId=" + matchId
                        + "&player=" + assignedPlayer
                        + "&event=" + eventNumber
                        + "&eventIndex=" + localEventIndex,
                        2000);

                lastSentLocalEventIndex = localEventIndex;

            }

        }

        private void retrieveRemoteEventData(){

            //This returns a JSON object with a {"eventIndex": int, "event": int} pattern.
            String data = getJSON("https://pis04-ub.herokuapp.com/retrieve_remote_event.php?matchId=" + matchId
                    + "&player=" + oppositePlayer, 2000);

            // Parse the JSON information into an EventIndexPair object.
            EventIndexPair p = new Gson().fromJson(data, EventIndexPair.class);

            // Set event and eventIndex variables retrieved from JSON to the remoteActiveEvent and
            // remoteEventIndex global variables.
            // These variables will be used to process events locally on the next frame.
            if (p != null){
                if (p.eventIndex > remoteEventIndex){

                    remoteActiveEvent = p.event;
                    remoteEventIndex = p.eventIndex;

                }
            }

        }

        private void sendLocalPositionData() {

            getJSON("https://pis04-ub.herokuapp.com/send_local_position.php?matchId=" + matchId
                    + "&player=" + assignedPlayer
                    + "&x=" + localPosition.x
                    + "&y=" + localPosition.y
                    + "&angle=" + localAngle,
                    2000);

            System.out.println(localAngle);

        }

        private void retrieveRemotePositionData() {

            //This returns a JSON object with a {"x": int,"y": int} pattern.
            String data = getJSON("https://pis04-ub.herokuapp.com/retrieve_remote_position.php?matchId=" + matchId
                    + "&player=" + oppositePlayer, 2000);

            System.out.println(currentFrame + ": " + data);

            // Parse the JSON information into a Point object.
            PointAnglePair p = new Gson().fromJson(data, PointAnglePair.class);

            // Set X and Y coordinates retrieved from JSON to the remotePosition.x and remotePosition.y global
            // variables. These variables will be used to position remotePlayer on the next frame.
            if (p != null) {

                remotePosition.set(p.x, p.y);
                remoteAngle = p.angle;

                lastCheckSuccessful = true;
                latency = (currentFrame - lastFrameChecked) / 30;

            } else {
                lastCheckSuccessful = false;
            }

        }

        private void sendEffectData() {

            getJSON("https://pis04-ub.herokuapp.com/send_effect.php?matchId=" + matchId
                    + "&player=" + assignedPlayer
                    + "&effect=" + effectToSend.ordinal(), 2000);

        }

        private void retrieveEffectData() {

            //somehow get the effect

            Card.Effect effect = NONE;

            addEffect(effect);

        }

    }

    /**
     * Get JSON response as String from URL.
     *
     * @param url     URL to retrieve JSON from.
     * @param timeout Time available to establish connection.
     * @return JSON response as String.
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

    private class EventIndexPair{
        int eventIndex;
        Event event;
    }

    private class PointAnglePair{
        int x;
        int y;
        float angle;
    }

    private class ScorePair{
        int score1;
        int score2;
    }

}
