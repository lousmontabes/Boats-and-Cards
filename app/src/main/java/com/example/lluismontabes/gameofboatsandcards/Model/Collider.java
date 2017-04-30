package com.example.lluismontabes.gameofboatsandcards.Model;

import android.content.Context;
import android.graphics.Point;
import android.widget.RelativeLayout;

/**
 * Created by lluismontabes on 20/4/17.
 */

public abstract class Collider extends RelativeLayout {

    protected int marginLeft, marginTop;

    public Collider(Context context) {
        super(context);
        marginLeft = 0;
        marginTop = 0;
    }

    public Collider(Context context, int marginLeft, int marginTop) {
        super(context);
        this.marginLeft = marginLeft;
        this.marginTop = marginTop;
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
        Point p = new Point((int)this.getX() + marginLeft, (int)this.getY() + marginTop);
        return p;
    }

    /**
     * Returns distance from the center of this Collider to the center of the
     * specified Collider.
     * @param c     Collider to get distance to.
     * @return      Distance.
     */
    public float getDistance(Collider c){
        return (float) Math.hypot(getDistanceVector(c).x, getDistanceVector(c).y);
    }

    /**
     * Returns the coordinates of the vector from the center of this Collider to
     * the center of the specified Collider.
     * @param c     Collider to get vector to.
     * @return      Vector coordinates.
     */
    public Point getDistanceVector(Collider c){
        float distX = c.getCenter().x - this.getCenter().x;
        float distY = c.getCenter().y - this.getCenter().y;
        return new Point((int) distX, (int) distY);
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
