package com.horace.coin.ecc;

import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignatureTest {

    @Test
    void der() {
        final Signature signature = new Signature(
                new BigInteger("37206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c6", 16),
                new BigInteger("8ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec", 16));
        assertEquals("3045022037206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c60221008ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec", Hex.toHexString(signature.der()));
    }

    @SneakyThrows
    @Test
    void parse_01() {
        Signature signature = new Signature(BigInteger.valueOf(1), BigInteger.valueOf(2));
        Signature signature2 = Signature.parse(signature.der());
        assertEquals(signature, signature2);
    }

    @SneakyThrows
    @Test
    void parse_02() {
        Random random = new Random();
        Signature signature = new Signature(BigInteger.valueOf(random.nextLong(0, 2^255)), BigInteger.valueOf(random.nextLong(0, 2^255)));
        Signature signature2 = Signature.parse(signature.der());
        assertEquals(signature, signature2);
    }

    @SneakyThrows
    @Test
    void parse_03() {
        Random random = new Random();
        Signature signature = new Signature(BigInteger.valueOf(random.nextLong(0, 2^255)), BigInteger.valueOf(random.nextLong(0, 2^255)));
        Signature signature2 = Signature.parse(signature.der());
        assertEquals(signature, signature2);
    }

}