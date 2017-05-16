package com.example.lluismontabes.gameofboatsandcards.Model;

import android.graphics.Point;

/**
 * Created by Lous on 16/05/2017.
 */

public class CubicBezierCurve {

    private Point p0;
    private Point p1;
    private Point p2;
    private Point p3;

    public CubicBezierCurve(Point p0, Point p2, Point p3, Point p4){
        this.p0 = p0;
        this.p1 = p2;
        this.p2 = p3;
        this.p3 = p4;
    }

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

        return new Point(x, y);

    }

}
