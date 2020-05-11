package com.gr.five;

import no.geosoft.cc.geometry.Geometry;
import no.geosoft.cc.graphics.*;

import java.awt.*;

public class Point extends GObject implements Comparable<Point> {

    private double x, y;
    private double size;
    private GSegment circle;

    public Point(double x, double y, double size, GScene scene) {

        this.x = x;
        this.y = y;
        this.size = size;

        circle = new GSegment();
        addSegment(circle);

        setStyle(new GStyle());

        scene.add(this);
    }

    public Point(double x, double y) {

        this.x = x;
        this.y = y;
        this.size = 0;

    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }

    public void draw() {
        circle.setGeometryXy(Geometry.createEllipse(x, y,
                1500.0 * size / getScene().getViewport().getWidth(),
                1250.0 * size / getScene().getViewport().getHeight()
        ));
    }

    public void move(double x, double y) {
        this.x = x / getScene().getViewport().getWidth() * 100.0;
        this.y = y / getScene().getViewport().getHeight() * 100.0;
    }

    private void setColor(Color color) {
        //getStyle().setForegroundColor(color);
        getStyle().setBackgroundColor(color);
    }

    private Color getColor() {
        return getStyle().getBackgroundColor();
    }

    public void setText(String text) {
        circle.setText(new GText(text, GPosition.MIDDLE));
    }

    public void setState(int state) {
        if (state == 2) {
            setColor(Color.GREEN);
        } else if (state == 1) {
                setColor(Color.YELLOW);
        } else {
            setColor(Color.RED);
        }
    }

    public int getState() {
        if (getColor() == Color.GREEN) {
            return 2;
        } else if (getColor() == Color.YELLOW) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setSize(double size) {
        this.size = size;
    }

    @Override
    public int compareTo(Point o) {
        if (y == o.y) {
            if (x == o.x) {
                return 0;
            }
            return x < o.x ? -1 : 1;
        }
        return y < o.y ? -1 : 1;
    }

    public static double angle(double[] p1, double[] p2, double[] p3) {
        double a = Math.atan2(p3[1] - p2[1], p3[0] - p2[0])
                - Math.atan2(p1[1] - p2[1], p1[0] - p2[0]);
        if (a < Math.PI) a += 2 * Math.PI;
        if (a >= Math.PI) a -= 2 * Math.PI;
        return a;
    }

    public static double angle(Point p1, Point p2, Point p3) {
        return angle(
                new double[]{p1.getX(), p1.getY()},
                new double[]{p2.getX(), p2.getY()},
                new double[]{p3.getX(), p3.getY()});
    }

    private static double area(double[] p1, double[] p2, double[] p3) {
        return Math.abs((p1[0] * (p2[1] - p3[1]) + p2[0] * (p3[1] - p1[1]) + p3[0] * (p1[1] - p2[1])) / 2);
    }

    public static double area(Point p1, Point p2, Point p3) {
        return area(
                new double[]{p1.getX(), p1.getY()},
                new double[]{p2.getX(), p2.getY()},
                new double[]{p3.getX(), p3.getY()});
    }

    private static int side(double[] a, double[] b, double[] x) {
        double angle = angle(a, x, b);
        //if (Math.PI - Math.abs(angle) < 0.1) return 0;
        return (int) - Math.signum(angle);
    }

    public static int side(Point a, Point b, Point x) {
        return side(
                new double[]{a.getX(), a.getY()},
                new double[]{b.getX(), b.getY()},
                new double[]{x.getX(), x.getY()});
    }
}
