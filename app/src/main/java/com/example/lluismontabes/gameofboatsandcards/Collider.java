package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by lluismontabes on 17/3/17.
 */

public abstract class Collider extends ImageView {

    private boolean isRound;
    private float radius;
    private float x, y, w, h;

    /**
     * Round collider constructor
     * @param context View context
     * @param r Radius of the collider
     */
    public Collider(Context context, float r){
        super(context);
        this.isRound = true;
        this.radius = r;
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
    }

    public boolean isRound(){
        return this.isRound;
    }

    public float getRadius(){
        return this.radius;
    }

    public float getCenterX(){
        return this.getX() + radius;
    }

    public float getCenterY(){
        return this.getY() + radius;
    }

    public float getDistance(Collider c){

        float distX = c.getCenterX() - this.getCenterX();
        float distY = c.getCenterY() - this.getCenterY();

        float dist = (float) Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

        return dist;

    }

    public boolean isColliding(Collider c){

        return (this.getDistance(c) <= c.getRadius());

    }

}
