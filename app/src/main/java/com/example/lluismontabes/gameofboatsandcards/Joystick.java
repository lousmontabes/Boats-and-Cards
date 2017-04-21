package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Joystick extends RelativeLayout {

    /** UI REFERENCES **/
    private ImageView image;
    private RelativeLayout area;

    /** LOGIC VALUES **/
    private float currentAngle = 0;
    private float currentDistance = 0;

    public Joystick(Context context, AttributeSet attrs) {

        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.joystick,this);

        this.image = (ImageView) findViewById(R.id.imgJoystick);
        this.area = (RelativeLayout) findViewById(R.id.layoutJoystick);

        this.image.setOnTouchListener(new OnTouchListener() {

            RelativeLayout.LayoutParams parms;
            LinearLayout.LayoutParams par;
            float dx = 0, dy = 0, x = 0, y = 0;
            float centerX, centerY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        dx = Joystick.this.image.getX() - event.getRawX();
                        dy = Joystick.this.image.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        centerX = area.getWidth()/2 - image.getWidth()/2;
                        centerY = area.getHeight()/2 - image.getHeight()/2;

                        x = event.getRawX() + dx;
                        y = event.getRawY() + dy;

                        // Calculate the current angle using the arctangent:
                        Joystick.this.currentAngle = (float) Math.atan2((y - centerY), (x - centerX));

                        // Calculate the current distance using Pythagoras' theorem:
                        // (Distance is limited to 90)
                        Joystick.this.currentDistance = 10 + (float) Math.hypot(y - centerY, x - centerX);

                        // Animate the joystick view:
                        Joystick.this.image.animate()
                                .x(Math.max(
                                        Math.min(x,
                                                centerX + Math.abs(centerX * (float) Math.cos(currentAngle))),
                                        centerX - Math.abs(centerX * (float) Math.cos(currentAngle))
                                ))
                                .y(Math.max(
                                        Math.min(y,
                                                centerY + Math.abs(centerY * (float) Math.sin(currentAngle))),
                                        centerY - Math.abs(centerY * (float) Math.sin(currentAngle))
                                ))
                                .setDuration(0)
                                .start();

                        break;

                    case MotionEvent.ACTION_UP:
                        Joystick.this.image.animate()
                                .x(centerX)
                                .y(centerY)
                                .setDuration(100)
                                .start();

                        Joystick.this.currentDistance = 10;
                        break;

                }
                return true;
            }
        });

    }

    /**
     * Returns the current angle from the center the joystick is moving to.
     * When the joystick is not moving, the last recorded angle will be returned.
     * @return float
     */
    public float getCurrentAngle(){
        return this.currentAngle;
    }

    /**
     * Returns the current distance from the center the user is pulling the joystick to.
     * @return float
     */
    public float getCurrentDistance(){
        return this.currentDistance;
    }

    /**
     * Returns the intensity with which the joystick is being pulled.
     * @return float
     */
    public float getCurrentIntensity(){
        return Math.min(this.currentDistance / this.area.getWidth(), 1.0f);
    }

}
