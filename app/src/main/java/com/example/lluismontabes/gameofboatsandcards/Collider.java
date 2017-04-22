package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.graphics.Point;
import android.widget.RelativeLayout;

/**
 * Created by lluismontabes on 20/4/17.
 */

public abstract class Collider extends RelativeLayout {

    public Collider(Context context) {
        super(context);
    }

    /**
     * Return center coordinates.
     * @return  Center position of the Collider.
     */
    abstract Point getCenter();

    /**
     * Returns top-left (real) coordinates.
     * @return  Top-left position of the Collider.
     */
    public Point getPosition() {
        Point p = new Point((int)this.getX(), (int)this.getY());
        return p;
    }

    /**
     * Returns distance from this Collider to the specified Collider.
     * @param c     Collider to get distance to.
     * @return      Distance.
     */
    public float getDistance(Collider c){
        float distX = Math.abs(c.getCenter().x - this.getCenter().x);
        float distY = Math.abs(c.getCenter().y - this.getCenter().y);

        return (float) Math.hypot(distX, distY);
    }

    /**
     * Returns distance from the center of this Collider to the contact point
     * with the specified Collider.
     * @param c     Collider to get distance to contact with.
     * @return
     */
    abstract float getDistanceToContact(Collider c);

    /**
     * Returns whether or not the current Collider is in contact with the specified Collider.
     * @param c     Collider to check collision with.
     * @return      Whether or not there is a collision.
     */
    public boolean isColliding(Collider c){

        // Generic comparison for all types of collisions.
        // getDistanceToContact(c) returns the radius in a RoundCollider
        // and the distance from the center to an edge at a given angle
        // in a RectangularCollider.
        return (this.getDistance(c) <= (this.getDistanceToContact(c) + c.getDistanceToContact(this)));

    }

    /**
     * UNIMPLEMENTED - Shows a visual representation of the current Collider's hitbox.
     */
    abstract void showHitbox();

}
