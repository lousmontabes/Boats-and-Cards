package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by lmontaga7.alumnes on 13/03/17.
 */

public class Projectile extends Collider {

    Paint projectilePaint;
    private float damage, velocity;
    private float angle, originX, originY;

    public Projectile(Context context, float angle, float originX, float originY) {
        super(context, 20);

        this.velocity = 30;
        this.angle = angle;
        this.setX(originX);
        this.setY(originY);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (20 * scale + 0.5f);

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

}
