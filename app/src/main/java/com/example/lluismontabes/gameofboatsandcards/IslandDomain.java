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

class IslandDomain extends Collider {

    private Paint islandPaint;
    private float positionX,positionY;
    private boolean isInvaded;


    public IslandDomain(Context context, float radius) {

        super(context, radius);

        this.isInvaded = false; //It is not being invaded
        setVisibility(VISIBLE);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        this.positionX = width/2;
        this.positionY = height/2;

        System.out.println("relativeLayout.getHeight():"+height);
        System.out.println("relativeLayout.getWidth(): "+width);

        //final float scale = getContext().getResources().getDisplayMetrics().density;
        //int pixels = (int) (20 * scale + 0.5f);
        this.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        //this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

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

        System.out.println("S'ha pintat islandArea ------------------------------------------------------");
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        canvas.drawCircle(this.positionX, this.positionY, this.getRadius(), islandPaint);

    }

    public Point getPosition(){

        Point p = new Point();
        p.set((int)this.positionX,(int)this.positionY);

        return p;

    }

    public void toggleInvadedStatus(){

        this.isInvaded = !isInvaded;

        if (this.isInvaded) this.islandPaint.setColor(getResources().getColor(R.color.invadedIsland));
        else this.islandPaint.setColor(getResources().getColor(R.color.uninvadedIsland));

        this.invalidate();

    }



}

