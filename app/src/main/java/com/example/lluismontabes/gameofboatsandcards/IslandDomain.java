package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by jtomebla14.alumnes on 13/03/17.
 */

class IslandDomain extends View {


    Paint radIsland;

    public IslandDomain(Context context) {
        super(context);
        styleDefine();
    }

    private Paint styleDefine(){
        this.radIsland = new Paint(Paint.ANTI_ALIAS_FLAG);
        radIsland.setColor(Color.GREEN);
        radIsland.setStyle(Paint.Style.FILL_AND_STROKE);
        return radIsland;
    }

    @Override
    protected void onDraw(Canvas canvas){
        int alt = canvas.getHeight();
        int anc = canvas.getWidth();
        canvas.drawCircle(anc/2,alt/2,50,radIsland);

    }

}

