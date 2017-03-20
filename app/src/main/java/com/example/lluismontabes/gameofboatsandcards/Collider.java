package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;

import android.view.View;

/**
 * Created by lluismontabes on 17/3/17.
 */

public abstract class Collider extends View {

    private boolean isRound;
    private float radius;
    private float x, y, w, h;
    private float centerX, centerY;

    /**
     * Round collider constructor
     * @param context View context
     * @param r Radius of the collider
     */
    public Collider(Context context, float r){
        super(context);
        this.isRound = true;
        this.radius = r;
        this.centerX = this.getX() + radius;
        this.centerY = this.getY() + radius;
    }

    /**
     * Rectangular collider constructor
     * @param context View context
     * @param x Left
     * @param y Top
     * @param w Width
     * @param h Height
     */
    public Collider(Context context, float x, float y, float w, float h){
        super(context);
        this.isRound = false;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.centerX = x + w / 2;
        this.centerY = y + h / 2;
    }

    public boolean isRound(){
        return this.isRound;
    }

    public float getRadius(){
        return this.radius;
    }

    public float getCenterX(){
        return this.centerX;
    }

    public float getCenterY(){
        return this.centerY;
    }

    public float getDistance(Collider collider){

        float distX = collider.getCenterX() - this.getCenterX();
        float distY = collider.getCenterY() - this.getCenterY();

        float dist = (float) Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

        return dist;

    }

    public boolean isColliding(Collider c){

        return (this.getDistance(c) <= this.getRadius());

    }

}
