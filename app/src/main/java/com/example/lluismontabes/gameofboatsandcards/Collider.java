package com.example.lluismontabes.gameofboatsandcards;

import android.graphics.Point;

/**
 * Created by lluismontabes on 20/4/17.
 */

public interface Collider {

    // Returns center coordinates.
    Point getCenter();

    // Returns top-left (real) coordinates.
    Point getPosition();

    // Returns the distance from this collider to the specified collider.
    float getDistance(Collider c);

    // Returns true if this collider is currently colliding with the specified collider.
    boolean isColliding(Collider c);

    // Unimplemented. Shows a visual representation of the hitbox.
    void showHitbox();

}
