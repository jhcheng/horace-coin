package com.horace.coin.ecc;

import lombok.SneakyThrows;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class Helper {

    private static String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    @SneakyThrows
    public static byte[] hash256(byte[] input) {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(digest.digest(input));
    }

    @SneakyThrows
    public static byte[] hash160(byte[] input) {
        final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        final RIPEMD160.Digest digest = new RIPEMD160.Digest();
        return digest.digest(sha256.digest(input));
    }

    public static String encode_base58(final byte[] s) {
        int count = 0;
        for (byte c : s) {
            if (c == 0) count++;
            else break;
        }
        BigInteger num = BigIntegers.fromUnsignedByteArray(s);
        String[] prefix = new String[count];
        Arrays.fill(prefix, "1");
        final StringBuffer sb = new StringBuffer();
        while (num.compareTo(BigInteger.ZERO) > 0) {
            final BigInteger[] divmod = num.divideAndRemainder(BigInteger.valueOf(58));
            sb.insert(0, BASE58_ALPHABET.charAt(divmod[1].intValueExact()));
            num = divmod[0];
        }
        sb.insert(0, String.join("", prefix));
        return sb.toString();
    }

    public static String encode_base58_checksum(final byte[] b) {
        final byte[] temp = new byte[b.length + 4];
        System.arraycopy(b, 0, temp, 0, b.length);
        System.arraycopy(hash256(b), 0, temp, b.length, 4);
        return encode_base58(temp);
    }

}
