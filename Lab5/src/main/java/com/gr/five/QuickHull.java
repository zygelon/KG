package com.gr.five;

import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GStyle;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class QuickHull extends GObject {
    private Point[] points;
    private Line[] hull;

    public QuickHull(Double[] coords, GScene scene) {

        int pointAmount = coords.length / 2;
        points = new Point[pointAmount];
        for (int i = 0; i < pointAmount; i++) {
            points[i] = new Point(coords[i * 2], coords[i * 2 + 1], 0.33, scene);
        }

        buildHull(scene);

        setStyle(new GStyle());

        scene.add(this);
    }

    public Point furthestPoint(LinkedList<Point> s, Point l, Point r) {
        Point result = null;
        for (Point p : s) {
            if (result == null) {
                result = p;
            } else if (Point.area(p, l, r) > Point.area(result, l, r)) {
                result = p;
            } else if (Point.area(p, l, r) == Point.area(result, l, r) && Point.angle(p, l, r) > Point.angle(result, l, r)) {
                result = p;
            }
        }
        return result;
    }

    public void buildHull(GScene scene) {
        LinkedList<Point> s = new LinkedList<>(Arrays.asList(points));
        Collections.sort(s);
        double eps = 0.000001;
        Point l = s.getFirst();
        Point r = new Point(l.getX() + eps, l.getY());
        s.add(r);
        s = quickHull(s, l, r);
        s.remove(r);
        LinkedList<Line> h = new LinkedList<Line>();
        for (int i = 0, size = s.size(); i < size; i++) {
            h.add(new Line(s.get(i), s.get((i + 1) % size), scene));
        }
        hull = h.toArray(new Line[0]);
    }

    private LinkedList<Point> quickHull(LinkedList<Point> s, Point l, Point r) {
        boolean empty = true;
        for (Point p : s) {
            if (p != l && p != r) {
                empty = false;
                break;
            }
        }
        if (empty) {
            return new LinkedList<>(Arrays.asList(l, r));
        } else {
            Point h = furthestPoint(s, l, r);
            LinkedList<Point> s1 = new LinkedList<>(), s2 = new LinkedList<>();
            for (Point p : s) {
                if (Point.side(l, h, p) < 1) s1.add(p);
                if (Point.side(h, r, p) < 1) s2.add(p);
            }
            LinkedList<Point> q1 = quickHull(s1, l, h);
            LinkedList<Point> q2 = quickHull(s2, h, r);
            q2.remove(h);
            q1.addAll(q2);
            return q1;
        }
    }
}
