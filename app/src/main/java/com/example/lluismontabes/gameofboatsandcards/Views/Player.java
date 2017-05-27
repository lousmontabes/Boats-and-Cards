package com.example.lluismontabes.gameofboatsandcards.Views;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.example.lluismontabes.gameofboatsandcards.Model.Collider;
import com.example.lluismontabes.gameofboatsandcards.Model.CubicBezierCurve;
import com.example.lluismontabes.gameofboatsandcards.Model.RectangularCollider;
import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by lluismontabes on 6/3/17.
 * <p>
 * Classe general per a definir un jugador.
 */

public class Player extends RectangularCollider {

    /**
     * VIEWS
     */
    public ImageView boatImageView;
    private ImageView shadowImageView;

    /**
     * MOVEMENT & POSITIONING
     */
    // Velocity
    private final float MAX_VELOCITY = 40;
    private final float MAX_ROTATION_SPEED = 45;
    private float velocity, idleVelocity;
    private float acceleration;
    private float rotationAcceleration;
    private float friction;
    private float rotationSpeed;
    private float currentMaxAngle;
    private float currentMinAngle;

    // Position
    private Point startPosition;
    private float x;
    private float y;
    private float angle;

    // Parameters for curved movement
    float p = 0;
    boolean moving = false;

    /**
     * HEALTH & GAMEPLAY PARAMETERS
     */
    // Health
    public static final int MAX_HEALTH = 100;
    private int health;

    // Cooldowns
    private final int MAX_FIRE_COOLDOWN = 10;       // 10 frames of max fire cooldown
    private int fireCooldown;

    private final int MAX_RESPAWN_TIME = 150;
    private final int QUICK_REPAWN_TIME = 30;
    private int timeToRespawn;

    // Effects
    private boolean alive;
    private boolean stunned;

    private boolean speedUp;
    private boolean backwards;

    public Player(Context context) {
        super(context, 34, 59, 40, 45);

        LayoutInflater.from(context).inflate(R.layout.player, this);

        boatImageView = (ImageView) findViewById(R.id.boatImageView);
        shadowImageView = (ImageView) findViewById(R.id.shadowImageView);
        shadowImageView.setColorFilter(getResources().getColor(R.color.shadow), android.graphics.PorterDuff.Mode.MULTIPLY);

        velocity = 0;               // 10 pixels per frame
        rotationSpeed = 0;          // 10 degrees per frame

        acceleration = 2;           // Velocity increments 2 pixels/frame per frame (2px / frame^2)
        rotationAcceleration = 1;
        friction = 1.5f;                   // friction further decelerates the player on deceleration.

        currentMaxAngle = rotationSpeed;
        currentMinAngle = -rotationSpeed;

        health = MAX_HEALTH;        // 100 units of current health

        fireCooldown = 0;           // 0 frames of current fire cooldown
        timeToRespawn = 0;

        stunned = false;
        speedUp = false;
        backwards = false;

        alive = true;

    }

    // SETTERS
    public void setStartPosition(Point p){
        this.startPosition = p;
    }

    public void setPosition(Point p){
        this.setX(p.x);
        this.setY(p.y);
    }

    public void setAngle(float a) {
        this.angle = a;
    }

    public void setVelocity(float v) {
        this.velocity = v;
    }

    public void setHealth(int h) {
        this.health = h;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setMaxVelocity(){
        this.velocity = MAX_VELOCITY;
    }

    public void setMoving(boolean m) { this.moving = m; }

    // GETTERS
    public Point getPosition(){
        return new Point((int) (this.getX()), (int) (this.getY()));
    }

    public float getAngle() {
        return angle;
    }

    public float getVelocity() {
        return this.velocity;
    }

    public float getAcceleration() {
        return this.acceleration;
    }

    public int getHealth() {
        return this.health;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean isMoving() { return this.moving; }

    public void accelerate() {

        float futureRotationSpeed = rotationSpeed + rotationAcceleration;
        if (futureRotationSpeed <= MAX_ROTATION_SPEED) this.rotationSpeed = futureRotationSpeed;

        float futureVelocity = velocity + acceleration;
        if (futureVelocity <= MAX_VELOCITY) this.velocity = futureVelocity;

    }

    public void decelerate() {

        float futureVelocity = velocity - friction;
        if (futureVelocity >= 0) this.velocity = futureVelocity;
        if (futureVelocity < friction) this.velocity = 0;
        restoreRotationSpeed();

    }

    public void restoreVelocity() {
        this.velocity = 0;
    }

    public void restoreRotationSpeed() {
        this.rotationSpeed = 0;
    }

    /**
     * Moves Player in the specified angle.
     *
     * @param a         Angle in which to move the Player.
     * @param intensity Multiplier (from 0.0 to 1.0) of the velocity.
     */
    public void move(float a, float intensity) {

        if (!stunned && alive) {

            this.angle = a;

            x = this.getX();
            y = this.getY();

            float scaleX = (float) Math.cos(a);
            float scaleY = (float) Math.sin(a);

            float modifier = 1;

            if (speedUp) {
                modifier *= 2;
            }
            if (backwards) {
                modifier = -modifier;
            }

            float velocityX = scaleX * this.velocity * intensity * modifier;
            float velocityY = scaleY * this.velocity * intensity * modifier;

            this.setX(x + velocityX);
            this.setY(y + velocityY);
            if (moving) this.setRotation((float) Math.toDegrees(a) + 90);

            // Move shadow with player
            shadowImageView.setY(-45 * (float) Math.sin(a));
            shadowImageView.setX(45 * (float) Math.cos(a));
        }
    }

    /**
     * Moves Player in the specified angle, showing a static angle.
     *
     * @param a         Angle in which to move the Player.
     * @param intensity Multiplier (from 0.0 to 1.0) of the velocity.
     */
    public void staticAngleMove(float a, float intensity) {

        if (!stunned && alive) {

            this.angle = a;

            x = this.getX();
            y = this.getY();

            float scaleX = (float) Math.cos(a);
            float scaleY = (float) Math.sin(a);

            float modifier = 1;

            if (speedUp) {
                modifier *= 2;
            }
            if (backwards) {
                modifier = -modifier;
            }

            float velocityX = scaleX * this.velocity * intensity * modifier;
            float velocityY = scaleY * this.velocity * intensity * modifier;

            this.setX(x + velocityX);
            this.setY(y + velocityY);

            // Move shadow with player
            shadowImageView.setY(-45 * (float) Math.sin(this.getRotation()));
            shadowImageView.setX(45 * (float) Math.cos(this.getRotation()));
        }
    }

    /**
     * Moves the Player towatds the specified point.
     * Does the same as calling moveTo(x, y).
     * @param p Destination point.
     */
    public void moveTo(Point p){
        this.moveTo(p.x, p.y);
    }

    /**
     * Moves Player towards the specified coordinates.
     *
     * @param destX X coordinate of the destination.
     * @param destY Y coordinate of the destination.
     */
    public void moveTo(float destX, float destY) {

        x = this.getX();
        y = this.getY();

        float pathX = destX - x;
        float pathY = destY - y;

        angle = (float) Math.atan2(pathY, pathX);

        if (Math.abs(pathX) > 0 || Math.abs(pathY) > 0){
            move(angle, 0.4f);
        }

    }

    /**
     * Moves the player following a specified CubicBezierCurve.
     * @param curve CubicBezierCurve to follow.
     */
    public void moveInCurve(CubicBezierCurve curve) {

        if (isMoving()){
            this.setRotation(curve.getAngleAt(p % 1));
            this.moveTo(curve.getPointAt(p % 1));
            p += 0.025f;
            if (p == 1) setMoving(false);
        }else{
            p = 0;
        }

    }

    public void toStartPosition(){
        this.setPosition(startPosition);
    }

    public void rotateTo(float a) {

        //angle = this.getRotation();

        float a2 = angle + Math.max(currentMinAngle, Math.min(a, currentMaxAngle));
        this.setRotation(a2);

        angle = a2;

        System.out.println(angle);

        currentMaxAngle = a2 + rotationSpeed;
        currentMinAngle = a2 - rotationSpeed;

    }

    public int getTimeToRespawn(){

        if ( timeToRespawn > 0)  timeToRespawn--;
        return  timeToRespawn;

    }

    public void respawn() {
        this.alive = true;
        health = MAX_HEALTH;
        velocity = 0;
        setAngle(0);
        setAlpha(1);
        toStartPosition();
    }

    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    public void setSpeedUp(boolean speedUp) {
        this.speedUp = speedUp;
    }

    public void setBackwards(boolean backwards) {
        this.backwards = backwards;
    }

    // HEALTH

    public void restoreHealth() {
        health = MAX_HEALTH;
    }

    public void damage(int d) {
        /*
        if (isEffectActive(DEFENSE_UP)) {
            d /= 2;
        }
        */
        this.health -= d;

    }

    public void die(boolean quickRespawn) {

        alive = false;
        this.setAlpha(0);

        if (quickRespawn) {
             timeToRespawn = QUICK_REPAWN_TIME;
        } else {
             timeToRespawn = MAX_RESPAWN_TIME;
        }

        //removeAllEffects();

    }

    // COOLDOWNS

    public void restoreFireCooldown() {
        fireCooldown = MAX_FIRE_COOLDOWN;
    }

    public void decreaseFireCooldown() {
        if (fireCooldown > 0) {
            fireCooldown--;
        }
    }

    public boolean canShoot() {
        return fireCooldown == 0 && !stunned && alive;
    }

    @Override
    public boolean isColliding(Collider c) {
        return (alive && super.isColliding(c));
    }
}
