package com.horace.coin.tx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EndianUtils {

    public static byte[] intToLittleEndian(long value, int len) {
        final byte[] array = new byte[len];
        final ByteBuffer bf = ByteBuffer.allocate(8);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        bf.putLong(value);
        bf.flip();
        bf.get(array);
        return array;
    }

    public static long littleEndianToInt(byte[] bytes) {
        if (bytes.length == 1) return Byte.toUnsignedInt(bytes[0]);
        final ByteBuffer bf = ByteBuffer.allocate(8);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        bf.put(bytes);
        bf.flip();
        if (bytes.length == 4) return bf.getInt();
        if (bytes.length == 2) return bf.getShort();
        return bf.getLong();
    }

    public static long readVarInt(final InputStream in) throws IOException {
        final byte[] i = in.readNBytes(1);
        if (i[0] == (byte) 0xfd) return littleEndianToInt(in.readNBytes(2));
        if (i[0] == (byte) 0xfe) return littleEndianToInt(in.readNBytes(4));
        if (i[0] == (byte) 0xff) return littleEndianToInt(in.readNBytes(8));
        return Byte.valueOf(i[0]).longValue();
    }

    public static byte[] encodeVarInt(long value) {
        if (value < 0xfdL) return new byte[]{Long.valueOf(value).byteValue()};
        if (value < 0x10000L) return concat(new byte[]{(byte) 0xfd}, intToLittleEndian(value, 2));
        if (value < 0x100000000L) return concat(new byte[]{(byte) 0xfe}, intToLittleEndian(value, 4));
        if (value < Long.parseUnsignedLong("0x10000000000000000", 16)) return concat(new byte[]{(byte) 0xfe}, intToLittleEndian(value, 8));
        throw new ArithmeticException(String.format("integer too large: %d", value));
    }

    private static byte[] concat(final byte[] a, final byte[] b) {
        final byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}
