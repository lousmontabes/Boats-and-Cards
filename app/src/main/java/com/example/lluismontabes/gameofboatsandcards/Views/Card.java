package com.example.lluismontabes.gameofboatsandcards.Views;


import android.support.annotation.NonNull;

import com.example.lluismontabes.gameofboatsandcards.R;


/**
 * Created by olekboad13.alumnes on 31/03/17.
 */

public class Card {

    public static final byte TOTAL_CARD_NUMBER = 12;

    // Target of the effect of the card
    public enum Target {
        SELF, OPPONENT, ALL
    }

    // Possible effects of the cards
    public enum Effect {
        SPEED_UP, ATTACK_UP, FULL_RESTORATION,
        STUNNED, REVERSED_HAND, DISCARD_ONE,
        REVERSED_CONTROLS, TRIPLE_SHOT, DISPEL,
        KO, QUICK_REVIVE, RANDOM_WARP
    }
    // Length of the duration of the effects, in frames
    public static final class Duration {
        public static final short INSTANT = 1;
        public static final short SHORT = 60; // 2 seconds
        public static final short MEDIUM = 180; // 6 seconds
        public static final short LONG = 450; // 15 seconds
        public static final short INFINITE = -1;
    }

    private Target target;
    private byte id;
    private Effect effect;
    private short duration;
    private String effectName;
    private boolean reversed;

    public Effect getEffect() {
        return effect;
    }

    public short getDuration() {
        return duration;
    }

    /**
     * Random card constructor.
     */
    public Card() {
        id = (byte) (Math.random() * TOTAL_CARD_NUMBER + 1);
        effect = getEffect(id);
        duration = getDuration(effect);
        effectName = getEffectName(effect);
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
        duration = getDuration(effect);
        effectName = getEffectName(effect);
        target = getTarget(effect);
        reversed = false;
    }

    public byte getId() {
        return id;
    }

    public static String getEffectName(Effect effect) {
        switch (effect) {
            case ATTACK_UP:
                return "Attack up!";
            case STUNNED:
                return "Immobilized!";
            case SPEED_UP:
                return "Speed up!";
            case REVERSED_HAND:
                return "Cards reversed!";
            case DISCARD_ONE:
                return "One card discarded!";
            case REVERSED_CONTROLS:
                return "Backwards!";
            case TRIPLE_SHOT:
                return "Multi-shot!";
            case DISPEL:
                return "All effects removed!";
            case KO:
                return "YOU DIED";
            case QUICK_REVIVE:
                return "Quick revive!";
            case FULL_RESTORATION:
                return "";
            case RANDOM_WARP:
                return "Random warp!";
            default:
                return "Carta ?";
        }
    }

    public String getEffectName() {
        return effectName;
    }

    public static Target getTarget(Effect effect) {
        switch(effect) {
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
                return Target.OPPONENT;
            case DISPEL:
            case KO:
            case RANDOM_WARP:
                return Target.ALL;
            default:
                throw new UnknownError();
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
                return R.drawable.placeholder3;
            case KO:
                return R.drawable.ko;
            case QUICK_REVIVE:
                return R.drawable.quick_revive;
            case FULL_RESTORATION:
                return R.drawable.full_heal;
            case RANDOM_WARP:
                return R.drawable.rand_warp;
            default:
                throw new UnknownError();
        }
    }

    public static Effect getEffect(byte id) {
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
                return Effect.DISCARD_ONE;
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
            case 11:
                return Effect.FULL_RESTORATION;
            case 12:
                return Effect.RANDOM_WARP;
            default:
                throw new UnknownError();
        }
    }

    public static short getDuration(Effect effect) {
        switch (effect) {
            //TODO: specify the duration of all effects
            // Duration in frames
            case FULL_RESTORATION:
            case DISCARD_ONE:
            case DISPEL:
            case KO:
            case RANDOM_WARP:
                return Duration.INSTANT;
            case STUNNED:
                return Duration.SHORT;
            case SPEED_UP:
            case ATTACK_UP:
            case TRIPLE_SHOT:
            case REVERSED_CONTROLS:
                return Duration.MEDIUM;
            case REVERSED_HAND:
                return Duration.LONG;
            case QUICK_REVIVE:
                return Duration.INFINITE;
            default:
                throw new UnknownError();
        }
    }

    public boolean getReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

}
