package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by lluismontabes on 6/3/17.
 *
 * Classe general per a definir un jugador.
 *
 */

public class Player extends ImageView{

    private float velocity, idleVelocity;
    private float x;
    private float y;

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    private float angle;

    public Player(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.velocity = 10;
    }


    // SETTERS
    public void setVelocity(float v){
        this.velocity = v;
    }

    // GETTERS
    public float getVelocity(){
        return this.velocity;
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
        this.setRotation((float) Math.toDegrees(angle) + 90);

    }

}
