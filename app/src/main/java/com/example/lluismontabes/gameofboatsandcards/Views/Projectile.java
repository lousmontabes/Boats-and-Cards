package com.example.lluismontabes.gameofboatsandcards.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;

import com.example.lluismontabes.gameofboatsandcards.Model.Graphics;
import com.example.lluismontabes.gameofboatsandcards.Model.RoundCollider;

/**
 * Created by lmontaga7.alumnes on 13/03/17.
 */

public class Projectile extends RoundCollider {

    Paint projectilePaint;
    private int damage;
    private float velocity, angle, originX, originY;

    public Projectile(Context context, float angle, float originX, float originY, int damage) {
        super(context, 20);

        setWillNotDraw(false);

        this.velocity = 30;
        this.damage = damage;
        this.angle = angle;
        this.setX(originX - Graphics.toPixels(getContext(), 10));
        this.setY(originY - Graphics.toPixels(getContext(), 10));

        int pixels = (int) Graphics.toPixels(getContext(), 20);

        this.setLayoutParams(new ViewGroup.LayoutParams(pixels, pixels));

        styleDefine();
    }

    private Paint styleDefine(){
        this.projectilePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        projectilePaint.setColor(Color.WHITE);
        projectilePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return projectilePaint;
    }

    @Override
    protected void onDraw(Canvas canvas){
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        canvas.drawCircle(this.getWidth()/2, this.getWidth()/2, this.getWidth()/4, projectilePaint);
        System.out.println(this.getX() + ", " + this.getY());
    }

    public void move(){

        float x = this.getX();
        float y = this.getY();

        float scaleX = (float) Math.cos(this.angle);
        float scaleY = (float) Math.sin(this.angle);

        float velocityX = scaleX * this.velocity;
        float velocityY = scaleY * this.velocity;

        this.setX(x + velocityX);
        this.setY(y + velocityY);
        this.setRotation((float) Math.toDegrees(angle) + 90);

    }

    public void explode(){

        this.setScaleX(2);
        this.setScaleY(2);

    }

    public int getDamage() {
        return damage;
    }

}
