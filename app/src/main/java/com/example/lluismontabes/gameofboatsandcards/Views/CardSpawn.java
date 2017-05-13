package com.example.lluismontabes.gameofboatsandcards.Views;

import android.content.Context;
import android.view.LayoutInflater;

import com.example.lluismontabes.gameofboatsandcards.Model.RoundCollider;
import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by Delta on 03/05/2017.
 */

public class CardSpawn extends RoundCollider {

    public CardSpawn(Context context) {
        super(context, 20);
        LayoutInflater.from(context).inflate(R.layout.card_back, this, true);
    }
}
