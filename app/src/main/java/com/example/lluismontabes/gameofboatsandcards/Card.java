package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by olekboad13.alumnes on 31/03/17.
 */


public class Card extends ImageView {

    public final static int BOOST = 0;
    public final static int EVENT = 1;
    public final static int TRAP = 2;

    int type;
    int id;

    public Card(Context context, int id, int type) {
        super(context);
        this.type = type;
    }

    public static void spawn() {
        float posX;
        float posY;
    }

}
