package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

/**
 * Created by lluismontabes on 6/3/17.
 *
 * Classe general per a definir un jugador.
 *
 */

public class Player extends Collider{

    private float velocity, idleVelocity;
    private float rotationSpeed;

    private int health;

    private float x;
    private float y;
    private float angle;

    private int delay;

    private CardZone cardZone;
    private int[] cardEffects;

    private ImageView boatImageView;
    private ImageView shadowImageView;

    public Player(Context context, AttributeSet attrs) {
        super(context, 50);

        LayoutInflater.from(context).inflate(R.layout.player, this);

        this.boatImageView = (ImageView) findViewById(R.id.boatImageView);
        this.shadowImageView = (ImageView) findViewById(R.id.shadowImageView);
        shadowImageView.setColorFilter(getResources().getColor(R.color.shadow), android.graphics.PorterDuff.Mode.MULTIPLY);

        this.velocity = 10;      // 10 pixels per frame
        this.rotationSpeed = 10; // 10 degrees per frame
        this.health = 100;       // 100 units of health
        delay = 0;               // 0 frames of fire delay
        cardEffects = new int[Card.TOTAL_CARD_NUMBER];
    }

    // SETTERS
    public void setAngle(float a) {
        this.angle = a;
    }
    public void setVelocity(float v){
        this.velocity = v;
    }
    public void setHealth(int h) { this.health = h; };

    //cardZone test
    public void setCardZone(CardZone cardZone) { this.cardZone = cardZone; }

    // GETTERS
    public float getAngle() {
        return angle;
    }
    public float getVelocity(){
        return this.velocity;
    }
    public int getHealth() { return this.health; };

    //cardZone test
    public CardZone getCardZone() { return this.cardZone; }

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

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public void decreaseDelay() { delay--; };

    /**
     * Moves Player in the specified angle.
     * @param angle Angle in which to move the Player.
     * @param intensity Multiplier (from 0.0 to 1.0) of the velocity.
     */
    public void move(float angle, float intensity){

        if (!isEffectActive(Card.Effect.STUNNED)) {

            x = this.getX();
            y = this.getY();

            float scaleX = (float) Math.cos(angle);
            float scaleY = (float) Math.sin(angle);

            float modifier = 1;
            if (isEffectActive(Card.Effect.SPEED_UP)) {
                modifier *= 2;
            }

            float velocityX = scaleX * this.velocity * intensity * modifier;
            float velocityY = scaleY * this.velocity * intensity * modifier;

            this.setX(x + velocityX);
            this.setY(y + velocityY);
            this.setRotation((float) Math.toDegrees(angle) + 90);

            // Move shadow with player
            System.out.println(100 * (float) Math.sin(angle));
            System.out.println(100 * (float) Math.cos(angle));
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

    // USER INTERACTION
    public void damage(int d){
        if (isEffectActive(Card.Effect.DEFENSE_UP)) {
            d /= 2;
        }
        this.health -= d;
    }

    public boolean canShoot() {
        return delay == 0;
    }

    public void die(){
        System.out.println("Player died");
        this.setAlpha(0);
        removeAllEffects();
    }

    public int handSize() {
        return cardZone.getCardList().size();
    }

    public void addEffect(int effect, int duration) {
        cardEffects[effect] = duration;
    }

    public void removeEffect(int effect) {
        cardEffects[effect] = 0;
    }

    public void removeAllEffects() {
        for (int pos = 0; pos < cardEffects.length; pos++) {
            cardEffects[pos] = 0;
        }
    }

    public boolean isEffectActive(int effect) {
        return cardEffects[effect] != 0;
    }

    public void handleEffects() {
        cardZone.reverseCards(isEffectActive(Card.Effect.REVERSED_HAND));
    }

    public void decreaseEffectsDuration() {
        for (int pos = 0; pos < cardEffects.length; pos++) {
            if (cardEffects[pos] > 0) cardEffects[pos]--;
        }
    }

    public void improveVisibilityCardZone(float maxDistance,float minDistance,int minAlpha){
        this.cardZone.improveVisibility(this,maxDistance,minDistance,minAlpha);
    }

}
