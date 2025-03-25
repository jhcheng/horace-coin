package com.horace.coin.tx;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

import java.io.IOException;
import java.io.InputStream;

public class EndianUtils {

    public static byte[] intToLittleEndian(long value, int len) {
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) (value >> (8 * i));
        }
        return result;
    }

    public static long littleEndianToInt(byte[] bytes) {
        if (bytes.length == 1) return Byte.toUnsignedInt(bytes[0]);
        return BigIntegers.fromUnsignedByteArray(Arrays.reverse(bytes)).longValue();
    }

    public static long readVarInt(final InputStream in) throws IOException {
        final byte[] i = in.readNBytes(1);
        if (i[0] == (byte) 0xfd) return littleEndianToInt(in.readNBytes(2));
        if (i[0] == (byte) 0xfe) return littleEndianToInt(in.readNBytes(4));
        if (i[0] == (byte) 0xff) return littleEndianToInt(in.readNBytes(8));
        return Byte.toUnsignedLong(i[0]);
    }

    public static byte[] encodeVarInt(long value) {
        if (value < 0xfdL) return new byte[]{Long.valueOf(value).byteValue()};
        if (value < 0x10000L) return concat(new byte[]{(byte) 0xfd}, intToLittleEndian(value, 2));
        if (value < 0x100000000L) return concat(new byte[]{(byte) 0xfe}, intToLittleEndian(value, 4));
        if (value < Long.parseUnsignedLong("10000000000000000", 16))
            return concat(new byte[]{(byte) 0xff}, intToLittleEndian(value, 8));
        throw new ArithmeticException(String.format("integer too large: %d", value));
    }

    private static byte[] concat(final byte[] a, final byte[] b) {
        final byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}
