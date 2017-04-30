package com.example.lluismontabes.gameofboatsandcards.Views;


import com.example.lluismontabes.gameofboatsandcards.R;

/**
 * Created by olekboad13.alumnes on 31/03/17.
 */

public class  Card {

    public static final int TOTAL_CARD_NUMBER = 10;

    // Target of the effect of the card
    public static final class Target {
        public static final int SELF = 0;
        public static final int OPPONENT = 1;
        public static final int ALL = 2;
        public static final int RANDOM = 3;
        public static final int TRAP = 4;
    }

    // Possible effects of the cards
    public static final class Effect {
        public static final int SPEED_UP = 0;
        public static final int ATTACK_UP = 1;
        public static final int DEFENSE_UP = 2;
        public static final int STUNNED = 3;
        public static final int REVERSED_HAND = 4;
        public static final int DISCARD_ALL = 5;
        public static final int REVERSED_CONTROLS = 6;
        public static final int TRIPLE_SHOT = 7;
        public static final int DISPEL = 8;
        public static final int KO = 9;
        public static final int QUICK_REVIVE = 10;
        public static final int RANDOM_WARP = 11;
        public static final int FULL_RESTORATION = 12;
    }

    private int target;
    private int id;
    private String name;
    private boolean reversed;

    /**
     * Random card constructor.
     */
    public Card() {
        id = (int) (Math.random() * TOTAL_CARD_NUMBER) + 1;
        name = getName(id);
        target = getTarget(id);
        reversed = false;
    }

    /**
     * Specific card constructor.
     * @param id    Type of the card to create.
     */
    public Card(int id) {
        this.id = id;
        name = getName(id);
        target = getTarget(id);
    }

    public int getId() {
        return id;
    }

    public static String getName(int id) {
        switch(id) {
            case 1:
                return "Attack up!";
            case 2:
                return "Immobilized!";
            case 3:
                return "Speed up!";
            case 4:
                return "Cards reversed!";
            case 5:
                return "All cards discarded!";
            case 6:
                return "Backwards!";
            case 7:
                return "Multi-shot!";
            case 8:
                return "All effects removed!";
            case 9:
                return "YOU DIED";
            case 10:
                return "Quick revive!";
            default:
                return "Carta ?";
        }
    }

    public String getName() {
        return name;
    }

    public static int getTarget(int id) {
        return Target.SELF;
        /*switch(id) {
            case 1:
                return Target.SELF;
            case 2:
                return Target.OPPONENT;
            case 3:
                return Target.ALL;
            case 4:
                return Target.RANDOM;
            case 5:
                return Target.TRAP;
            default:
                throw new UnknownError();
        }*/
    }

    public int getTarget() {
        return target;
    }

    public int getResourceID() {
        if (reversed) {
            return R.drawable.placeholder_reversed;
        }
        switch(id) {
            case 1:
                return R.drawable.placeholder1;
            case 2:
                return R.drawable.placeholder2;
            case 3:
                return R.drawable.placeholder3;
            case 4:
                return R.drawable.placeholder4;
            case 5:
                return R.drawable.placeholder5;
            case 6:
                return R.drawable.placeholder6;
            default:
                return R.drawable.void_card_resized;
        }
    }

    public int getEffect() {
        switch (id) {
            case 1:
                return Effect.ATTACK_UP;
            case 2:
                return Effect.STUNNED;
            case 3:
                return Effect.SPEED_UP;
            case 4:
                return Effect.REVERSED_HAND;
            case 5:
                return Effect.DISCARD_ALL;
            case 6:
                return Effect.REVERSED_CONTROLS;
            case 7:
                return Effect.TRIPLE_SHOT;
            case 8:
                return Effect.DISPEL;
            case 9:
                return Effect.KO;
            case 10:
                return Effect.QUICK_REVIVE;
            default:
                throw new UnknownError();
        }
    }

    public int getDuration() {
        switch (id) {
            //TODO: specify the duration of all effects
            // Duration in frames
            case 5:
            case 8:
            case 9:
                return 1;//Instant
            case 10:
                return Integer.MAX_VALUE;//Infinite
            default:
                return 150;
        }
    }

    public boolean getReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

}
