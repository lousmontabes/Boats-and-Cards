package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.widget.RelativeLayout;

/**
 * Created by lluismontabes on 17/3/17.
 */

public abstract class RoundCollider extends RelativeLayout implements Collider {

    private float radius, radiusPixels;
    private Point center;
    private float x, y, w, h;

    /**
     * Round collider constructor
     * @param context View context
     * @param r Radius of the collider
     */
    public RoundCollider(Context context, float r){
        super(context);

        this.radius = r;
        this.radiusPixels = Graphics.toPixels(getContext(), r);
        this.center = new Point();

        setWillNotDraw(false);
    }

    public float getRadius(){
        return this.radius;
    }

    public float getRadiusPixels() {
        return radiusPixels;
    }

    public Point getCenter(){
        center.set((int) (this.getX() + this.getRadiusPixels()), (int) (this.getY() + this.getRadiusPixels()));
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

    public boolean isColliding(Collider c){

        boolean colliding = false;

        if (c instanceof RoundCollider){

            // Specified collider is a RoundCollider.
            // Round - Round collision.

            RoundCollider roundCollider = (RoundCollider) c;
            colliding = (this.getDistance(c) <= (this.getRadiusPixels() + roundCollider.getRadiusPixels()));

        }else if (c instanceof RectangularCollider){

            // Specified collider is a RectangularCollider.
            // Round - Rectangular collision.

            RectangularCollider rectangularCollider = (RectangularCollider) c;
            colliding = (this.getDistance(c) <= (this.getRadiusPixels() + rectangularCollider.getHalfWidthPixels()));

        }

        return colliding;

    }

    public void showHitbox(){

        int bDim = (int) getRadiusPixels();

        Bitmap bitmap = Bitmap.createBitmap(bDim, bDim, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint hitboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hitboxPaint.setColor(Color.MAGENTA);
        hitboxPaint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(getCenter().x, getCenter().y, getRadiusPixels(), hitboxPaint);

    }

}
