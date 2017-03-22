package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;

import android.graphics.Point;
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
    public float getDistance(Collider c,String msg){

        System.out.println("Mi collider es island domain -----------------------"+(msg));
        System.out.println("this.getCenterX(): "+(this.getCenterX()));
        System.out.println("this.getCenterY(): "+(this.getCenterY()));
        IslandDomain islandDomain = (IslandDomain)c;
        Point p = islandDomain.getPosition();
        System.out.println("POSITION OF ISLAND DOMAIN CENTER: "+p.toString());
        float distX = Math.abs(p.x - this.getCenterX());
        float distY = Math.abs(p.y - this.getCenterY());
        System.out.println("Dist X: "+(distX));
        System.out.println("Dist Y: "+(distY));
        float dist = (float) Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
        System.out.println("DIST from getDistance(Collinder c): "+(dist));
        return dist;

    }


    public boolean isColliding(Collider c){

        return (this.getDistance(c) <= c.getRadius());

    }
    public boolean isColliding(Collider c,String msg){

        return (this.getDistance(c,msg) <= c.getRadius());

    }

}
