package com.example.lluismontabes.gameofboatsandcards.Views;

import android.content.Context;

import com.example.lluismontabes.gameofboatsandcards.Model.RectangularCollider;
import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by Delta on 03/05/2017.
 */

public class CardSpawn extends RectangularCollider {

    public CardSpawn(Context context, float w, float h) {
        super(context, w, h);
    }

    public CardSpawn(Context context, float w, float h, int marginLeft, int marginTop) {
        super(context, w, h, marginLeft, marginTop);
    }
}
