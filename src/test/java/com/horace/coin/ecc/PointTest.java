package com.horace.coin.ecc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void add01() {
        final Point a = new Point(2, 5);
        final Point b = new Point(-1, -1);
        final Point c = new Point(3, -7);
        assertEquals(c, a.add(b));
    }

    @Test
    void add02() {
        final Point a = new Point(-1, -1);
        final Point b = new Point(-1, -1);
        final Point c = new Point(18, 77);
        assertEquals(c, a.add(b));
    }

    @Test
    void onCurve() {
        assertFalse(Point.onCurve(2, 4));
        assertTrue(Point.onCurve(-1, -1));
        assertTrue(Point.onCurve(18, 77));
        assertFalse(Point.onCurve(5, 7));
    }
}