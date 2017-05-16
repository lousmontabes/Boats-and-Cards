package com.example.lluismontabes.gameofboatsandcards.Model;

import android.graphics.Point;

/**
 * Created by Lous on 16/05/2017.
 */

public class CubicBezierCurve {

    private Point p0; // Starting point
    private Point p1; // Support point 1
    private Point p2; // Support point 2
    private Point p3; // Finishing point

    /**
     * Constructor that takes the 4 points as parameters.
     * @param p0 Starting point.
     * @param p2 Support point 1.
     * @param p3 Support point 2.
     * @param p4 Finishing point.
     */
    public CubicBezierCurve(Point p0, Point p2, Point p3, Point p4){
        this.p0 = p0;
        this.p1 = p2;
        this.p2 = p3;
        this.p3 = p4;
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
        this.p1 = new Point((int) (40 * Math.cos(a)), (int) (40 * Math.sin(a)));
        this.p2 = new Point((int) (40 * Math.cos(b)), (int) (40 * Math.sin(b)));
        this.p3 = p3;
    }

    /**
     * Constructor that takes the coordinates of the 4 points as parameters.
     * @param x1 X coordinate of starting point.
     * @param y1 Y coordinate of starting point.
     * @param x2 X coordinate of support point 1.
     * @param y2 Y coordinate of support point 2.
     * @param x3 X coordinate of support point 3.
     * @param y3 Y coordinate of support point 3.
     * @param x4 X coordinate of finishing point.
     * @param y4 Y coordinate of finishing point.
     */
    public CubicBezierCurve(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
        this.p0 = new Point(x1, y1);
        this.p1 = new Point(x2, y2);
        this.p2 = new Point(x3, y3);
        this.p3 = new Point(x4, y4);
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

}
