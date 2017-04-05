package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lmontaga7.alumnes on 13/03/17.
 */

public class Trace extends View {

    Paint tracePaint;
    float angle;

    public Trace(Context context, float originX, float originY, float angle) {
        super(context);

        this.setX(originX);
        this.setY(originY);
        this.angle = angle;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(100, 120);
        this.setLayoutParams(params);

        styleDefine();
    }

    private Paint styleDefine(){
        this.tracePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tracePaint.setColor(Color.WHITE);
        tracePaint.setAlpha(2); // 2
        tracePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return tracePaint;
    }

    @Override
    protected void onDraw(Canvas canvas){

        canvas.rotate(this.angle);

        canvas.drawRect((float) (Math.cos(angle)), (float) (Math.cos(angle)), (float) (90 * Math.cos(angle)), (float) (90 * Math.cos(angle)), tracePaint);  // Estela principal
        canvas.drawRect(0, 0, 20, 90, tracePaint);  // Estela esquerra
        canvas.drawRect(70, 0, 90, 90, tracePaint); // Estela dreta

    }

}
