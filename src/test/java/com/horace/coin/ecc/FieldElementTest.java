package com.horace.coin.ecc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldElementTest {

    @Test
    public void testEquals() {
        final FieldElement a = new FieldElement(7, 13);
        final FieldElement b = new FieldElement(6, 13);
        assertEquals(a, a);
        assertNotEquals(a, b);
    }

    @Test
    public void add() {
        final FieldElement a = new FieldElement(7, 13);
        final FieldElement b = new FieldElement(12, 13);
        final FieldElement c = new FieldElement(6, 13);
        assertEquals(a.add(b), c);
    }

    @Test
    void sub() {
        final FieldElement a = new FieldElement(11, 19);
        final FieldElement b = new FieldElement(9, 19);
        final FieldElement c = new FieldElement(2, 19);
        assertEquals(a.sub(b), c);
    }

    @Test
    void mul() {
        final FieldElement a = new FieldElement(3, 13);
        final FieldElement b = new FieldElement(12, 13);
        final FieldElement c = new FieldElement(10, 13);
        assertEquals(a.mul(b), c);
    }

    @Test
    void mul19() {
        final FieldElement a = new FieldElement(5, 19);
        final FieldElement b = new FieldElement(3, 19);
        final FieldElement c = new FieldElement(15, 19);
        assertEquals(a.mul(b), c);
    }

    @Test
    void pow1() {
        final FieldElement a = new FieldElement(3, 13);
        final FieldElement b = new FieldElement(1, 13);
        assertEquals(a.pow(3), b);
    }

    @Test
    void pow2() {
        final FieldElement a = new FieldElement(7, 13);
        final FieldElement b = new FieldElement(8, 13);
        assertEquals(a.pow(-3), b);
    }

    @Test
    void div1() {
        final FieldElement a = new FieldElement(2, 19);
        final FieldElement b = new FieldElement(7, 19);
        final FieldElement c = new FieldElement(3, 19);
        assertEquals(a.div(b), c);
    }

    @Test
    void div2() {
        final FieldElement a = new FieldElement(7, 19);
        final FieldElement b = new FieldElement(5, 19);
        final FieldElement c = new FieldElement(9, 19);
        assertEquals(a.div(b), c);
    }
}