package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by lmontaga7.alumnes on 13/03/17.
 */

public class Projectile extends View {

    Paint projectilePaint;
    private float damage, velocity;
    private float originX, originY;

    public Projectile(Context context, float originX, float originY) {
        super(context);
        this.originX = originX;
        this.originY = originY;
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
        canvas.drawCircle(originX, originY, 10, projectilePaint);
        System.out.println("hi");
    }

}
