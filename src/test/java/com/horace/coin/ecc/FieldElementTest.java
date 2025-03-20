package com.horace.coin.ecc;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FieldElementTest {

    @Test
    public void testEquals() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(13));
        final FieldElement b = new FieldElement(BigInteger.valueOf(6), BigInteger.valueOf(13));
        assertEquals(a, a);
        assertNotEquals(a, b);
    }

    @Test
    public void add() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(13));
        final FieldElement b = new FieldElement(BigInteger.valueOf(12), BigInteger.valueOf(13));
        final FieldElement c = new FieldElement(BigInteger.valueOf(6), BigInteger.valueOf(13));
        assertEquals(a.add(b), c);
    }

    @Test
    void sub() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(11), BigInteger.valueOf(19));
        final FieldElement b = new FieldElement(BigInteger.valueOf(9), BigInteger.valueOf(19));
        final FieldElement c = new FieldElement(BigInteger.valueOf(2), BigInteger.valueOf(19));
        assertEquals(a.sub(b), c);
    }

    @Test
    void mul() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(3), BigInteger.valueOf(13));
        final FieldElement b = new FieldElement(BigInteger.valueOf(12), BigInteger.valueOf(13));
        final FieldElement c = new FieldElement(BigInteger.valueOf(10), BigInteger.valueOf(13));
        assertEquals(a.mul(b), c);
    }

    @Test
    void mul19() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(5), BigInteger.valueOf(19));
        final FieldElement b = new FieldElement(BigInteger.valueOf(3), BigInteger.valueOf(19));
        final FieldElement c = new FieldElement(BigInteger.valueOf(15), BigInteger.valueOf(19));
        assertEquals(a.mul(b), c);
    }

    @Test
    void pow1() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(3), BigInteger.valueOf(13));
        final FieldElement b = new FieldElement(BigInteger.valueOf(1), BigInteger.valueOf(13));
        assertEquals(a.pow(BigInteger.valueOf(3)), b);
    }

    @Test
    void pow2() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(13));
        final FieldElement b = new FieldElement(BigInteger.valueOf(8), BigInteger.valueOf(13));
        assertEquals(a.pow(BigInteger.valueOf(-3)), b);
    }

    @Test
    void div1() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(2), BigInteger.valueOf(19));
        final FieldElement b = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(19));
        final FieldElement c = new FieldElement(BigInteger.valueOf(3), BigInteger.valueOf(19));
        assertEquals(a.div(b), c);
    }

    @Test
    void div2() {
        final FieldElement a = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(19));
        final FieldElement b = new FieldElement(BigInteger.valueOf(5), BigInteger.valueOf(19));
        final FieldElement c = new FieldElement(BigInteger.valueOf(9), BigInteger.valueOf(19));
        assertEquals(a.div(b), c);
    }

}