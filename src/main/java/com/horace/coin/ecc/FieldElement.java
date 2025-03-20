package com.horace.coin.ecc;

import lombok.Getter;

import java.math.BigInteger;
import java.util.Objects;

public class FieldElement {

    @Getter
    private final BigInteger num;
    @Getter
    private final BigInteger prime;

    public FieldElement(final BigInteger num, final BigInteger prime) {
        this.num = num;
        this.prime = prime;
    }

    public FieldElement none() {
        return new FieldElement(null, prime);
    }

    public FieldElement add(final FieldElement other) {
        if (!prime.equals(other.prime)) throw new ArithmeticException("Cannot add two numbers in different Fields");
        return new FieldElement(num.add(other.num).mod(prime), prime);
    }

    public FieldElement sub(final FieldElement other) {
        if (!prime.equals(other.prime)) throw new ArithmeticException("Cannot sub two numbers in different Fields");
        return new FieldElement(num.subtract(other.num).mod(prime), prime);
    }

    public FieldElement mul(final int scalar) {
        if (scalar == 0) return new FieldElement(BigInteger.ZERO, prime);
        if (scalar == 1) return this;
        FieldElement result = this;
        for (int i = 1; i < scalar; i++) {
            result = result.add(this);
        }
        return result;
    }

    public FieldElement mul(final FieldElement other) {
        if (!prime.equals(other.prime)) throw new ArithmeticException("Cannot sub multiply numbers in different Fields");
        return new FieldElement(num.multiply(other.num).mod(prime), prime);
    }

    public FieldElement pow(final BigInteger exponent) {
        final BigInteger n = exponent.mod(prime.subtract(BigInteger.ONE));
        return new FieldElement(num.modPow(n, prime), prime);
    }

    public FieldElement div(final FieldElement other) {
        if (!prime.equals(other.prime)) throw new ArithmeticException("Cannot divide two numbers in different Fields");
        return mul(other.pow(prime.subtract(BigInteger.TWO)));
    }

    public FieldElement sqrt() {
        return pow(prime.add(BigInteger.ONE).divide(BigInteger.valueOf(4)));
    }

    @Override
    public String toString() {
        return "FieldElement{" +
                "num=" + num +
                ", prime=" + prime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FieldElement that = (FieldElement) o;
        return Objects.equals(num, that.num) && Objects.equals(prime, that.prime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, prime);
    }
}