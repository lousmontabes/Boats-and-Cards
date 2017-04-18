package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by lluismontabes on 17/3/17.
 */

public abstract class Collider extends RelativeLayout {

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

        setWillNotDraw(false);
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

    public float getRadiusPixels() {

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int rp = (int) (this.radius * scale + 0.5f);

        return rp;

    }

    public float getCenterX(){
        // TODO: Fix this method
        return this.getX() + this.getRadiusPixels();
    }

    public float getCenterY(){
        return this.getY() + this.getRadiusPixels();
    }

    public Point getPosition() {

        Point p = new Point();
        p.set((int)this.getX(),(int)this.getY());

        return p;
    }

    public float getDistance(Collider c){

        float distX = c.getCenterX() - this.getCenterX();
        float distY = c.getCenterY() - this.getCenterY();

        float dist = (float) Math.hypot(distX, distY);

        return dist;

    }

    public boolean isColliding(Collider c){

        return (this.getDistance(c) <= (this.getRadiusPixels() + c.getRadiusPixels()));

    }

    public void showHitbox(){

        int bDim = (int) getRadiusPixels();

        Bitmap bitmap = Bitmap.createBitmap(bDim, bDim, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint hitboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hitboxPaint.setColor(Color.MAGENTA);
        hitboxPaint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(getCenterX(), getCenterY(), getRadiusPixels(), hitboxPaint);

    }

}
