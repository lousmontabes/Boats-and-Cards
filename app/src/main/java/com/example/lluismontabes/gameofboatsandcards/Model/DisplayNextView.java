package com.example.lluismontabes.gameofboatsandcards.Model;

import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by JorgeTB on 17/04/2017.
 */

public class DisplayNextView implements Animation.AnimationListener{

    private boolean mCurrentView;
    ImageView image1;
    ImageView image2;

    public DisplayNextView(boolean currentView, ImageView image1, ImageView image2) {
        mCurrentView = currentView;
        this.image1 = image1;
        this.image2 = image2;
    }
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        image1.post(new SwapViews(mCurrentView, image1, image2));
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
