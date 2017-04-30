package com.example.lluismontabes.gameofboatsandcards.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by lluismontabes on 17/3/17.
 */

public abstract class RoundCollider extends Collider {

    private float radius, radiusPixels;
    private Point center;
    private float x, y, w, h;

    /**
     * Round collider constructor
     *
     * @param context View context
     * @param r       Radius of the collider
     */
    public RoundCollider(Context context, float r) {
        super(context);

        this.radius = r;
        this.radiusPixels = Graphics.toPixels(getContext(), r);
        this.center = new Point();

        setWillNotDraw(false);
    }

    public float getRadius() {
        return this.radius;
    }

    public float getRadiusPixels() {
        return radiusPixels;
    }

    public float getDistanceToContact(Collider c) {
        return getRadiusPixels();
    }

    public Point getCenter() {
        // Position of colliders can change, so center must be set dynamically.
        center.set((int) (this.getX() + this.getRadiusPixels() + marginLeft), (int) (this.getY() + this.getRadiusPixels() + marginTop));
        //System.out.println("Center of RoundCollider at: " + center);
        return center;
    }

    public void showHitbox() {

        int bDim = (int) getRadiusPixels();

        Bitmap bitmap = Bitmap.createBitmap(bDim, bDim, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint hitboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hitboxPaint.setColor(Color.MAGENTA);
        hitboxPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawCircle(getCenter().x, getCenter().y, getRadiusPixels(), hitboxPaint);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        Paint hitboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hitboxPaint.setColor(Color.MAGENTA);
        hitboxPaint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(getPosition().x, getPosition().y, getPosition().x + getWidth(), getPosition().y + getHeight(), hitboxPaint);
    }

}
