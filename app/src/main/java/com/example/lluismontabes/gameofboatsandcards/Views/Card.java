package com.example.lluismontabes.gameofboatsandcards.Views;


import com.example.lluismontabes.gameofboatsandcards.R;

import static com.example.lluismontabes.gameofboatsandcards.Views.Card.Target.*;


/**
 * Created by olekboad13.alumnes on 31/03/17.
 */

public class Card {

    public static final byte TOTAL_CARD_NUMBER = 12;

    // Target of the effect of the card
    public enum Target {
        SELF, OPPONENT, ALL, NONE
    }

    // Possible effects of the cards
    public enum Effect {
        SPEED_UP(MEDIUM, "Speed up!"),
        ATTACK_UP(MEDIUM, "Attack up!"),
        FULL_RESTORATION(INSTANT, "Full heal!"),
        STUNNED(SHORT, "Immobilized!"),
        REVERSED_HAND(LONG, "Cards reversed!"),
        DISCARD_ONE(INSTANT, "One card discarded!"),
        REVERSED_CONTROLS(MEDIUM, "Backwards!"),
        TRIPLE_SHOT(MEDIUM, "Multi-shot!"),
        DISPEL(INSTANT, "All effects removed!"),
        KO(INSTANT, "YOU DIED"),
        QUICK_REVIVE(INFINITE, "Quick revive!"),
        RANDOM_WARP(INSTANT, "Random warp!"),
        NONE(INSTANT, "");

        private short duration;
        private String name;

        Effect(short duration, String name) {
            this.duration = duration;
            this.name = name;
        }

        public short getDuration() {
            return duration;
        }

        public String getName() {
            return name;
        }
    }

    //Duration constants, in frames
    public static final short INSTANT = 1;
    public static final short SHORT = 60; // 2 seconds
    public static final short MEDIUM = 180; // 6 seconds
    public static final short LONG = 450; // 15 seconds
    public static final short INFINITE = -1;

    private Target target;
    private byte id;
    private Effect effect;
    private boolean reversed;

    /**
     * Random card constructor.
     */
    public Card() {
        id = (byte) (Math.random() * TOTAL_CARD_NUMBER);
        effect = getEffect(id);
        target = getTarget(effect);
        reversed = false;
    }

    /**
     * Specific card constructor.
     *
     * @param id Type of the card to create.
     */
    public Card(byte id) {
        this.id = id;
        effect = getEffect(id);
        target = getTarget(effect);
        reversed = false;
    }

    public byte getId() {
        return id;
    }

    public Effect getEffect() {
        return effect;
    }

    public static Target getTarget(Effect effect) {
        switch (effect) {
            case ATTACK_UP:
            case SPEED_UP:
            case FULL_RESTORATION:
            case TRIPLE_SHOT:
            case QUICK_REVIVE:
                return Target.SELF;
            case STUNNED:
            case REVERSED_HAND:
            case REVERSED_CONTROLS:
            case DISCARD_ONE:
                return OPPONENT;
            case DISPEL:
            case KO:
            case RANDOM_WARP:
                return ALL;
            default:
                return NONE;
        }
    }

    public Target getTarget() {
        return target;
    }

    public int getResourceID() {
        if (reversed) {
            return R.drawable.reversed_card;
        }
        switch (effect) {
            case ATTACK_UP:
                return R.drawable.attack_up;
            case STUNNED:
                return R.drawable.stunned;
            case SPEED_UP:
                return R.drawable.speed_up;
            case REVERSED_HAND:
                return R.drawable.reversed_hand;
            case DISCARD_ONE:
                return R.drawable.discard_one;
            case REVERSED_CONTROLS:
                return R.drawable.backwards;
            case TRIPLE_SHOT:
                return R.drawable.multishot;
            case DISPEL:
                return R.drawable.dispel;
            case KO:
                return R.drawable.ko;
            case QUICK_REVIVE:
                return R.drawable.quick_revive;
            case FULL_RESTORATION:
                return R.drawable.full_heal;
            case RANDOM_WARP:
                return R.drawable.rand_warp;
            default:
                return R.drawable.carta;
        }
    }

    public static Effect getEffect(byte id) {
        switch (id) {
            case 0:
                return Effect.ATTACK_UP;
            case 1:
                return Effect.STUNNED;
            case 2:
                return Effect.SPEED_UP;
            case 3:
                return Effect.REVERSED_HAND;
            case 4:
                return Effect.DISCARD_ONE;
            case 5:
                return Effect.REVERSED_CONTROLS;
            case 6:
                return Effect.TRIPLE_SHOT;
            case 7:
                return Effect.DISPEL;
            case 8:
                return Effect.KO;
            case 9:
                return Effect.QUICK_REVIVE;
            case 10:
                return Effect.FULL_RESTORATION;
            case 11:
                return Effect.RANDOM_WARP;
            default:
                return Effect.NONE;
        }
    }

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

}
