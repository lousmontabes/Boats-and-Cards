package com.example.lluismontabes.gameofboatsandcards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by lluismontabes on 6/3/17.
 */

public class Joystick extends RelativeLayout {

    /** UI REFERENCES **/
    private ImageView image;
    private RelativeLayout area;

    /** LOGIC VALUES **/
    private float currentAngle;
    private float currentDistance;

    public Joystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.joystick, this);

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
                        Joystick.this.currentDistance = Math.min(10 + (float) Math.sqrt(Math.pow(y - centerY, 2) + Math.pow(x - centerX, 2)), 90);

                        // Animate the joystick view:
                        Joystick.this.image.animate()
                                .x(centerX + Math.max(
                                        Math.min(x, centerX * (float) Math.cos(currentAngle)),
                                        centerX * (float) Math.cos(currentAngle)
                                ))
                                .y(centerY + Math.max(
                                        Math.min(y, centerY * (float) Math.sin(currentAngle)),
                                        centerY * (float) Math.sin(currentAngle)
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

    public float getCurrentAngle(){
        return this.currentAngle;
    }

    public float getCurrentDistance(){
        return this.currentDistance;
    }

}
