package com.horace.coin.ecc;

import java.util.Objects;

public class Point {

    private final double x;
    private final double y;
    private final double a;
    private final double b;

    public Point(final double x, final double y, final double a, final double b) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;
        if (!onCurve(x, y, a, b)) throw new ArithmeticException(String.format("({}, {}) is not on the curve", x, y));
    }

    public Point(final double x, final double y) {
        this(x, y, 5, 7);
    }

    public static boolean onCurve(final double x, final double y, final double a, final double b) {
        return y * y == x * x * x + a * x + b;
    }

    public static boolean onCurve(final double x, final double y) {
        return onCurve(x, y, 5, 7);
    }

    public Point add(final Point p) {
        if (a != p.a || b != p.b) {
            throw new ArithmeticException(String.format("({}, {}) are not on the same curve", this, p));
        }
        if (x == Double.NaN) return p;
        if (p.x == Double.NaN) return this;
        if (x == p.x && y != p.y) return new Point(Double.NaN, Double.NaN);
        if (x != p.x) {
            double s = (p.y - y) / (p.x - x);
            double x3 = s * s - x - p.x;
            double y3 = s * (x - x3) - y;
            return new Point(x3, y3, a, b);
        }
        if (this.equals(p)) {
            if (y == 0 * x) return new Point(Double.NaN, Double.NaN);
            double s = (3 * x * x + a) / (2 * y);
            double x3 = s * s - 2 * x;
            double y3 = s * (x - x3) - y;
            return new Point(x3, y3, a, b);
        }
        return new Point(Double.NaN, Double.NaN);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(x, point.x) == 0 && Double.compare(y, point.y) == 0 && Double.compare(a, point.a) == 0 && Double.compare(b, point.b) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, a, b);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
