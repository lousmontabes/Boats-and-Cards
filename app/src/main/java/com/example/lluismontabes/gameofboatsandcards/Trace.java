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
        tracePaint.setAlpha(2);
        tracePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return tracePaint;
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawRect(originX - 45, originY - 45, originX + 45, originY + 45, tracePaint);
    }

    public void fade(){
        if(this.getAlpha() > 0){
            this.setAlpha((float) (this.getAlpha() - 0.05));
            System.out.println(this.getAlpha());
        }
    }

}
