package com.horace.coin.ecc;

public record FieldElement(long num, long prime) {

    public FieldElement add(final FieldElement other) {
        if (prime != other.prime) throw new ArithmeticException("Cannot add two numbers in different Fields");
        return new FieldElement((num + other.num) % prime, prime);
    }

    public FieldElement sub(final FieldElement other) {
        if (prime != other.prime) throw new ArithmeticException("Cannot sub two numbers in different Fields");
        return new FieldElement((num - other.num) % prime, prime);
    }

    public FieldElement mul(final FieldElement other) {
        if (prime != other.prime) throw new ArithmeticException("Cannot sub two numbers in different Fields");
        return new FieldElement((num * other.num) % prime, prime);
    }

    public FieldElement pow(final int exponent) {
        //return new FieldElement((long) (Math.pow(num, exponent) % prime), prime);
        final int n = Math.toIntExact(Math.floorMod(exponent, prime - 1));
        return new FieldElement((long) (Math.pow(num, n) % prime), prime);
    }

    public FieldElement div(final FieldElement other) {
        if (prime != other.prime) throw new ArithmeticException("Cannot sub two numbers in different Fields");
        return mul(other.pow(Math.toIntExact(prime - 2)));
    }

    @Override
    public String toString() {
        return "FieldElement{" +
                "num=" + num +
                ", prime=" + prime +
                '}';
    }
}