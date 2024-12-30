package com.horace.coin.ecc;

public class FieldElement {

    private final long num;
    private final long prime;

    public FieldElement(final long num, final long prime) {
        this.num = num;
        this.prime = prime;
    }

    @Override
    public boolean equals(final FieldElement obj) {
        return false;
    }
}