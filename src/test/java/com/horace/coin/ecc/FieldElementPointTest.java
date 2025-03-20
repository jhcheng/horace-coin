package com.horace.coin.ecc;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class FieldElementPointTest {

    @Test
    public void testOnCurve() {
        final BigInteger prime = BigInteger.valueOf(223);
        final FieldElement a = new FieldElement(BigInteger.ZERO, prime);
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), prime);
        assertDoesNotThrow(() -> new FieldElementPoint(new FieldElement(BigInteger.valueOf(192), prime), new FieldElement(BigInteger.valueOf(105), prime), a, b));
        assertDoesNotThrow(() -> new FieldElementPoint(new FieldElement(BigInteger.valueOf(17), prime), new FieldElement(BigInteger.valueOf(56), prime), a, b));
        assertDoesNotThrow(() -> new FieldElementPoint(new FieldElement(BigInteger.valueOf(1), prime), new FieldElement(BigInteger.valueOf(193), prime), a, b));
        assertThrows(ArithmeticException.class, () -> new FieldElementPoint(new FieldElement(BigInteger.valueOf(200), prime), new FieldElement(BigInteger.valueOf(119), prime), a, b));
        assertThrows(ArithmeticException.class, () -> new FieldElementPoint(new FieldElement(BigInteger.valueOf(42), prime), new FieldElement(BigInteger.valueOf(99), prime), a, b));
    }

    @Test
    public void testAdd1() {
        final BigInteger prime = BigInteger.valueOf(223);
        final FieldElement a = new FieldElement(BigInteger.ZERO, prime);
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), prime);
        final FieldElement x1 = new FieldElement(BigInteger.valueOf(170), prime);
        final FieldElement y1 = new FieldElement(BigInteger.valueOf(142), prime);
        final FieldElement x2 = new FieldElement(BigInteger.valueOf(60), prime);
        final FieldElement y2 = new FieldElement(BigInteger.valueOf(139), prime);
        final FieldElementPoint p1 = new FieldElementPoint(x1, y1, a, b);
        final FieldElementPoint p2 = new FieldElementPoint(x2, y2, a, b);
        final FieldElementPoint answer = new FieldElementPoint(new FieldElement(BigInteger.valueOf(220), prime), new FieldElement(BigInteger.valueOf(181), prime), a, b);
        assertEquals(answer, p1.add(p2));
    }

    @Test
    public void testAdd2() {
        final BigInteger prime = BigInteger.valueOf(223);
        final FieldElement a = new FieldElement(BigInteger.ZERO, prime);
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), prime);
        final FieldElement x1 = new FieldElement(BigInteger.valueOf(143), prime);
        final FieldElement y1 = new FieldElement(BigInteger.valueOf(98), prime);
        final FieldElement x2 = new FieldElement(BigInteger.valueOf(76), prime);
        final FieldElement y2 = new FieldElement(BigInteger.valueOf(66), prime);
        final FieldElementPoint p1 = new FieldElementPoint(x1, y1, a, b);
        final FieldElementPoint p2 = new FieldElementPoint(x2, y2, a, b);
        final FieldElementPoint answer = new FieldElementPoint(new FieldElement(BigInteger.valueOf(47), prime), new FieldElement(BigInteger.valueOf(71), prime), a, b);
        assertEquals(answer, p1.add(p2));
    }

    @Test
    public void testAdd3() {
        final BigInteger prime = BigInteger.valueOf(223);
        final FieldElement a = new FieldElement(BigInteger.ZERO, prime);
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), prime);
        final FieldElement x1 = new FieldElement(BigInteger.valueOf(47), prime);
        final FieldElement y1 = new FieldElement(BigInteger.valueOf(71), prime);
        final FieldElement x2 = new FieldElement(BigInteger.valueOf(17), prime);
        final FieldElement y2 = new FieldElement(BigInteger.valueOf(56), prime);
        final FieldElementPoint p1 = new FieldElementPoint(x1, y1, a, b);
        final FieldElementPoint p2 = new FieldElementPoint(x2, y2, a, b);
        final FieldElementPoint answer = new FieldElementPoint(new FieldElement(BigInteger.valueOf(215), prime), new FieldElement(BigInteger.valueOf(68), prime), a, b);
        assertEquals(answer, p1.add(p2));
    }

    @Test
    void rmul1() {
        final BigInteger prime = BigInteger.valueOf(223);
        final FieldElement a = new FieldElement(BigInteger.ZERO, prime);
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), prime);
        final FieldElement x1 = new FieldElement(BigInteger.valueOf(192), prime);
        final FieldElement y1 = new FieldElement(BigInteger.valueOf(105), prime);
        final FieldElementPoint p = new FieldElementPoint(x1, y1, a, b);
        final FieldElementPoint answer = new FieldElementPoint(new FieldElement(BigInteger.valueOf(49), prime), new FieldElement(BigInteger.valueOf(71), prime), a, b);
        assertEquals(answer, p.add(p));
        assertEquals(answer, p.rmul(BigInteger.TWO));
    }

    @Test
    void rmul2() {
        final BigInteger prime = BigInteger.valueOf(223);
        final FieldElement a = new FieldElement(BigInteger.ZERO, prime);
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), prime);
        final FieldElement x1 = new FieldElement(BigInteger.valueOf(143), prime);
        final FieldElement y1 = new FieldElement(BigInteger.valueOf(98), prime);
        final FieldElementPoint p = new FieldElementPoint(x1, y1, a, b);
        final FieldElementPoint answer = new FieldElementPoint(new FieldElement(BigInteger.valueOf(64), prime), new FieldElement(BigInteger.valueOf(168), prime), a, b);
        assertEquals(answer, p.add(p));
        assertEquals(answer, p.rmul(BigInteger.TWO));
    }

    @Test
    void rmul3() {
        final BigInteger prime = BigInteger.valueOf(223);
        final FieldElement a = new FieldElement(BigInteger.ZERO, prime);
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), prime);
        final FieldElement x1 = new FieldElement(BigInteger.valueOf(47), prime);
        final FieldElement y1 = new FieldElement(BigInteger.valueOf(71), prime);
        final FieldElementPoint p = new FieldElementPoint(x1, y1, a, b);
        final FieldElementPoint answer2 = new FieldElementPoint(new FieldElement(BigInteger.valueOf(36), prime), new FieldElement(BigInteger.valueOf(111), prime), a, b);
        assertEquals(answer2, p.add(p));
        assertEquals(answer2, p.rmul(BigInteger.TWO));
        final FieldElementPoint answer4 = new FieldElementPoint(new FieldElement(BigInteger.valueOf(194), prime), new FieldElement(BigInteger.valueOf(51), prime), a, b);
        assertEquals(answer4, p.add(p).add(p).add(p));
        assertEquals(answer4, p.rmul(BigInteger.valueOf(4)));
        final FieldElementPoint answer8 = new FieldElementPoint(new FieldElement(BigInteger.valueOf(116), prime), new FieldElement(BigInteger.valueOf(55), prime), a, b);
        assertEquals(answer8, p.add(p).add(p).add(p).add(p).add(p).add(p).add(p));
        assertEquals(answer8, p.rmul(BigInteger.valueOf(8)));
    }

    @Test
    void rmul4() {
        final BigInteger prime = BigInteger.valueOf(223);
        final FieldElement a = new FieldElement(BigInteger.ZERO, prime);
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), prime);
        final FieldElement x1 = new FieldElement(BigInteger.valueOf(47), prime);
        final FieldElement y1 = new FieldElement(BigInteger.valueOf(71), prime);
        final FieldElementPoint p = new FieldElementPoint(x1, y1, a, b);
        final FieldElementPoint answer = new FieldElementPoint(x1.none(), y1.none(), a, b);
        assertEquals(answer, p.add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p).add(p));
        assertEquals(answer, p.rmul(BigInteger.valueOf(21)));
    }

}