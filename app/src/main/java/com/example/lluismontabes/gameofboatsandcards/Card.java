package com.example.lluismontabes.gameofboatsandcards;

/**
 * Created by olekboad13.alumnes on 31/03/17.
 */

public class Card {

    public final static int TOTAL_CARD_NUMBER = 3;

    public final static int BOOST = 0;
    public final static int EVENT = 1;
    public final static int TRAP = 2;

    private int type;
    private int id;

    public Card() {
        id = (int) (Math.random() * TOTAL_CARD_NUMBER) + 1;
    }

    public Card(int id) {
        this.id = id;
    }

    public static void spawn() {
        float posX;
        float posY;
    }

    public int getResourceID() {
        switch(id) {
            case 1:
                return R.drawable.placeholder1;
            case 2:
                return R.drawable.placeholder2;
            case 3:
                return R.drawable.placeholder3;
            default:
                return R.drawable.void_card_resized;
        }
    }

}
