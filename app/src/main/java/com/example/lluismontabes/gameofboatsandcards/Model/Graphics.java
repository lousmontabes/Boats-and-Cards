package com.example.lluismontabes.gameofboatsandcards.Model;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Lous on 18/04/2017.
 *
 * Library with complementary methods to work with pixel graphics.
 */

public final class Graphics {

    private Graphics(){}

    /**
     * Returns application screen width.
     * @param c     Application context.
     * @return      Screen width.
     */
    public static int getScreenWidth(Context c){
        return c.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Returns application screen height.
     * @param c     Application context.
     * @return      Screen height.
     */
    public static int getScreenHeight(Context c){
        return c.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Converts from density-independent units (dp) to real pixels.
     * @param c     Window context.
     * @param dp    Density-independent units.
     * @return      (Float) Converted unit.
     */
    public static float toPixels(Context c, float dp){
        final float scale = c.getResources().getDisplayMetrics().density;
        return (dp * scale) + 0.5f;
    }

    /**
     * Converts from pixels to density-independent units (dp).
     * @param c     Window context.
     * @param px    Pixels.
     * @return      (Float) Converted unit.
     */
    public static float toDp(Context c, float px){
        final float scale = c.getResources().getDisplayMetrics().density;
        return (px / scale) + 0.5f;
    }

}
