package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by lmontaga7.alumnes on 13/03/17.
 */

public class Projectile extends ImageView {

    private float damage, velocity;

    public Projectile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
