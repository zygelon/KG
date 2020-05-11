package com.gr.five;

import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class RegionalSearcher extends GObject {

    private Point[] points;
    private int pointAmount;

    private SegmentTree tree;
    
    private Point from, to;
    private GSegment[] region;

    private static class SegmentTree {
        private SegmentTree parent, left, right;
        private int from, to;
        public BinaryTree<Point> subtree;

        public SegmentTree(int from, int to) {
            parent = null;
            left = null;
            right = null;
            this.from = from;
            this.to = to;
            if (from >= to) return;
            if (to - from > 1) {
                left = new SegmentTree(from, (from + to) / 2);
                right = new SegmentTree((from + to) / 2, to);
            }
        }

        public SegmentTree left() {
            return left;
        }

        public SegmentTree right() {
            return right;
        }

        public SegmentTree parent() {
            return parent;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

    }

    public RegionalSearcher(Double[] coords, GScene scene) {

        pointAmount = coords.length / 2;
        points = new Point[pointAmount];
        for (int i = 0; i < pointAmount; i++) {
            points[i] = new Point(coords[i * 2], coords[i * 2 + 1], 0.33, scene);
        }

        from = new Point(25, 25, 0.5, scene);
        to = new Point(75, 75, 0.2, scene);

        from.setText("L");
        to.setState(1);

        region = new GSegment[4];
        for (int i = 0; i < region.length; i++) {
            region[i] = new GSegment();
            addSegment(region[i]);
        }

        sort();
        buildTree();

        setStyle(new GStyle());

        scene.add(this);

    }

    private void sort() {
        Arrays.sort(points, Comparator.comparingDouble(o -> o.getX() * 1000 + o.getY()));
    }

    private void buildTree() {
        tree = new SegmentTree(0, pointAmount - 1);
        buildSubtree(tree);
    }

    private void buildSubtree(SegmentTree node) {
        if (node.to() - node.from() == 1) {
            node.subtree = new BinaryTree<>();
            node.subtree.fill(point(node.from()), point(node.to()));
            node.subtree.thread();
        } else {
            node.subtree = new BinaryTree<>();
            LinkedList<Point> points = new LinkedList<>();
            if (node.left() != null) {
                buildSubtree(node.left());
                points.addAll(node.left().subtree.toList());
            }
            points.sort(Comparator.comparingDouble(o -> o.getX() * 1000 + o.getY()));
            points.removeLast();
            if (node.right() != null) {
                buildSubtree(node.right());
                points.addAll(node.right().subtree.toList());
            }
            node.subtree.fill(points.toArray(new Point[0]));
            node.subtree.thread();
        }
    }

    private void regionalSearch(SegmentTree node) {
        if (x(node.from()) >= minX() && x(node.to()) <= maxX()) {
            regionalSubsearch(node);
            return;
        }
        if (x(node.to()) < minX() || x(node.from()) > maxX()) {
            return;
        }
        if (node.to() - node.from() == 1) {
            individualCheck(node.subtree.data());
            individualCheck(node.subtree.left().data());
            return;
        }
        if (node.left() != null) regionalSearch(node.left());
        if (node.right() != null) regionalSearch(node.right());
    }

    private void regionalSubsearch(SegmentTree node) {
        BinaryTree<Point> subnode = node.subtree;
        while (true) {
            if (subnode.left() == null) {
                break;
            }
            if (subnode.left().data().getY() < minY() && subnode.left().right().data().getY() >= minY()) {
                subnode = subnode.left().right();
                break;
            }
            if (subnode.right() != null && subnode.data().getY() < minY() && subnode.right().data().getY() >= minY()) {
                subnode = subnode.right();
                break;
            }
            if (subnode.data().getY() >= minY()) {
                subnode = subnode.left();
            } else {
                subnode = subnode.right();
                if (subnode == null) break;
            }
        }
        while (subnode != null && subnode.data().getY() <= maxY()) {
            subnode.data().setState(2);
            subnode = subnode.right();
            while (subnode != null && subnode.left() != null && subnode.left().data().getState() != 2
                    && subnode.left().data().getY() >= minY() && subnode.left().parent() == subnode) {
                subnode = subnode.left();
            }
        }
    }

    private void individualCheck(Point point) {
        if (point.getX() >= minX() && point.getX() <= maxX() && point.getY() >= minY() && point.getY() <= maxY()) {
            point.setState(2);
        }
    }

    public void draw() {
        for (Point point : points) {
            point.setState(0);
        }
        regionalSearch(tree);
        region[0].setGeometry(new double[]{
                from.getX(), from.getY(), 0,
                from.getX(), to.getY(), 0
        });
        region[1].setGeometry(new double[]{
                from.getX(), to.getY(), 0,
                to.getX(), to.getY(), 0
        });
        region[2].setGeometry(new double[]{
                to.getX(), to.getY(), 0,
                to.getX(), from.getY(), 0
        });
        region[3].setGeometry(new double[]{
                to.getX(), from.getY(), 0,
                from.getX(), from.getY(), 0
        });
        for (int i = 0; i < pointAmount; i++) {
            point(i).draw();
        }
        from.draw();
        to.draw();
    }

    public void moveFrom(double x, double y) {
        from.move(x, y);
    }

    public void moveTo(double x, double y) {
        to.move(x, y);
    }

    private double x(int i) {
        return points[i].getX();
    }

    private double y(int i) {
        return points[i].getY();
    }

    private Point point(int i) {
        return points[i];
    }

    private double minX() {
        return Math.min(from.getX(), to.getX());
    }

    private double maxX() {
        return Math.max(from.getX(), to.getX());
    }

    private double minY() {
        return Math.min(from.getY(), to.getY());
    }

    private double maxY() {
        return Math.max(from.getY(), to.getY());
    }

}
