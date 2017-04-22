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

public abstract class RectangularCollider extends Collider {

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

        this.halfWidth = Graphics.toPixels(getContext(), w / 2);
        this.halfHeight = Graphics.toPixels(getContext(), h / 2);
        this.halfHypothenuse = (float) Math.hypot((double) halfWidth,(double) halfHeight);

        this.criticalAngle = (float) Math.asin(halfHeight / halfHypothenuse);

        this.center = new Point();

        setWillNotDraw(false);
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
        center.set((int) (this.getX() + halfWidth), (int) (this.getY() + halfHeight));
        //System.out.println("Center of RectangularCollider at: " + center);
        return center;
    }

    public float getDistanceToContact(Collider c){
        return getHypotAtAngle(getIncidenceAngle(c));
    }

    public float getIncidenceAngle(Collider c){
        System.out.println("ANGLE OF INCIDENCE: " + Math.toDegrees(Math.asin(c.getCenter().x - this.getCenter().x / this.getDistance(c))));
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

    public void showHitbox(){

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint hitboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hitboxPaint.setColor(Color.MAGENTA);
        hitboxPaint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(getPosition().x, getPosition().y, getPosition().x + getWidth(), getPosition().y + getHeight(), hitboxPaint);

    }

    @Override
    protected void onDraw(Canvas canvas){

        Paint hitboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hitboxPaint.setColor(Color.MAGENTA);
        hitboxPaint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(getPosition().x, getPosition().y, getPosition().x + getWidth(), getPosition().y + getHeight(), hitboxPaint);
        canvas.drawCircle(getCenter().x, getCenter().y, 10, hitboxPaint);
    }

}
