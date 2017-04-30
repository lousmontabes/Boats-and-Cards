package com.example.lluismontabes.gameofboatsandcards.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.example.lluismontabes.gameofboatsandcards.Model.Collider;
import com.example.lluismontabes.gameofboatsandcards.Model.RectangularCollider;
import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by lluismontabes on 6/3/17.
 *
 * Classe general per a definir un jugador.
 *
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
    private float velocity, idleVelocity;
    private float rotationSpeed;

    // Position
    private float x;
    private float y;
    private float angle;

    /**
     * HEALTH & GAMEPLAY PARAMETERS
     */
    // Health
    private final int maxHealth;
    private int health;

    // Cooldowns
    private final int maxFireCooldown;
    private int fireCooldown;

    // Effects
    private boolean alive;
    private boolean stunned;

    public Player(Context context, AttributeSet attrs) {
        super(context, 34, 59, 40, 45);

        LayoutInflater.from(context).inflate(R.layout.player, this);

        boatImageView = (ImageView) findViewById(R.id.boatImageView);
        shadowImageView = (ImageView) findViewById(R.id.shadowImageView);
        shadowImageView.setColorFilter(getResources().getColor(R.color.shadow), android.graphics.PorterDuff.Mode.MULTIPLY);

        velocity = 10;              // 10 pixels per frame
        rotationSpeed = 10;         // 10 degrees per frame
        
        maxHealth = 100;            // 100 units of max health
        health = 100;               // 100 units of current health
        
        maxFireCooldown = 10;       // 10 frames of max fire cooldown
        fireCooldown = 0;    // 0 frames of current fire cooldown
        
        alive = true;

    }

    // SETTERS
    public void setAngle(float a) {
        this.angle = a;
    }
    public void setVelocity(float v){
        this.velocity = v;
    }
    public void setHealth(int h){
        this.health = h;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    // GETTERS
    public float getAngle() {
        return angle;
    }
    public float getVelocity(){
        return this.velocity;
    }
    public int getHealth() {
        return this.health;
    }
    public boolean isAlive() {
        return this.alive;
    }

    // MOVEMENT METHODS
    public void moveUp(){
        y = this.getY();
        this.setY(y - velocity);
    }

    public void moveDown(){
        y = this.getY();
        this.setY(y + velocity);
    }

    public void moveLeft(){
        x = this.getX();
        this.setX(x - velocity);
    }

    public void moveRight(){
        x = this.getX();
        this.setX(x + velocity);
    }

    /**
     * Moves Player in the specified angle.
     * @param angle Angle in which to move the Player.
     * @param intensity Multiplier (from 0.0 to 1.0) of the velocity.
     */
    public void move(float angle, float intensity){

        if (!stunned && alive) {

            x = this.getX();
            y = this.getY();

            float scaleX = (float) Math.cos(angle);
            float scaleY = (float) Math.sin(angle);

            float modifier = 1;
            /*
            if (isEffectActive(SPEED_UP)) {
                modifier *= 2;
            }
            if (isEffectActive(REVERSED_CONTROLS)) {
                modifier = -modifier;
            }*/

            float velocityX = scaleX * this.velocity * intensity * modifier;
            float velocityY = scaleY * this.velocity * intensity * modifier;

            this.setX(x + velocityX);
            this.setY(y + velocityY);
            this.setRotation((float) Math.toDegrees(angle) + 90);

            // Move shadow with player
            shadowImageView.setY(-45 * (float) Math.sin(angle));
            shadowImageView.setX(45 * (float) Math.cos(angle));
        }
    }

    /**
     * Moves Player towards the specified coordinates.
     * @param destX X coordinate of the destination.
     * @param destY Y coordinate of the destination.
     */
    public void moveTo(float destX, float destY){

        x = this.getX();
        y = this.getY();

        float pathX = destX - x;
        float pathY = destY - y;

        angle = (float) Math.atan2(pathY, pathX);

        float scaleX = (float) Math.cos(angle);
        float scaleY = (float) Math.sin(angle);

        float velocityX = scaleX * this.velocity;
        float velocityY = scaleY * this.velocity;

        this.setX(x + velocityX);
        this.setY(y + velocityY);

        float convertedAngle = (float) Math.toDegrees(angle) + 90;

        if (Math.abs(pathX) > 10 || Math.abs(pathY) > 10) this.setRotation(convertedAngle);

    }

    public void rotateTo(float a){

        angle = this.getRotation();

        if (angle != a){
            if (Math.abs(angle - a) < rotationSpeed) this.setRotation(angle);
            else this.setRotation(angle + rotationSpeed);
        }

    }

    public void respawn() {
        if (fireCooldown <= 0) {
            setAlpha(1);
            alive = true;
            setAngle(0);
        }
    }

    // HEALTH

    public void restoreHealth(){
        health = maxHealth;
    }

    public void damage(int d){
        /*
        if (isEffectActive(DEFENSE_UP)) {
            d /= 2;
        }
        */
        this.health -= d;
        if (this.health <= 0) {
            die();
        }
    }

    public void die(){

        System.out.println("Player died");

        alive = false;
        this.setAlpha(0);

        /*if (isEffectActive(QUICK_REVIVE)) {
            delay = 30;
        } else {
            delay = 150;
        }*/

        //removeAllEffects();

    }

    // COOLDOWNS

    public void restoreFireCooldown(){
        fireCooldown = maxFireCooldown;
    }

    public void decreaseFireCooldown() {
        if (fireCooldown > 0) {
            fireCooldown--;
        }
    }

    public boolean canShoot() {
        return fireCooldown == 0 && !stunned;
    }

    @Override
    public boolean isColliding(Collider c) {
        return (alive && super.isColliding(c));
    }

}
