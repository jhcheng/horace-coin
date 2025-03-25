package com.horace.coin.ecc;

import lombok.SneakyThrows;
import org.bouncycastle.util.BigIntegers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public record Signature(BigInteger r, BigInteger s) {

    public byte[] der() {
        byte[] rBin = r.toByteArray();
        byte[] sBin = s.toByteArray();

        // Strip leading zeros if necessary
        rBin = stripLeadingZeros(rBin);
        sBin = stripLeadingZeros(sBin);

        // Ensure the first byte does not have its high bit set (avoiding negative interpretation)
        if ((rBin[0] & 0x80) != 0) {
            rBin = prependZero(rBin);
        }
        if ((sBin[0] & 0x80) != 0) {
            sBin = prependZero(sBin);
        }

        // Create DER sequence
        byte[] rPart = encodeInteger(rBin);
        byte[] sPart = encodeInteger(sBin);
        byte[] result = new byte[2 + rPart.length + sPart.length];

        result[0] = 0x30; // ASN.1 Sequence tag
        result[1] = (byte) (rPart.length + sPart.length); // Length
        System.arraycopy(rPart, 0, result, 2, rPart.length);
        System.arraycopy(sPart, 0, result, 2 + rPart.length, sPart.length);

        return result;
    }

    private byte[] stripLeadingZeros(byte[] bytes) {
        int start = 0;
        while (start < bytes.length - 1 && bytes[start] == 0) {
            start++;
        }
        return Arrays.copyOfRange(bytes, start, bytes.length);
    }

    private byte[] prependZero(byte[] bytes) {
        byte[] extended = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, extended, 1, bytes.length);
        return extended;
    }

    private byte[] encodeInteger(byte[] value) {
        byte[] encoded = new byte[value.length + 2];
        encoded[0] = 0x02; // ASN.1 Integer tag
        encoded[1] = (byte) value.length; // Length
        System.arraycopy(value, 0, encoded, 2, value.length);
        return encoded;
    }

    /*
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
     */

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

    public static Signature parse(byte[] derSignature) throws IOException {
        try (final ByteArrayInputStream s = new ByteArrayInputStream(derSignature)) {
            int compound = s.read();
            if (compound != 0x30) {
                throw new IllegalArgumentException("Bad Signature");
            }

            int length = s.read();
            if (length + 2 != derSignature.length) {
                throw new IllegalArgumentException("Bad Signature Length");
            }

            int marker = s.read();
            if (marker != 0x02) {
                throw new IllegalArgumentException("Bad Signature");
            }

            int rlength = s.read();
            byte[] rBytes = new byte[rlength];
            if (s.read(rBytes) != rlength) {
                throw new IllegalArgumentException("Bad Signature");
            }
            BigInteger r = new java.math.BigInteger(1, rBytes); // Convert to positive integer

            marker = s.read();
            if (marker != 0x02) {
                throw new IllegalArgumentException("Bad Signature");
            }

            int slength = s.read();
            byte[] sBytes = new byte[slength];
            if (s.read(sBytes) != slength) {
                throw new IllegalArgumentException("Bad Signature");
            }
            BigInteger sValue = new java.math.BigInteger(1, sBytes);

            if (derSignature.length != 6 + rlength + slength) {
                throw new IllegalArgumentException("Signature too long");
            }

            return new Signature(r, sValue);
        }
    }

}
