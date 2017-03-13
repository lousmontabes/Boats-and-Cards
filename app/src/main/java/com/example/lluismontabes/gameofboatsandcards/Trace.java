package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by lmontaga7.alumnes on 13/03/17.
 */

public class Trace extends View {

    Paint tracePaint;
    private float originX, originY;

    public Trace(Context context, float originX, float originY) {
        super(context);
        this.originX = originX;
        this.originY = originY;
        styleDefine();
    }

    private Paint styleDefine(){
        this.tracePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tracePaint.setColor(Color.WHITE);
        tracePaint.setAlpha(20);
        tracePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return tracePaint;
    }

    @Override
    protected void onDraw(Canvas canvas){
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        canvas.drawCircle(originX, originY, 50, tracePaint);
        System.out.println("hi");
    }

}
