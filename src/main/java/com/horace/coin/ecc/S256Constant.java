package com.horace.coin.ecc;

import java.math.BigInteger;

public interface S256Constant {

    S256Field A = new S256Field(BigInteger.ZERO);
    S256Field B = new S256Field(BigInteger.valueOf(7));
    BigInteger N = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);
    S256Point G = new S256Point(
            new BigInteger("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798", 16),
            new BigInteger("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8", 16)
    );


}
