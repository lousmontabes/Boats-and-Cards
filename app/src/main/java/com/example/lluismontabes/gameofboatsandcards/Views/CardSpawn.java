package com.example.lluismontabes.gameofboatsandcards.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.example.lluismontabes.gameofboatsandcards.Model.Graphics;
import com.example.lluismontabes.gameofboatsandcards.Model.RectangularCollider;
import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by Delta on 03/05/2017.
 */

public class CardSpawn extends RectangularCollider {

    public CardSpawn(Context context) {
        super(context, 15, 20, 40, 45);
        LayoutInflater.from(context).inflate(R.layout.card_back, this);
    }

    public CardSpawn(Context context, int marginLeft, int marginTop) {
        super(context, 15, 20, marginLeft, marginTop);
        LayoutInflater.from(context).inflate(R.layout.card_back, this);
    }
}
