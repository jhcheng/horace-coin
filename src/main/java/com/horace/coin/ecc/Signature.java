package com.horace.coin.ecc;

import lombok.SneakyThrows;
import org.bouncycastle.util.BigIntegers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public record Signature(BigInteger r, BigInteger s) {

    public byte[] der() {
        byte[] rbin = buildValueArray(r);
        byte[] sbin = buildValueArray(s);
        byte[] result = new byte[2 + rbin.length + sbin.length];
        result[0] = 0x30;
        result[1] = Integer.valueOf(rbin.length + sbin.length).byteValue();
        System.arraycopy(rbin, 0, result, 2, rbin.length);
        System.arraycopy(sbin, 0, result, rbin.length + 2, sbin.length);
        return result;
    }

    @SneakyThrows
    private byte[] buildValueArray(final BigInteger value) {
        byte[] vbin = BigIntegers.asUnsignedByteArray(32, value);
        vbin = lstrip(vbin, (byte) 0x00);
        if ((vbin[0] & 0xFF) >=  0x80) {
            byte[] tmp = new byte[vbin.length + 1];
            tmp[0] = 0x00;
            System.arraycopy(vbin, 0, tmp, 1, vbin.length);
            vbin = tmp;
        }
        byte[] result = new byte[2 + vbin.length];
        result[0] = 0x02;
        result[1] = Integer.valueOf(vbin.length).byteValue();
        System.arraycopy(vbin, 0, result, 2, vbin.length);
        return result;
    }

    private byte[] lstrip(byte[] bytes, byte b) throws IOException {
        try (final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
            boolean startCopy = false;
            for (byte a : bytes) {
                if (startCopy) arrayOutputStream.write(new byte[]{a});
                else if (a != b && !startCopy) {
                    arrayOutputStream.write(new byte[]{a});
                    startCopy = true;
                }
            }
            return arrayOutputStream.toByteArray();
        }
    }

}
