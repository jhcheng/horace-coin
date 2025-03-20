package com.horace.coin.ecc;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrivateKeyTest {

    @Test
    void wif1() {
        final PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(5003));
        assertEquals("cMahea7zqjxrtgAbB7LSGbcQUr1uX1ojuat9jZodMN8rFTv2sfUK", privateKey.wif(true, true));
    }

    @Test
    void wif2() {
        final PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(2021).pow(5));
        assertEquals("91avARGdfge8E4tZfYLoxeJ5sGBdNJQH4kvjpWAxgzczjbCwxic", privateKey.wif(false, true));
    }

    @Test
    void wif3() {
        final PrivateKey privateKey = new PrivateKey(new BigInteger("54321deadbeef", 16));
        assertEquals("KwDiBf89QgGbjEhKnhXJuH7LrciVrZi3qYjgiuQJv1h8Ytr2S53a", privateKey.wif(true, false));
    }

}