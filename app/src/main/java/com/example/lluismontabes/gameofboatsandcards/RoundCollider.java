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

public abstract class RoundCollider extends Collider {

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

    public float getDistanceToContact(Collider c){
        return getRadiusPixels();
    }

    public Point getCenter(){
        center.set((int) (this.getX() + this.getRadiusPixels()), (int) (this.getY() + this.getRadiusPixels()));
        return this.center;
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
