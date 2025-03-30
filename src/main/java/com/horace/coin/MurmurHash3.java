package com.horace.coin;

import java.math.BigInteger;

public class MurmurHash3 {

    public static BigInteger murmurhash3_x86_32(byte[] key, BigInteger seed) {
        return murmurhash3_x86_32(key, 0, key.length, seed);
    }

    public static BigInteger murmurhash3_x86_32(byte[] key, int offset, int length, BigInteger seed) {
        BigInteger C1 = new BigInteger("CC9E2D51", 16);
        BigInteger C2 = new BigInteger("1B873593", 16);
        BigInteger M = new BigInteger("4294967295", 10); // 2^32 - 1

        BigInteger h1 = seed.and(M); // Ensure seed is 32-bit

        int roundedEnd = offset + (length & 0xfffffffc);

        for (int i = offset; i < roundedEnd; i += 4) {
            BigInteger k1 = BigInteger.valueOf(
                    (key[i] & 0xFF) |
                            ((key[i + 1] & 0xFF) << 8) |
                            ((key[i + 2] & 0xFF) << 16) |
                            ((key[i + 3]) << 24)
            ).and(M);

            k1 = k1.multiply(C1).and(M);
            k1 = k1.shiftLeft(15).or(k1.shiftRight(17)).and(M);
            k1 = k1.multiply(C2).and(M);

            h1 = h1.xor(k1).and(M);
            h1 = h1.shiftLeft(13).or(h1.shiftRight(19)).and(M);
            h1 = h1.multiply(BigInteger.valueOf(5)).add(new BigInteger("E6546B64", 16)).and(M);
        }

        // tail
        BigInteger k1 = BigInteger.ZERO;

        switch (length & 3) {
            case 3:
                k1 = k1.xor(BigInteger.valueOf((key[roundedEnd + 2] & 0xFF) << 16));
            case 2:
                k1 = k1.xor(BigInteger.valueOf((key[roundedEnd + 1] & 0xFF) << 8));
            case 1:
                k1 = k1.xor(BigInteger.valueOf(key[roundedEnd] & 0xFF));

                k1 = k1.multiply(C1).and(M);
                k1 = k1.shiftLeft(15).or(k1.shiftRight(17)).and(M);
                k1 = k1.multiply(C2).and(M);
                h1 = h1.xor(k1).and(M);
        }

        // finalization
        h1 = h1.xor(BigInteger.valueOf(length)).and(M);

        h1 = h1.xor(h1.shiftRight(16)).and(M);
        h1 = h1.multiply(new BigInteger("85EBCA6B", 16)).and(M);
        h1 = h1.xor(h1.shiftRight(13)).and(M);
        h1 = h1.multiply(new BigInteger("C2B2AE35", 16)).and(M);
        h1 = h1.xor(h1.shiftRight(16)).and(M);

        return h1;
    }

}
