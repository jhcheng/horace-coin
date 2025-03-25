package com.horace.coin.ecc;

import com.horace.coin.Helper;
import lombok.SneakyThrows;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.nio.ByteBuffer;

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
        vbin = Helper.lstrip(vbin, (byte) 0x00);
        if ((vbin[0] & 0xFF) >= 0x80) {
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

    public static Signature parse(byte[] derSignature) {
        ByteBuffer buffer = ByteBuffer.wrap(derSignature);
        int compound = buffer.get();
        if (compound != 0x30) {
            throw new IllegalArgumentException("Bad Signature");
        }

        int length = buffer.get();
        if (length + 2 != derSignature.length) {
            throw new IllegalArgumentException("Bad Signature Length");
        }

        int marker = buffer.get();
        if (marker != 0x02) {
            throw new IllegalArgumentException("Bad Signature");
        }

        int rlength = buffer.get();
        byte[] rBytes = new byte[rlength];
        buffer.get(rBytes);
            /*
            if (buffer.get(rBytes) != rlength) {
                throw new IllegalArgumentException("Bad Signature");
            }
             */
        BigInteger r = new java.math.BigInteger(1, rBytes); // Convert to positive integer

        marker = buffer.get();
        if (marker != 0x02) {
            throw new IllegalArgumentException("Bad Signature");
        }

        int slength = buffer.get();
        byte[] sBytes = new byte[slength];
        buffer.get(sBytes);
            /*
            if (s.read(sBytes) != slength) {
                throw new IllegalArgumentException("Bad Signature");
            }
             */
        BigInteger sValue = new java.math.BigInteger(1, sBytes);

        if (derSignature.length != 6 + rlength + slength) {
            throw new IllegalArgumentException("Signature too long");
        }

        return new Signature(r, sValue);

    }

}
