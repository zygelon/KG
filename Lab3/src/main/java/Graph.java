import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class Graph extends GObject {

    private double[] vertices;
    private int vertexAmount;
    private int[] edges;
    private int edgeAmount;
    private BinaryTree<Stripe> stripes;
    private int stripeAmount;
    private BinaryTree<Trapezoid>[] parts;

    private GSegment[] edgeLines;

    private static class Stripe implements Comparable<Stripe> {
        private double from, to;

        public Stripe(double a, double b) {
            from = Math.min(a, b);
            to = Math.max(a, b);
        }

        public int position (double y) {
            if (from > y) return -1;
            if (to < y) return 1;
            return 0;
        }

        public double from() {
            return from;
        }

        public double to() {
            return to;
        }

        @Override
        public int compareTo(Stripe o) {
            if (to <= o.from) return -1;
            if (from >= o.to) return 1;
            if (from == o.from && to == o.to) return 0;
            throw new IllegalArgumentException();
        }
    }

    private static class Trapezoid implements Comparable<Trapezoid> {
        private Double[] xs;

        public Trapezoid(Double[] xs) {
            this.xs = xs;
        }

        public double position(double[] x, Stripe s) {
            int left = side(new double[]{xs[0], s.from()}, new double[]{xs[1], s.to()}, x);
            int right = side(new double[]{xs[2], s.from()}, new double[]{xs[3], s.to()}, x);
            if (left == 0) return -0.5;
            if (right == 0) return 0.5;
            if (left == -1) return -1;
            if (right == 1) return 1;
            return 0;
        }

        @Override
        public int compareTo(Trapezoid o) {
            if (xs[2] <= o.xs[0] && xs[3] <= o.xs[1]) return -1;
            if (xs[0] >= o.xs[2] && xs[1] >= o.xs[3]) return 1;
            if (Arrays.equals(xs, o.xs)) return 0;
            throw new IllegalArgumentException();
        }
    }

    public Graph(double[] vertices, int[] edges, GScene scene) {

        vertexAmount = vertices.length / 2;
        edgeAmount = edges.length / 2;

        this.vertices = vertices;

        this.edges = edges;

        sort();
        buildStripes();
        buildParts();

        edgeLines = new GSegment[edgeAmount];
        for (int i = 0; i < edgeAmount; i++) {
            edgeLines[i] = new GSegment();
            addSegment(edgeLines[i]);
        }

        setStyle(new GStyle());

        scene.add(this);

    }

    private void sort() {
        double[] weights = new double[vertexAmount];
        Integer[] order = new Integer[vertexAmount];
        for (int i = 0; i < vertexAmount; i++) {
            order[i] = i;
            weights[i] = y(i) * 1000 + x(i);
        }
        Arrays.sort(order, Comparator.comparingDouble(o -> weights[o]));
        int[] inverseOrder = new int[vertexAmount];
        double[] verticesCopy = vertices.clone();
        for (int i = 0; i < vertexAmount; i++) {
            inverseOrder[order[i]] = i;
            vertices[i * 2] = verticesCopy[order[i] * 2];
            vertices[i * 2 + 1] = verticesCopy[order[i] * 2 + 1];
        }
        for (int i = 0; i < edgeAmount; i++) {
            edges[i * 2] = inverseOrder[edges[i * 2]];
            edges[i * 2 + 1] = inverseOrder[edges[i * 2 + 1]];
            if (edges[i * 2] > edges[i * 2 + 1]) {
                int temp = edges[i * 2];
                edges[i * 2] = edges[i * 2 + 1];
                edges[i * 2 + 1] = temp;
            }
        }
    }

    public void buildStripes() {
        LinkedList<Double> stripeYs = new LinkedList<>();
        stripeYs.add(0d);
        for (int i = 0; i < vertexAmount; i++) {
            if (stripeYs.isEmpty() || stripeYs.getLast() != y(i)) {
                stripeYs.add(y(i));
            }
        }
        stripeYs.add(100d);
        LinkedList<Stripe> stripes = new LinkedList<>();
        stripeAmount = stripeYs.size() - 1;
        for (int i = 0; i < stripeAmount; i++) {
            stripes.add(new Stripe(stripeYs.get(i), stripeYs.get(i + 1)));
        }
        this.stripes = new BinaryTree<>();
        this.stripes.fill(stripes.toArray(new Stripe[]{}));
    }

    private void buildParts() {
        parts = new BinaryTree[stripeAmount];
        buildParts(stripes);
    }

    private void buildParts(BinaryTree<Stripe> stripes) {
        if (stripes != null) {
            buildParts(stripes.data(), stripes.index());
            buildParts(stripes.left());
            buildParts(stripes.right());
        }
    }

    private void buildParts(Stripe s, int index) {
        ArrayList<Double> partXs = new ArrayList<>();
        partXs.add(0d);
        partXs.add(0d);
        for (int i = 0; i < edgeAmount; i++) {
            if (y(edgeStart(i)) <= s.from() && y(edgeEnd(i)) >= s.to()) {
                partXs.add(crossX(point(edgeStart(i)), point(edgeEnd(i)), s.from()));
                partXs.add(crossX(point(edgeStart(i)), point(edgeEnd(i)), s.to()));
            }
        }
        partXs.add(100d);
        partXs.add(100d);
        int trapAmount = partXs.size() / 2 - 1;

        Integer[] order = new Integer[trapAmount];
        for (int i = 0; i < trapAmount; i++) {
            order[i] = i;
        }
        Arrays.sort(order, Comparator.comparingDouble(o ->
                Math.min(partXs.get(o * 2), partXs.get(o * 2 + 1)) * 1000
                        + Math.max(partXs.get(o * 2), partXs.get(o * 2 + 1))));
        Double[] partXsCopy = partXs.toArray(new Double[]{});
        for (int i = 0; i < trapAmount; i++) {
            partXs.set(i * 2, partXsCopy[order[i] * 2]);
            partXs.set(i * 2 + 1, partXsCopy[order[i] * 2 + 1]);
        }

        LinkedList<Trapezoid> traps = new LinkedList<>();
        for (int i = 0; i < trapAmount; i++) {
            traps.add(new Trapezoid(partXs.subList(i * 2, i * 2 + 4).toArray(new Double[]{})));
        }
        parts[index] = new BinaryTree<>();
        parts[index].fill(traps.toArray(new Trapezoid[]{}));
    }

    public double face(double x, double y) {
        BinaryTree<Stripe> stripeSearch = stripes;
        while (stripeSearch.data().position(y) != 0) {
            stripeSearch = stripeSearch.data().position(y) < 0 ? stripeSearch.left() : stripeSearch.right();
        }
        Stripe stripe = stripeSearch.data();
        int index = stripeSearch.index();
        BinaryTree<Trapezoid> traps = parts[index];
        while (Math.abs(traps.data().position(new double[]{x, y}, stripe)) == 1) {
            traps = traps.data().position(new double[]{x, y}, stripe) < 0 ? traps.left() : traps.right();
        }
        if (traps.data().position(new double[]{x, y}, stripe) == 0) {
            return index + 1 + ((double)(traps.index() + 1) / 10);
        }
        return 0;
    }

    public void draw() {
        for (int i = 0; i < edgeAmount; i++) {
            edgeLines[i].setGeometry(new double[]{
                    x(edgeStart(i)), y(edgeStart(i)), 0,
                    x(edgeEnd(i)), y(edgeEnd(i)), 0
            });
        }
    }

    private double x(int i) {
        return vertices[i * 2];
    }

    private double y(int i) {
        return vertices[i * 2 + 1];
    }

    private double[] point(int i) {
        return new double[]{x(i), y(i)};
    }

    private int edgeStart(int i) {
        return edges[i * 2];
    }

    private int edgeEnd(int i) {
        return edges[i * 2 + 1];
    }

    private static double angle(double[] p1, double[] p2, double[] p3) {
        double a = Math.atan2(p3[1] - p2[1], p3[0] - p2[0])
                 - Math.atan2(p1[1] - p2[1], p1[0] - p2[0]);
        if (a < Math.PI) a += 2 * Math.PI;
        if (a >= Math.PI) a -= 2 * Math.PI;
        return a;
    }

    private static int side(double[] a, double[] b, double[] x) {
        double angle = angle(a, x, b);
        if (Math.PI - Math.abs(angle) < 0.1) return 0;
        return (int) - Math.signum(angle(a, x, b));
    }

    private int side(int i1, int i2, double[] x) {
        return side(point(i1), point(i2), x);
    }

    private double crossX(double[] a, double[] b, double y) {
        return (y - a[1]) / (b[1] - a[1]) * (b[0] - a[0]) + a[0];
    }
}
