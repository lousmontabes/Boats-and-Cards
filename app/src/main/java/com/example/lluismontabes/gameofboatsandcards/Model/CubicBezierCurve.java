package com.example.lluismontabes.gameofboatsandcards.Model;

import android.graphics.Point;

import com.example.lluismontabes.gameofboatsandcards.Interface.GameActivity;

/**
 * Created by Lous on 16/05/2017.
 */

public class CubicBezierCurve {

    private Point p0; // Starting point
    private Point p1; // Support point 1
    private Point p2; // Support point 2
    private Point p3; // Finishing point

    private float a;  // Starting angle
    private float b;  // Finishing angle

    /**
     * Constructor that takes the 4 points as parameters.
     * @param p0 Starting point.
     * @param p1 Support point 1.
     * @param p2 Support point 2.
     * @param p3 Finishing point.
     */
    public CubicBezierCurve(Point p0, Point p1, Point p2, Point p3){
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        // TODO: Proof-check the following:
        this.a = (float) Math.atan2((p1.y - p0.y), (p1.x - p0.x)) + 90;
        this.b = (float) Math.atan2((p2.y - p3.y), (p2.x - p3.x)) + 90;
    }

    /**
     * Constructor that takes the starting point (p0) and finish point (p3) as parameters
     * and generates a point at distance 40 from p0 and p3 at an angle of a degrees and b degrees.
     * @param p0 Starting point.
     * @param p3 Finishing point.
     * @param a  Angle between p0 and p1.
     * @param b  Angle between p2 and p3.
     */
    public CubicBezierCurve(Point p0, Point p3, float a, float b){
        this.p0 = p0;
        //this.p1 = new Point((int) (p0.x + 40 * Math.cos(Math.toRadians(a))), (int) (p0.y + 40 * Math.sin(Math.toRadians(a))));
        //this.p2 = new Point((int) (p3.x + 40 * Math.cos(Math.toRadians(b))), (int) (p3.y + 40 * Math.sin(Math.toRadians(b))));
        this.p1 = p0;
        this.p2 = p3;
        this.p3 = p3;

        this.a = a;
        this.b = b;
    }

    /**
     * Constructor that takes the coordinates of the 4 points as parameters.
     * @param x0 X coordinate of starting point.
     * @param y0 Y coordinate of starting point.
     * @param x1 X coordinate of support point 1.
     * @param y1 Y coordinate of support point 1.
     * @param x2 X coordinate of support point 2.
     * @param y2 Y coordinate of support point 2.
     * @param x3 X coordinate of finishing point.
     * @param y3 Y coordinate of finishing point.
     */
    public CubicBezierCurve(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3){
        this.p0 = new Point(x0, y0);
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
        this.p3 = new Point(x3, y3);
        // TODO: Proof-check the following:
        this.a = (float) Math.atan2((y1 - y0), (x1 - x0)) + 90;
        this.b = (float) Math.atan2((y2 - y3), (x2 - x3)) + 90;
    }

    public CubicBezierCurve(){
        this.p0 = new Point();
        this.p1 = new Point();
        this.p2 = new Point();
        this.p3 = new Point();
        this.a = 90;
        this.b = 90;
    }

    /**
     * Changes the properties of the Bezier curve.
     * @param p0 Starting point.
     * @param p3 Finishing point.
     * @param a  Starting angle in rad.
     * @param b  Finishing angle in rad.
     */
    public void set(Point p0, Point p3, float a, float b){
        this.p0 = p0;
        //this.p1 = new Point((int) (p0.x + 40 * Math.cos(Math.toRadians(a))), (int) (p0.y + 40 * Math.sin(Math.toRadians(a))));
        //this.p2 = new Point((int) (p3.x + 40 * Math.cos(Math.toRadians(b))), (int) (p3.y + 40 * Math.sin(Math.toRadians(b))));
        this.p1 = new Point(p0);
        p1.offset(100 * (int) Math.cos(a), 100 * (int) Math.sin(a));
        this.p2 = new Point(p3);
        p2.offset(100 * (int) Math.cos(b), 100 * (int) Math.sin(b));
        this.p3 = p3;
        this.a = a;
        this.b = b;
        System.out.println("New Bezier curve:\nP0: " + p0 + " - P1: " + p1 + " - P2: " + p2 + " - P3: " + p3 + "\nα:" + this.a + " - β:" + this.b);
    }

    /**
     * Returns the Point corresponding to the x and y values at a specific parameter.
     * @param t Parameter.
     * @return  Corresponding point.
     */
    public Point getPointAt(float t){

        double arg0 = Math.pow((1 - t), 3);
        double arg1 = 3 * Math.pow((1 - t), 2) * t;
        double arg2 = 3 * (1 - t) * Math.pow(t, 2);
        double arg3 = Math.pow(t, 3);

        double x = arg0 * p0.x + arg1 * p1.x + arg2 * p2.x + arg3 * p3.x;
        double y = arg0 * p0.y + arg1 * p1.y + arg2 * p2.y + arg3 * p3.y;

        return new Point((int) x,(int) y);

    }

    /**
     * Returns the interpolated angle between a and b at a specific parameter.
     * @param t Parameter.
     * @return  Corresponding angle.
     */
    public float getAngleAt(float t) {
        return (b - a) * t + a;
    }

}
