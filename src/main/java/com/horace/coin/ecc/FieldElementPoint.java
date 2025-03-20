package com.horace.coin.ecc;

public class FEPoint {

    private final FieldElement x;
    private final FieldElement y;
    private final FieldElement a;
    private final FieldElement b;

    public FEPoint(FieldElement x, FieldElement y, FieldElement a, FieldElement b) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;
    }
}
