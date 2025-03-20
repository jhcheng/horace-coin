package com.horace.coin.ecc;

import lombok.Getter;

import java.math.BigInteger;
import java.util.Objects;

public class FieldElementPoint {

    @Getter
    private final FieldElement x;
    @Getter
    private final FieldElement y;
    private final FieldElement a;
    private final FieldElement b;

    public FieldElementPoint(final FieldElement x, final FieldElement y, final FieldElement a, final FieldElement b) {
        this.x = x;
        this.y = y;
        this.a = a;
        this.b = b;
        if (!(x.equals(x.none()) && y.equals(y.none()))) {
            if (!onCurve(x, y, a, b)) throw new ArithmeticException(String.format("({}, {}) is not on the curve", x, y));
        }
    }

    public static boolean onCurve(final FieldElement x, final FieldElement y, final FieldElement a, final FieldElement b) {
        return y.mul(y).equals(x.mul(x).mul(x).add(x.mul(a)).add(b));
    }

    public FieldElementPoint add(final FieldElementPoint p) {
        if (!a.equals(p.a) || !b.equals(p.b)) {
            throw new ArithmeticException(String.format("({}, {}) are not on the same curve", this, p));
        }
        if (x.equals(x.none())) return p;
        if (p.x.equals(p.x.none())) return this;
        if (x.equals(p.x) && !y.equals(p.y)) return new FieldElementPoint(x.none(), y.none(), a, b);
        if (!x.equals(p.x)) {
            final FieldElement s = p.y.sub(y).div(p.x.sub(x));
            final FieldElement x3 = s.pow(BigInteger.TWO).sub(x).sub(p.x);
            final FieldElement y3 = s.mul(x.sub(x3)).sub(y);
            return new FieldElementPoint(x3, y3, a, b);
        }
        if (this.equals(p)) {
            if (y.equals(x.mul(0))) return new FieldElementPoint(x.none(), y.none(), a, b);
            final FieldElement s = x.pow(BigInteger.TWO).mul(3).add(a).div(y.mul(2));
            final FieldElement x3 = s.pow(BigInteger.TWO).sub(x.mul(2));
            final FieldElement y3 = s.mul(x.sub(x3)).sub(y);
            return new FieldElementPoint(x3, y3, a, b);
        }
        return new FieldElementPoint(x.none(), y.none(), a, b);
    }

    public FieldElementPoint rmul(final BigInteger coefficient) {
        BigInteger coef = new BigInteger(coefficient.toByteArray());
        FieldElementPoint current = this;
        FieldElementPoint result = new FieldElementPoint(current.x.none(), current.y.none(), a, b);
        while (coef.compareTo(BigInteger.ZERO) > 0) {
            if (coef.getLowestSetBit() == 0) {
                result = result.add(current);
            }
            current = current.add(current);
            coef = coef.shiftRight(1);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FieldElementPoint that = (FieldElementPoint) o;
        return Objects.equals(x, that.x) && Objects.equals(y, that.y) && Objects.equals(a, that.a) && Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, a, b);
    }

    @Override
    public String toString() {
        return "FieldElementPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
