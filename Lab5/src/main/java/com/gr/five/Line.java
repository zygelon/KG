package com.gr.five;

import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;

public class Line extends GObject {
    private double x1, y1, x2, y2;
    private GSegment line;

    public Line(double x1, double y1, double x2, double y2, GScene scene) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        line = new GSegment();
        addSegment(line);

        setStyle(new GStyle());

        scene.add(this);
    }

    public Line(Point p1, Point p2, GScene scene) {
        this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), scene);
    }

    double getX1() {
        return x1;
    }

    double getY1() {
        return y1;
    }

    double getX2() {
        return x2;
    }

    double getY2() {
        return y2;
    }

    public void draw() {
        line.setGeometry(x1, y1, x2, y2);
    }

}
