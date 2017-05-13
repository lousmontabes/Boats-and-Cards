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

import com.example.lluismontabes.gameofboatsandcards.Model.RoundCollider;
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
    int connectionFrequency = 6;
    int lastFrameChecked = 0;
    float latency;
    boolean lastCheckSuccessful = false;

    // Online player positioning
    Point remotePosition = new Point(0, 0);
    Point localPosition = new Point(0, 0);

    // Online player and match IDs
    int matchId;
    int assignedPlayer;
    int oppositePlayer;

    // Online invasion and winning statuses
    public enum Invader {
        NONE, LOCAL_PLAYER, REMOTE_PLAYER
    }


    public enum GameState {UNFINISHED, LOCAL_WON, REMOTE_WON, DRAW}

    static GameState gs = GameState.UNFINISHED;
    public static GameState getGameState() {
        return gs;
    }

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
    TextView textViewCounterLocal;
    TextView textViewCounterRemote;

    // Images
    ImageView treasureImageView;

    /**
     * STATISTICS
     **/
    // Scores
    int localScore = 0;
    int remoteScore = 0;

    // Time
    int currentFrame = 0;
    int seconds = 0;
    int secondsLeft = 120;
    boolean gameFinished;

    // Counters
    byte framesUntilTickLocal = fps / 2;
    byte framesUntilTickRemote = fps / 2;

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
    int cardVisivilityTimer;
    boolean cardHasSpawned = false;
    private static final int MAX_CARD_COOLDOWN = 500;
    private static final int MIN_CARD_COOLDOWN = 100;
    private static final int CARD_VISIBLE_TIME = 300;

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
    private int[] cardEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullscreen();
        setContentView(R.layout.activity_game);

        running = true;
        gameFinished = false;

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

    }

    private void startRemoteTask() {

        remoteTask = new RemoteDataTask();
        remoteTask.execute();

    }

    private void initializeCardEffects() {
        cardEffects = new int[Card.TOTAL_CARD_NUMBER * 2];
    }

    private void initializeListeners() {

        // Layout onTouchListener
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cardUsed == 0) localPlayerShoot();
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

        cardSpawn = new CardSpawn(this);
        cardSpawn.setLayoutParams(new ViewGroup.LayoutParams((int) Graphics.toPixels(this, 15),
                (int) Graphics.toPixels(this, 20)));
        cardSpawnCooldown = (int) (Math.random() * (MAX_CARD_COOLDOWN - MIN_CARD_COOLDOWN)) + MIN_CARD_COOLDOWN;
        cardVisivilityTimer = 0;
        layout.addView(cardSpawn);

    }

    private void initializeIslandDomain(float radius) {

        islandDomain = new IslandDomain(this, radius);
        layout.addView(islandDomain);

        treasureImageView.bringToFront();

    }

    private void initializePlayers() {

        localPlayer = new Player(GameActivity.this, null);
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

    private void localPlayerShoot() {

        if (localPlayer.canShoot()) {
            float pW = localPlayer.getWidth();
            float pH = localPlayer.getHeight();
            float oX = localPlayer.getX() + pW / 2;
            float oY = localPlayer.getY() + pH / 2;
            int baseDamage = 20;

            if (isEffectActive(Card.Effect.ATTACK_UP)) {
                baseDamage *= 2;
            }

            Projectile projectile = new Projectile(GameActivity.this, joystick.getCurrentAngle(), oX, oY, baseDamage);
            GameActivity.this.activeProjectiles.add(projectile);
            GameActivity.this.activeColliders.add(projectile);

            layout.addView(projectile);

            /*if (fireSound.isPlaying()) fireSound.stop();
            fireSound.start();*/

            if (isEffectActive(Card.Effect.TRIPLE_SHOT)) {

                Projectile projectileL = new Projectile(GameActivity.this, joystick.getCurrentAngle() - .3f, oX, oY, baseDamage);
                GameActivity.this.activeProjectiles.add(projectileL);
                GameActivity.this.activeColliders.add(projectileL);

                Projectile projectileR = new Projectile(GameActivity.this, joystick.getCurrentAngle() + .3f, oX, oY, baseDamage);
                GameActivity.this.activeProjectiles.add(projectileR);
                GameActivity.this.activeColliders.add(projectileR);

                layout.addView(projectileL);
                layout.addView(projectileR);
            }

            localPlayer.restoreFireCooldown();
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
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to forfeit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GameActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
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

            if (p.isColliding(remotePlayer)) {

                remotePlayer.boatImageView.setColorFilter(getResources().getColor(R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                remotePlayer.damage(p.getDamage());
                showPlayerPopup(remotePlayer, "-" + p.getDamage() + " ♡", 300, true);

                layout.removeView(p);
                projectileIterator.remove();

                if (hitSound.isPlaying()) hitSound.stop();
                hitSound.start();

            } else remotePlayer.boatImageView.setColorFilter(null);

        }
    }

    /**
     * Check if any player is colliding with the IslandDomain.
     */
    private void checkIslandDomainCollisions() {

        // TODO: Make this method less spaghetti-y

        boolean localPlayerColliding = localPlayer.isColliding(islandDomain);
        boolean remotePlayerColliding = remotePlayer.isColliding(islandDomain);

        if (localPlayerColliding && remotePlayerColliding) {

            // Do nothing - preference is for the player who invaded first.

        } else if (localPlayerColliding) {

            // localPlayer is colliding with islandDomain.
            if (!localPlayerInside) {

                // localPlayer was not inside before this collision.
                islandDomain.setInvadedBy(Invader.LOCAL_PLAYER);
                localPlayerInside = true;
            }

        } else if (remotePlayerColliding) {

            // remotePlayer is colliding with islandDomain.
            if (!remotePlayerInside) {

                // remotePlayer was not inside before this collision.
                islandDomain.setInvadedBy(Invader.REMOTE_PLAYER);
                remotePlayerInside = true;
            }

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
        if (activePopups.size() > 4) activePopups.remove(0);

    }

    private void finishGame() {
        if (!gameFinished) {
            gameFinished = true;
            Intent i = new Intent(GameActivity.this, GameEndActivity.class);

            if (localScore > remoteScore) {
                gs = GameState.LOCAL_WON;
            } else if (localScore < remoteScore) {
                gs = GameState.REMOTE_WON;
            } else {
                gs = GameState.DRAW;
            }

            startActivity(i);
            finish();
        }
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

        if (localPlayerInside) {

            if (--framesUntilTickLocal == 0) {

                if (localScore < 100) {

                    localScore++;
                    framesUntilTickLocal = fps / 2;
                    textViewCounterLocal.setText(Integer.toString(localScore) + "%");

                    showPlayerPopup(localPlayer, "+1", 250, true);

                    if (running) {
                        pointSound.start();
                    }

                } else {
                    // localPlayer reached 100% score.
                    finishGame();
                }

            }

            textViewCounterLocal.setTextColor(getResources().getColor(R.color.localCounterActive));

        } else {
            framesUntilTickLocal = fps / 2;
            textViewCounterLocal.setTextColor(getResources().getColor(R.color.counterStopped));
        }

        if (remotePlayerInside) {

            if (--framesUntilTickRemote == 0) {

                if (remoteScore < 100) {

                    remoteScore++;
                    framesUntilTickRemote = fps / 2;
                    textViewCounterRemote.setText(Integer.toString(remoteScore) + "%");

                    showPlayerPopup(remotePlayer, "+1", 250, true);

                    if (running) {
                        pointSound.start();
                    }

                } else {
                    // remotePlayer reached 100% score.
                    finishGame();
                }

            }

            textViewCounterRemote.setTextColor(getResources().getColor(R.color.remoteCounterActive));

        } else {
            framesUntilTickRemote = fps / 2;
            textViewCounterRemote.setTextColor(getResources().getColor(R.color.counterStopped));
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

                // Control buttons (currently unused)
                if (upPressed) localPlayer.moveUp();
                if (leftPressed) localPlayer.moveLeft();
                if (rightPressed) localPlayer.moveRight();
                if (downPressed) localPlayer.moveDown();

                // Starting position
                if (currentFrame <= 10) setStartPositions();

                    // Joystick controls
                    // IMPORTANT: Block joystick on first frame to avoid disappearing player bug.
                else if (localPlayer.isAlive()) {

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
                    localPlayer.respawn();
                    joystick.resetCurrentAngle();
                }

                // Player 2 controls
                //retrieveRemoteAction();

                // Projectile movement
                for (Projectile p : activeProjectiles) p.move();

                // Boat trace
                showBoatTrace();

                // Collisions
                checkProjectileCollisions();

                // Death check
                checkPlayerHealth();

                // Decrease cooldown to shoot again
                localPlayer.decreaseFireCooldown();

                // islandDomain collisions
                checkIslandDomainCollisions();

                // Card collecting
                //if (secondsLeft % 6 == 5 && currentFrame % fps == 0) drawCard(localPlayer);

                // Card usage
                if (cardUsed != 0) useCard(localPlayer, cardUsed);

                //Card spawning
                spawnCard();
                checkCollisionCardSpawn();

                // Effect management
                handleEffects();
                decreaseEffectsDuration();

                // Scoreboard and timer counter
                advanceCounter();

                // Prepare local data to send to server
                localPosition.x = (int) localPlayer.getX();
                localPosition.y = (int) localPlayer.getY();

                // Apply remote data to remotePlayer
                //log(remotePosition.x + ", " + remotePosition.y);
                log("PNG: " + latency);
                remotePlayer.accelerate();
                remotePlayer.moveTo(remotePosition.x, remotePosition.y);

                //test CardZone
                improveVisibilityCardZone(180, 140, 90);

                // Environmental effects
                showDripplets();

            }
        });
    }

    /**
     * Check if a player has died.
     */
    private void checkPlayerHealth() {

        if (localPlayer.getHealth() <= 0) localPlayer.die(isEffectActive(QUICK_REVIVE));
        if (remotePlayer.getHealth() <= 0) remotePlayer.die(isEffectActive(QUICK_REVIVE));

    }

    /**
     * Set starting position of players. (Bottom for player 1, top for player 2)
     */
    private void setStartPositions() {

        int centerX = (layout.getWidth() - localPlayer.getWidth()) / 2;

        if (assignedPlayer == 1 || assignedPlayer == -1) {

            // Spawn local player at bottom position
            localPlayer.setX(centerX);
            localPlayer.setY(layout.getHeight() - localPlayer.getHeight());

            // Spawn remote player at top position
            remotePlayer.setX(centerX);
            remotePlayer.setY(0);

        } else if (assignedPlayer == 2) {

            // Spawn local player at bottom position
            remotePlayer.setX(centerX);
            remotePlayer.setY(layout.getHeight() - localPlayer.getHeight());

            // Spawn remote player at top position
            localPlayer.setX(centerX);
            localPlayer.setY(0);

        }

    }

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

    private void spawnCard() {
        if (!cardHasSpawned) {
            cardSpawned = new Card();
            ImageView im = (ImageView) cardSpawn.findViewById(R.id.cardSpawn);
            Drawable d = ContextCompat.getDrawable(this, cardSpawned.getResourceID());
            im.setImageDrawable(d);
            cardSpawn.setVisibility(View.VISIBLE);
            cardHasSpawned = true;
            caught = false;
            cardVisivilityTimer = 0;
            do {
                cardSpawn.setX((float) (Math.random() * (layout.getWidth() - cardSpawn.getWidth())));
                cardSpawn.setY((float) (Math.random() * (layout.getHeight() - cardSpawn.getHeight())));
            } while (islandDomain.isColliding(cardSpawn) || localPlayer.isColliding(cardSpawn) || remotePlayer.isColliding(cardSpawn));
        } else if (caught || cardVisivilityTimer > CARD_VISIBLE_TIME) {
            cardSpawnCooldown--;
            cardSpawn.setVisibility(View.GONE);
            if (cardSpawnCooldown == 0) {
                cardSpawnCooldown = (int) (Math.random() * (MAX_CARD_COOLDOWN - MIN_CARD_COOLDOWN)) + MIN_CARD_COOLDOWN;
                cardHasSpawned = false;
            }
            caught = true;
        } else {
            if (cardVisivilityTimer > CARD_VISIBLE_TIME - 48) {
                if (cardVisivilityTimer % 6 < 3) {
                    cardSpawn.setVisibility(View.INVISIBLE);
                } else {
                    cardSpawn.setVisibility(View.VISIBLE);
                }
            }
            cardVisivilityTimer++;
        }
    }

    private void keepInBounds(Player p) {
        p.setX(Math.min(Math.max(p.getX(), 0), layout.getWidth() - p.getWidth()));
        p.setY(Math.min(Math.max(p.getY(), 0), layout.getHeight() - p.getHeight()));
    }

    private void checkCollisionCardSpawn() {
        if (localPlayer.isColliding(cardSpawn) && !caught && cardZone.getCardList().size() < 3) {
            drawCard(localPlayer);
            caught = true;
        }
    }

    private void drawCard(Player player) {
        //TODO: use Deck class for card drawing
        // (int) (Math.random() * (MAX_CARD_COOLDOWN - MIN_CARD_COOLDOWN)) + MIN_CARD_COOLDOWN;
        cardZone.addCard(cardSpawned);
        log(Integer.toString(cardSpawned.getId()));

    }

    private void useCard(Player player, int n) {
        if (player.isAlive()) {
            Card usedCard = cardZone.removeCard(n);

            int effect = usedCard.getEffect();
            int duration = usedCard.getDuration();

            switch (usedCard.getTarget()) {

                case Card.Target.SELF:
                    addEffect(effect, duration);
                    break;

                case Card.Target.OPPONENT:
                    addEffect(effect, duration);
                    break;

                case Card.Target.ALL:
                    addEffect(effect, duration);
                    break;

                case Card.Target.TRAP:
                    //TODO: implemantar trampes
                    break;

            }

            showPlayerPopup(localPlayer, usedCard.getEffectName(), 1000, false);
        }
        cardUsed = 0;

    }

    private void handleEffects() {
        cardZone.reverseCards(isEffectActive(REVERSED_HAND));
        if (isEffectActive(DISCARD_ALL)) cardZone.discardAll();
        if (isEffectActive(KO)) {
            localPlayer.die(isEffectActive(QUICK_REVIVE));
            addEffect(QUICK_REVIVE, 0);
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

    private void addEffect(int effect, int duration) {
        cardEffects[effect] = duration;
    }

    private boolean isEffectActive(int e) {
        return cardEffects[e] != 0;
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

                if ((currentFrame % connectionFrequency == 0) && (lastFrameChecked != currentFrame)) {

                    lastFrameChecked = currentFrame;

                    /* SEND DATA */
                    getJSON("https://pis04-ub.herokuapp.com/send_local_action.php?matchId=" + matchId
                            + "&player=" + assignedPlayer
                            + "&x=" + localPosition.x
                            + "&y=" + localPosition.y, 2000);

                    /* RETRIEVE DATA */
                    //This returns a JSON object with a {"x": x,"y": y} pattern.
                    String data = getJSON("https://pis04-ub.herokuapp.com/retrieve_remote_action.php?matchId=" + matchId
                            + "&player=" + oppositePlayer, 2000);

                    System.out.println(currentFrame + ": " + data);

                    // Parse the JSON information into a Point object.
                    Point p = new Gson().fromJson(data, Point.class);

                    // Set X and Y coordinates retrieved from JSON to the remotePosition.x and remotePosition.y global
                    // variables. These variables will be used to position remotePlayer on the next frame.
                    if (p != null) {
                        remotePosition = p;
                        lastCheckSuccessful = true;
                        latency = (currentFrame - lastFrameChecked) / 30;
                    } else {
                        lastCheckSuccessful = false;
                    }

                }

            }
            return null;
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
}
