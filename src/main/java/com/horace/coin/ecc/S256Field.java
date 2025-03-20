package com.horace.coin.ecc;

import java.math.BigInteger;

public class S256Field extends FieldElement {

    private static final BigInteger P = BigInteger.TWO.pow(256).subtract(BigInteger.TWO.pow(32)).subtract(BigInteger.valueOf(977));

    public S256Field(final BigInteger num) {
        super(num, P);
    }

}
