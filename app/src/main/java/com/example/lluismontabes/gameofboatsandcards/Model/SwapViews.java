package com.example.lluismontabes.gameofboatsandcards.Model;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.example.lluismontabes.gameofboatsandcards.Interface.Flip3DAnimation;

/**
 * Created by JorgeTB on 17/04/2017.
 */

public class SwapViews implements Runnable{

    private boolean mIsFirstView;
    ImageView image1;
    ImageView image2;

    public SwapViews(boolean isFirstView, ImageView image1, ImageView image2) {
        mIsFirstView = isFirstView;
        this.image1 = image1;
        this.image2 = image2;
    }

    @Override
    public void run() {
        final float centerX = image1.getWidth() / 2.0f;
        final float centerY = image1.getHeight() / 2.0f;
        Flip3DAnimation rotation;
        if (mIsFirstView) {
            image1.setVisibility(View.GONE);
            image2.setVisibility(View.VISIBLE);
            //image2.bringToFront();
            image2.requestFocus();
            rotation = new Flip3DAnimation(-90, 0, centerX, centerY);
        } else {
            image2.setVisibility(View.GONE);
            image1.setVisibility(View.VISIBLE);
            //image1.bringToFront();
            image1.requestFocus();
            rotation = new Flip3DAnimation(90, 0, centerX, centerY);

        }
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new DecelerateInterpolator());
        if (mIsFirstView) {
            image2.startAnimation(rotation);
        } else {
            image1.startAnimation(rotation);
        }
    }
}
