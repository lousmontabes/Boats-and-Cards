package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by jtomebla14.alumnes on 13/03/17.
 */

public class IslandDomain extends Collider {

    private Paint islandPaint;
    private boolean isInvaded;

    public IslandDomain(Context context, float radius) {

        super(context, radius);

        this.isInvaded = false; // It is not being invaded
        setVisibility(VISIBLE);

        setWillNotDraw(false);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        float screenCenterX = width / 2;
        float screenCenterY = height / 2;

        this.setX(screenCenterX - this.getRadiusPixels());
        this.setY(screenCenterY - this.getRadiusPixels());

        this.setLayoutParams(new ViewGroup.LayoutParams(width, height));

        styleDefine();
    }

    private Paint styleDefine(){

        this.islandPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        islandPaint.setColor(getResources().getColor(R.color.uninvadedIsland));
        islandPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return islandPaint;

    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawCircle(this.getRadiusPixels(), this.getRadiusPixels(), this.getRadiusPixels(), islandPaint);
        System.out.println("ISLA DIBUJADA");
    }

    public void toggleInvadedStatus(){

        this.isInvaded = !isInvaded;

        if (this.isInvaded) this.islandPaint.setColor(getResources().getColor(R.color.invadedIsland));
        else this.islandPaint.setColor(getResources().getColor(R.color.uninvadedIsland));

        this.invalidate();

    }



}

