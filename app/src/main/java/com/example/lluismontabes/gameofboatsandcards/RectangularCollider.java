package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.widget.RelativeLayout;

/**
 * Created by lluismontabes on 20/4/17.
 */

public abstract class RectangularCollider extends RelativeLayout implements Collider {

    private Point center;
    private float w, h;
    private float halfWidth, halfHeight, halfHypothenuse;
    private float criticalAngle;

    /**
     * Rectangular collider constructor
     * @param context View context
     * @param w Width
     * @param h Height
     */
    public RectangularCollider(Context context, float w, float h){
        super(context);

        this.w = w;
        this.h = h;

        this.halfWidth = w / 2;
        this.halfHeight = h / 2;
        this.halfHypothenuse = (float) Math.hypot((double) halfWidth,(double) halfHeight);

        this.criticalAngle = (float) Math.asin(halfHeight / halfHypothenuse);

        this.center = new Point();

    }

    public float getHalfWidth(){
        return this.halfWidth;
    }

    public float getHalfHeight(){
        return this.halfHeight;
    }

    public float getHalfWidthPixels(){
        return Graphics.toPixels(getContext(), halfWidth);
    }

    public float getHalfHeightPixels(){
        return Graphics.toPixels(getContext(), halfHeight);
    }

    public Point getCenter(){
        this.center.set((int) (this.getX() + halfWidth), (int) (this.getY() + halfHeight));
        return this.center;
    }

    public Point getPosition() {
        Point p = new Point((int)this.getX(), (int)this.getY());
        return p;
    }

    public float getDistance(Collider c){

        float distX = c.getCenter().x - this.getCenter().x;
        float distY = c.getCenter().y - this.getCenter().y;

        float dist = (float) Math.hypot(distX, distY);
        return dist;

    }

    public float getIncidenceAngle(Collider c){

        return (float) Math.asin(c.getCenter().x - this.getCenter().x / this.getDistance(c));

    }

    public float getHypotAtAngle(float angle){

        float hypot = -1;

        if (angle < criticalAngle){
            hypot = (float) (halfWidth / Math.cos(angle));
        } else if (angle > criticalAngle){
            hypot = (float) (halfHeight / Math.sin(angle));
        } else if (angle == criticalAngle){
            hypot = halfHypothenuse;
        }

        return hypot;

    }

    public boolean isColliding(Collider c){

        boolean colliding = false;

        if (c instanceof RoundCollider){

            // Specified collider is a RoundCollider.
            // Rectangular - Round collision.

            RoundCollider roundCollider = (RoundCollider) c;
            float angle = getIncidenceAngle(c);
            colliding = (this.getDistance(c) <= (this.getHypotAtAngle(angle) + roundCollider.getRadiusPixels()));

        }else if(c instanceof RectangularCollider){

            // Specified collider is a RectangularCollider.
            // Rectangular - Rectangular collision.

            RectangularCollider rectangularCollider = (RectangularCollider) c;
            colliding = (this.getDistance(c) <= (this.getHalfWidthPixels() + rectangularCollider.getHalfWidthPixels()));

        }

        return colliding;

    }

    public void showHitbox(){

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint hitboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hitboxPaint.setColor(Color.MAGENTA);
        hitboxPaint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(getPosition().x, getPosition().y, getPosition().x + getWidth(), getPosition().y + getHeight(), hitboxPaint);

    }

}
