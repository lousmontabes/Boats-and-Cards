package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by jtomebla14.alumnes on 13/03/17.
 */

class IslandDomain extends Collider {

    Paint islandArea;
    private float radius;

    public IslandDomain(Context context, float radius) {
        super(context, radius);
        styleDefine();
    }

    private Paint styleDefine(){
        this.islandArea = new Paint(Paint.ANTI_ALIAS_FLAG);
        islandArea.setColor(Color.GREEN);
        islandArea.setStyle(Paint.Style.FILL_AND_STROKE);
        return islandArea;
    }

    @Override
    protected void onDraw(Canvas canvas){
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        canvas.drawCircle(canvasWidth / 2, canvasHeight / 2, 300, islandArea);

    }

}

