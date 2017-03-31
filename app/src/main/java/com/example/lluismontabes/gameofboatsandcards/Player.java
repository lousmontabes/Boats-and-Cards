package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by lluismontabes on 6/3/17.
 *
 * Classe general per a definir un jugador.
 *
 */

public class Player extends Collider{

    private float velocity, idleVelocity;
    private int health;
    private float x;
    private float y;

    private float angle;

    //private CardZone cardZone; //Its own card zone, each player must to have one

    public Player(Context context, AttributeSet attrs) {
        super(context, 50);
        this.velocity = 10;
        this.health = 100;
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
    //public void setCardZone(CardZone cardZone) { this.cardZone = cardZone; }

    // GETTERS
    public float getAngle() {
        return angle;
    }
    public float getVelocity(){
        return this.velocity;
    }
    public int getHealth() { return this.health; };

    //cardZone test
    //public CardZone getCardZone() { return this.cardZone; }

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

    public void move(float angle, float intensity){

        x = this.getX();
        y = this.getY();

        float scaleX = (float) Math.cos(angle);
        float scaleY = (float) Math.sin(angle);

        float velocityX = scaleX * this.velocity * intensity;
        float velocityY = scaleY * this.velocity * intensity;

        this.setX(x + velocityX);
        this.setY(y + velocityY);
        this.setRotation((float) Math.toDegrees(angle) + 90);

    }

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

    // USER INTERACTION
    public void damage(int d){
        this.health -= d;
    }

    public void die(){
        System.out.println("Player died");
        this.setAlpha(0);
    }

    /*public void improveVisibilityCardZone(float maxDistance,float minDistance,int minAlpha){
        this.cardZone.improveVisibility(this,maxDistance,minDistance,minAlpha);
    }*/

}
