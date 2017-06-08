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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lluismontabes.gameofboatsandcards.Model.Collider;
import com.example.lluismontabes.gameofboatsandcards.Model.CubicBezierCurve;
import com.example.lluismontabes.gameofboatsandcards.Model.Data;
import com.example.lluismontabes.gameofboatsandcards.Model.Graphics;
import com.example.lluismontabes.gameofboatsandcards.R;
import com.example.lluismontabes.gameofboatsandcards.Views.Card;
import com.example.lluismontabes.gameofboatsandcards.Views.CardSpawn;
import com.example.lluismontabes.gameofboatsandcards.Views.CardZone;
import com.example.lluismontabes.gameofboatsandcards.Views.IslandDomain;
import com.example.lluismontabes.gameofboatsandcards.Views.Joystick;
import com.example.lluismontabes.gameofboatsandcards.Views.Player;
import com.example.lluismontabes.gameofboatsandcards.Views.Projectile;
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

import static com.example.lluismontabes.gameofboatsandcards.Interface.MainMenuActivity.soundActive;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.DISCARD_ONE;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.DISPEL;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.FULL_RESTORATION;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.KO;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.NONE;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.QUICK_REVIVE;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.RANDOM_WARP;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.REVERSED_CONTROLS;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.REVERSED_HAND;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.SPEED_UP;
import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Effect.STUNNED;

public class AIGameActivity extends AppCompatActivity {

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
    AITask remoteTask;
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
        @SerializedName("9") LOCAL_PLAYER_USED_SPEED_UP,
        @SerializedName("10") LOCAL_PLAYER_USED_ATTACK_UP,
        @SerializedName("11") LOCAL_PLAYER_USED_FULL_RESTORATION,
        @SerializedName("12")LOCAL_PLAYER_USED_STUNNED,
        @SerializedName("13") LOCAL_PLAYER_USED_REVERSED_HAND,
        @SerializedName("14") LOCAL_PLAYER_USED_DISCARD_ONE,
        @SerializedName("15") LOCAL_PLAYER_USED_REVERSED_CONTROLS,
        @SerializedName("16") LOCAL_PLAYER_USED_TRIPLE_SHOT,
        @SerializedName("17") LOCAL_PLAYER_USED_DISPEL,
        @SerializedName("18") LOCAL_PLAYER_USED_KO,
        @SerializedName("19") LOCAL_PLAYER_USED_QUICK_REVIVE,
        @SerializedName("20") LOCAL_PLAYER_USED_RANDOM_WARP,
        @SerializedName("21") LOCAL_PLAYER_WON,
        @SerializedName("22") LOCAL_PLAYER_LEFT
    }
    private Event localActiveEvent = Event.NONE;
    private int localEventIndex = 0;
    private int lastSentLocalEventIndex = -1;

    private Event remoteActiveEvent = Event.NONE;
    private int remoteEventIndex = 0;
    private int lastReadRemoteEventIndex = 0;

    private ArrayList<EventIndexPair> localUnsentEvents = new ArrayList<>();
    private ArrayList<EventIndexPair> remoteUnhandledEvents = new ArrayList<>();

    // Online invasion and winning statuses
    public enum Invader {NONE, LOCAL_PLAYER, REMOTE_PLAYER}
    public enum GameState {UNFINISHED, TIME_OUT, LOCAL_WON, REMOTE_WON, DRAW}
    public enum FinishCondition {TIME_OUT, LOCAL_MAX_SCORE, REMOTE_MAX_SCORE, REMOTE_PLAYER_LEFT}
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

    // Death prompt
    static LinearLayout deathPromptLayout;
    TextView respawnTimerTextView;
    static boolean deathPromptActive = false;

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
        initializeReleaseConfig();

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

    protected void onStop() {
        super.onStop();
        connectionActive = false;
        running = false;
    }

    private void startRemoteTask() {

        remoteTask = new AITask();
        remoteTask.execute();

    }

    private void initializeReleaseConfig() {
        this.log.setAlpha(0);
        this.frameLog.setAlpha(0);
    }

    private void initializeCardEffects() {
        cardEffects = new short[Card.TOTAL_CARD_NUMBER * 2];
    }

    private void initializeListeners() {

        // Layout onTouchListener
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cardUsed == 0) spawnProjectile(localPlayer);
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

        // Death prompt
        deathPromptLayout = (LinearLayout) findViewById(R.id.deathPromptLayout);
        respawnTimerTextView = (TextView) findViewById(R.id.respawnTimerTextView);

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

        localPlayer = new Player(AIGameActivity.this, true);
        localPlayer.setMoving(true);

        remotePlayer = new Player(AIGameActivity.this, false);
        remotePlayer.setRemoteVelocity();

        remotePlayer.bringToFront();
        localPlayer.bringToFront();

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

        if (soundActive) {
            // Initialize sound effects
            pointSound = MediaPlayer.create(getApplicationContext(), R.raw.point);
            fireSound = MediaPlayer.create(getApplicationContext(), R.raw.fire);
            hitSound = MediaPlayer.create(getApplicationContext(), R.raw.hit);

            // Initialize background music
            backgroundMusic = MediaPlayer.create(getApplicationContext(), R.raw.background_music);

            // Start background music
            backgroundMusic.start();
        }

    }

    /**
     * Makes a new Projectile appear at the coordinates and angle of the specified Player.
     * @param player Player to appear next to.
     */
    private void spawnProjectile(Player player) {

        if (player.canShoot()) {

            float angle = (float) Math.toRadians(player.getRotation() - 90); // Radians

            float pW = player.getWidth();
            float pH = player.getHeight();
            float oX = player.getX() + pW * Math.max(0.5f, (float) Math.cos(angle));
            float oY = player.getY() + pH * Math.max(0.5f, (float) Math.sin(angle));
            int baseDamage = 20;
            boolean firedByLocal = player.isLocal();

            if (isEffectActive(Card.Effect.ATTACK_UP)) {
                baseDamage *= 2;
            }

            Projectile projectile = new Projectile(AIGameActivity.this, firedByLocal, angle, oX, oY, baseDamage);
            AIGameActivity.this.activeProjectiles.add(projectile);
            AIGameActivity.this.activeColliders.add(projectile);

            layout.addView(projectile);

            if (firedByLocal){
                activateEventFlag(Event.LOCAL_PLAYER_FIRED);
                shotsFiredStats++;
                player.restoreFireCooldown();
            }

            if (isEffectActive(Card.Effect.TRIPLE_SHOT)) {

                Projectile projectileL = new Projectile(AIGameActivity.this, firedByLocal, angle - .3f, oX, oY, baseDamage);
                AIGameActivity.this.activeProjectiles.add(projectileL);
                AIGameActivity.this.activeColliders.add(projectileL);

                Projectile projectileR = new Projectile(AIGameActivity.this, firedByLocal, angle + .3f, oX, oY, baseDamage);
                AIGameActivity.this.activeProjectiles.add(projectileR);
                AIGameActivity.this.activeColliders.add(projectileR);

                layout.addView(projectileL);
                layout.addView(projectileR);

                if (player == localPlayer) shotsFiredStats += 2;
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundActive) backgroundMusic.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundActive) backgroundMusic.start();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(AIGameActivity.this)
                .setTitle(getString(R.string.exitGameDialogTitle))
                .setMessage(getString(R.string.exitGameWarning))
                .setNegativeButton(getString(R.string.exitGameNegativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setPositiveButton(getString(R.string.exitGamePositiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activateEventFlag(Event.LOCAL_PLAYER_LEFT);
                        gameFinished = true;
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

        Trace trace = new Trace(AIGameActivity.this, oX, oY, angle);
        activeTraces.add(trace);
        layout.addView(trace);

        if (activeTraces.size() > 20) {
            layout.removeView(activeTraces.get(0));
            activeTraces.remove(0);
        }
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

    public static void deathPromptVisible(boolean visible) {

        if (visible){

            deathPromptLayout.bringToFront();
            deathPromptLayout.setAlpha(1);

            AnimationSet animSet = new AnimationSet(true);

            Animation scaleAnim = new ScaleAnimation(
                    1.5f, 1f, // Start and end values for the X axis scaling
                    1.5f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            scaleAnim.setDuration(200);

            Animation alphaAnim = new AlphaAnimation(0, 1);
            alphaAnim.setDuration(200);

            animSet.addAnimation(scaleAnim);
            animSet.addAnimation(alphaAnim);
            animSet.setFillAfter(true);

            deathPromptLayout.startAnimation(animSet);

            //deathPromptLayout.animate().alpha(1).setDuration(100);
        }else{

            AnimationSet animSet = new AnimationSet(true);

            Animation scaleAnim = new ScaleAnimation(
                    1f, 0.5f, // Start and end values for the X axis scaling
                    1f, 0.5f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            scaleAnim.setDuration(200);
            scaleAnim.setFillAfter(false);

            Animation alphaAnim = new AlphaAnimation(1, 0);
            alphaAnim.setDuration(200);
            alphaAnim.setFillAfter(true);

            animSet.addAnimation(scaleAnim);
            animSet.addAnimation(alphaAnim);
            animSet.setFillAfter(true);

            deathPromptLayout.startAnimation(animSet);

        }

    }

    private void checkProjectileCollisions() {
        Iterator<Projectile> projectileIterator = activeProjectiles.iterator();

        while (projectileIterator.hasNext()) {

            Projectile p = projectileIterator.next();
            Player playerToCheck;

            if (p.isFiredByLocal()) playerToCheck = remotePlayer;
            else playerToCheck = localPlayer;

            if (p.isColliding(playerToCheck)) {

                playerToCheck.boatImageView.setColorFilter(getResources().getColor(R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                playerToCheck.damage(p.getDamage());
                showPlayerPopup(playerToCheck, "-" + p.getDamage() + " ♡", 300, true);

                layout.removeView(p);
                projectileIterator.remove();

                if (soundActive) {
                    if (hitSound.isPlaying()) hitSound.stop();
                    hitSound.start();
                }

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

            // Do nothing - preference is for whoever came in first.

        } else if (localPlayerColliding) {

            islandDomain.setInvadedBy(GameActivity.Invader.LOCAL_PLAYER);
            localPlayerInside = true;
            remotePlayerInside = false;

        } else if (remotePlayerColliding) {

            islandDomain.setInvadedBy(GameActivity.Invader.REMOTE_PLAYER);
            remotePlayerInside = true;
            localPlayerInside = false;

        } else {

            // No one is colliding with islandDomain.
            islandDomain.setInvadedBy(GameActivity.Invader.NONE);

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

    private void finishGame(FinishCondition condition) {

        if (!gameFinished) {

            gameFinished = true;
            Intent i = new Intent(AIGameActivity.this, GameEndActivity.class);

            if ((localScore > remoteScore) || condition == FinishCondition.REMOTE_PLAYER_LEFT) {
                currentGameState = GameState.LOCAL_WON;
            } else if (localScore < remoteScore) {
                currentGameState = GameState.REMOTE_WON;
            } else {
                currentGameState = GameState.DRAW;
            }

            i.putExtra("finishCondition", condition);
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
                        if (soundActive) pointSound.start();
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

        if (remotePlayerInside && !localPlayerInside){

            if (--framesUntilTickRemote == 0) {

                if (remoteScore == 99) {

                    // This comprovation is meant in case the remote game fails to send
                    // the remote score to the server in time before closing the activity.

                    remoteScore++;
                    framesUntilTickLocal = MAX_FRAMES_UNTIL_TICK;

                    finishGame(FinishCondition.REMOTE_MAX_SCORE);

                }

            }

        }

        // SCORE COUNTERS
        textViewCounterLocal.setText(Integer.toString(localScore) + "%");
        textViewCounterRemote.setText(Integer.toString(remoteScore) + "%");

    }

    // Method that gets called every frame.
    private void refresh() {
        runOnUiThread(new Runnable() {
            public void run() {

                remotePlayer.bringToFront();
                localPlayer.bringToFront();

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
                float localPlayerDpX = Graphics.toDp(AIGameActivity.this, localPlayer.getX());
                float localPlayerDpY = Graphics.toDp(AIGameActivity.this, localPlayer.getY());
                localPosition.set((int) localPlayerDpX * 1000, (int) localPlayerDpY * 1000);
                localAngle = (float) Math.toDegrees(localPlayer.getAngle());

                // Move remote player
                moveRemotePlayer();

                //test CardZone
                improveVisibilityCardZone(180, 140, 90);

                // Process remote events
                if (remoteActiveEvent != null) handleRemoteEvent();

                // Check game finish conditions
                checkGameFinish();

            }
        });
    }

    private void moveRemotePlayer() {

        remoteCurve.set(remotePlayer.getPosition(), remotePosition, remotePlayer.getRotation(), remoteAngle);

        // If remotePlayer is in a range of 10px from the destination, consider it reached
        boolean reached = (Math.abs(remotePosition.x - remotePlayer.getPosition().x) < 10
                && Math.abs(remotePosition.y - remotePlayer.getPosition().y) < 10);

        if (!reached){
            remotePlayer.restoreMovement();
            remotePlayer.setMoving(true);
        }else{
            remotePlayer.setMoving(false);
            remotePlayer.restoreMovement();
        }

        //remoteCurve.set(remotePlayer.getPosition(), remotePosition, (float) Math.toDegrees(remotePlayer.getRotation()), remoteAngle);
        if(remotePlayer.isMoving()) remotePlayer.moveInCurve(remoteCurve);

    }

    private void checkGameFinish() {

        if (secondsLeft <= 0) finishGame(FinishCondition.TIME_OUT); // Time ran out
        else if (localScore >= 100){
            activateEventFlag(Event.LOCAL_PLAYER_WON);
            finishGame(FinishCondition.LOCAL_MAX_SCORE); // localPlayer reached 100%
        }
        else if (remoteScore >= 100){
            finishGame(FinishCondition.REMOTE_MAX_SCORE); // remotePlayer reached 100%
        }

    }

    /**
     * If localPlayer is dead, check if localPlayer should respawn on this frame or decrease
     * the counter until it respawns.
     */
    private void checkLocalPlayerRespawn() {

        if (!localPlayer.isAlive()){

            System.out.println("localPlayer is dead");

            deathPromptLayout.bringToFront();

            int timeToRespawn = localPlayer.getTimeToRespawn();
            respawnTimerTextView.setText(Integer.toString(timeToRespawn / fps + 1));

            // Calculate frames left for player respawn.
            if (timeToRespawn == 0){

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
                    //localPlayer.damage(remotePlayer.getDamage(isEffectActive(ATTACK_UP)));
                    localPlayer.damage(20);
                    break;

                case LOCAL_PLAYER_RESPAWNED:
                    // remotePlayer respawned
                    remotePlayer.respawn();
                    remotePlayer.setMaxVelocity();
                    break;

                case LOCAL_PLAYER_FIRED:
                    // remotePlayer fired
                    spawnProjectile(remotePlayer);
                    break;

                case LOCAL_PLAYER_WON:
                    // remotePlayer won the game
                    remoteScore = 100;
                    finishGame(FinishCondition.REMOTE_MAX_SCORE);
                    break;

                case LOCAL_PLAYER_USED_SPEED_UP:
                    // Does not apply.
                    break;

                case LOCAL_PLAYER_USED_ATTACK_UP:
                    // TODO
                    break;

                case LOCAL_PLAYER_USED_FULL_RESTORATION:
                    // Does not apply
                    break;

                case LOCAL_PLAYER_USED_STUNNED:
                    showPlayerPopup(localPlayer, Card.Effect.STUNNED.getName(), 1000, false);
                    addEffect(STUNNED);
                    break;

                case LOCAL_PLAYER_USED_REVERSED_HAND:
                    showPlayerPopup(localPlayer, Card.Effect.REVERSED_HAND.getName(), 1000, false);
                    addEffect(REVERSED_HAND);
                    break;

                case LOCAL_PLAYER_USED_DISCARD_ONE:
                    showPlayerPopup(localPlayer, Card.Effect.DISCARD_ONE.getName(), 1000, false);
                    addEffect(DISCARD_ONE);
                    break;

                case LOCAL_PLAYER_USED_REVERSED_CONTROLS:
                    showPlayerPopup(localPlayer, Card.Effect.REVERSED_CONTROLS.getName(), 1000, false);
                    addEffect(REVERSED_CONTROLS);
                    break;

                case LOCAL_PLAYER_USED_TRIPLE_SHOT:
                    // TODO
                    break;

                case LOCAL_PLAYER_USED_DISPEL:
                    // Does not apply
                    break;

                case LOCAL_PLAYER_USED_KO:
                    showPlayerPopup(localPlayer, Card.Effect.KO.getName(), 1000, false);
                    addEffect(KO);
                    break;

                case LOCAL_PLAYER_USED_QUICK_REVIVE:
                    // Does not apply
                    break;

                case LOCAL_PLAYER_USED_RANDOM_WARP:
                    showPlayerPopup(localPlayer, Card.Effect.RANDOM_WARP.getName(), 1000, false);
                    addEffect(RANDOM_WARP);
                    break;

                case LOCAL_PLAYER_LEFT:
                    finishGame(FinishCondition.REMOTE_PLAYER_LEFT);

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

        EventIndexPair eventIndexPair = new EventIndexPair(localActiveEvent, localEventIndex);
        localUnsentEvents.add(eventIndexPair);

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

                    switch(effect){

                        case SPEED_UP:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_SPEED_UP);
                            break;

                        case ATTACK_UP:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_ATTACK_UP);
                            break;

                        case FULL_RESTORATION:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_FULL_RESTORATION);
                            break;

                        case STUNNED:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_STUNNED);
                            break;

                        case REVERSED_HAND:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_REVERSED_HAND);
                            break;

                        case DISCARD_ONE:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_DISCARD_ONE);
                            break;

                        case REVERSED_CONTROLS:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_REVERSED_CONTROLS);
                            break;

                        case TRIPLE_SHOT:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_TRIPLE_SHOT);
                            break;

                        case DISPEL:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_DISPEL);
                            break;

                        case KO:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_KO);
                            break;

                        case QUICK_REVIVE:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_QUICK_REVIVE);
                            break;

                        case RANDOM_WARP:
                            activateEventFlag(Event.LOCAL_PLAYER_USED_RANDOM_WARP);
                            break;

                        case NONE:
                        default:
                            break;

                    }

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
            remotePlayer.die(isEffectActive(QUICK_REVIVE));
            activateEventFlag(Event.REMOTE_PLAYER_DIED);
            killsStats++;
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
            if (localPlayerReady && remotePlayerReady && running){

                // Start the game loop only once both players have posted their
                // ready signals to the server and these signals have been retrieved.

                refresh();
                layout.postInvalidate();
            }
        }
    }

    // Asynchronous task that handles the AI
    public class AITask extends AsyncTask<String, String, Void> {

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
                        retrieveScoreData();

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

        private void retrieveScoreData() {

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

            if(!localUnsentEvents.isEmpty()){

                EventIndexPair eventIndexPair = localUnsentEvents.get(0);

                Event event = eventIndexPair.event;
                int eventIndex = eventIndexPair.eventIndex;
                int eventNumber = event.ordinal();

                System.out.println("EVENT: " + event);
                System.out.println("INDEX: " + eventIndex);

                getJSON("https://pis04-ub.herokuapp.com/send_local_event.php?matchId=" + matchId
                                + "&player=" + assignedPlayer
                                + "&event=" + eventNumber
                                + "&eventIndex=" + eventIndex,
                        2000);

                lastSentLocalEventIndex = eventIndex;
                localUnsentEvents.remove(0);

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

            // Local coordinates get sent as DP and multiplied by 1000 to keep decimal data.

            getJSON("https://pis04-ub.herokuapp.com/send_local_position.php?matchId=" + matchId
                    + "&player=" + assignedPlayer
                    + "&x=" + localPosition.x
                    + "&y=" + localPosition.y
                    + "&angle=" + localAngle,
                    2000);

            System.out.println(localAngle);

        }

        private void retrieveRemotePositionData() {

            // Remote coordinates are received as DP and must be converted to px.
            // They're also received multiplied by 1000 to keep decimal data, so they
            // must be divided by 1000.

            //This returns a JSON object with a {"x": int,"y": int} pattern.
            String data = getJSON("https://pis04-ub.herokuapp.com/retrieve_remote_position.php?matchId=" + matchId
                    + "&player=" + oppositePlayer, 2000);

            System.out.println(currentFrame + ": " + data);

            // Parse the JSON information into a PointAnglePair object.
            PointAnglePair p = new Gson().fromJson(data, PointAnglePair.class);

            // Set X and Y coordinates retrieved from JSON to the remotePosition.x and remotePosition.y global
            // variables. These variables will be used to position remotePlayer on the next frame.
            if (p != null) {

                remotePosition.set((int) Graphics.toPixels(AIGameActivity.this, p.x / 1000), (int) Graphics.toPixels(AIGameActivity.this, p.y / 1000));
                remoteAngle = p.angle;

                lastCheckSuccessful = true;
                latency = (currentFrame - lastFrameChecked) / 30;

            } else {
                lastCheckSuccessful = false;
            }

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

        public EventIndexPair(Event e, int i){
            this.event = e;
            this.eventIndex = i;
        }

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
