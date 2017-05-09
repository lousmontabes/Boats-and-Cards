package com.example.lluismontabes.gameofboatsandcards.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.example.lluismontabes.gameofboatsandcards.Interface.GameActivity;
import com.example.lluismontabes.gameofboatsandcards.Model.RoundCollider;
import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by jtomebla14.alumnes on 13/03/17.
 */

public class IslandDomain extends RoundCollider {

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

    public void toggleInvadedStatus(boolean invaderIsLocal){

        this.isInvaded = !isInvaded;

        if (isInvaded){

            if(invaderIsLocal){
                this.islandPaint.setColor(getResources().getColor(R.color.invadedIslandLocal));
            }else{
                this.islandPaint.setColor(getResources().getColor(R.color.invadedIslandRemote));
            }

        }else {
            this.islandPaint.setColor(getResources().getColor(R.color.uninvadedIsland));
        }

        this.invalidate();

    }

    /**
     * Graphically adapt island to invader. (NONE, LOCAL_PLAYER or REMOTE_PLAYER)
     * @param invader
     */
    public void setInvadedBy(GameActivity.Invader invader){

        // TODO: Generalize enum Invader to avoid having to get it from GameActivity.

        /*
         * 0: NO INVADER
         * 1: localPlayer
         * 2: remotePlayer
         */

        switch (invader) {

            case NONE:
                this.islandPaint.setColor(getResources().getColor(R.color.uninvadedIsland));
                break;

            case LOCAL_PLAYER:
                this.islandPaint.setColor(getResources().getColor(R.color.invadedIslandLocal));
                break;

            case REMOTE_PLAYER:
                this.islandPaint.setColor(getResources().getColor(R.color.invadedIslandRemote));
                break;

        }

        this.invalidate();

    }

}

