package com.example.lluismontabes.gameofboatsandcards.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by lluismontabes on 20/4/17.
 */

public abstract class RectangularCollider extends Collider {

    private Point center;
    private float w, h;
    private float halfWidth, halfHeight, halfHypotenuse;
    private float criticalAngle;

    /**
     * Rectangular collider with no margins constructor.
     * @param context   View context
     * @param w         Width
     * @param h         Height
     */
    public RectangularCollider(Context context, float w, float h){
        super(context);

        this.w = w;
        this.h = h;

        this.halfWidth = w / 2;
        this.halfHeight = h / 2;
        this.halfHypotenuse = (float) Math.hypot((double) halfWidth,(double) halfHeight);

        this.criticalAngle = (float) Math.asin(halfHeight / halfHypotenuse);

        this.center = new Point();

        setWillNotDraw(false);
    }

    /**
     * Rectangular collider with specified margins constructor.
     * @param context   View context
     * @param w         Width
     * @param h         Height
     */
    public RectangularCollider(Context context, float w, float h, int marginLeft, int marginTop){
        super(context, marginLeft, marginTop);

        this.w = w;
        this.h = h;

        this.halfWidth = w / 2;
        this.halfHeight = h / 2;
        this.halfHypotenuse = (float) Math.hypot((double) halfWidth,(double) halfHeight);

        this.criticalAngle = (float) Math.asin(halfHeight / halfHypotenuse);

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
        center.set((int) (getX() + getHalfWidthPixels() + marginLeft), (int) (getY() + getHalfHeightPixels() + marginTop));
        //System.out.println("Center of RectangularCollider at: " + center);
        return center;
    }

    public float getDistanceToContact(Collider c){
        return Graphics.toPixels(getContext(), getHypotAtAngle(getIncidenceAngle(c)));
    }

    public float getIncidenceAngle(Collider c){
        float distX = Math.abs(getDistanceVector(c).x);
        float distY = Math.abs(getDistanceVector(c).y);
        float angle = (float) Math.atan2(distY, distX);
        return angle;
    }

    public float getHypotAtAngle(float angle){

        float hypot = -1;

        if (Math.abs(angle) < criticalAngle){
            hypot = (float) (halfWidth / Math.cos(angle));
        } else if (Math.abs(angle) > criticalAngle){
            hypot = (float) (halfHeight / Math.sin(angle));
        } else if (Math.abs(angle) == criticalAngle){
            hypot = halfHypotenuse;
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
        /*
        Paint hitboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hitboxPaint.setColor(Color.MAGENTA);
        hitboxPaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(getPosition().x, getPosition().y, getPosition().x + Graphics.toPixels(getContext(), w), getPosition().y + Graphics.toPixels(getContext(), h), hitboxPaint);
        canvas.drawCircle(getCenter().x, getCenter().y, 10, hitboxPaint);
        */
    }

}
