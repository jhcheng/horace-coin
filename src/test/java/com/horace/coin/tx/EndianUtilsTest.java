package com.horace.coin.tx;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class EndianUtilsTest {

    HexFormat hexFormat = HexFormat.of();

    @Test
    void intToLittleEndian_01() {
        assertArrayEquals(new byte[]{0x01, 0x00, 0x00, 0x00}, EndianUtils.intToLittleEndian(1, 4));
    }

    @Test
    void intToLittleEndian_02() {
        assertArrayEquals(new byte[]{(byte) 0x99, (byte) 0xc3, (byte) 0x98, 0x00, 0x00, 0x00, 0x00, 0x00}, EndianUtils.intToLittleEndian(10011545, 8));
    }

    @Test
    void intToLittleEndian_03() {
        assertArrayEquals(new byte[]{(byte) 0xfd, (byte) 0xff, (byte) 0x00}, EndianUtils.encodeVarInt(255));
    }

    @Test
    void littleEndianToInt_01() {
        assertEquals(10011545, EndianUtils.littleEndianToInt(hexFormat.parseHex("99c3980000000000")).intValue());
    }

    @Test
    void littleEndianToInt_02() {
        assertEquals(32454049, EndianUtils.littleEndianToInt(hexFormat.parseHex("a135ef0100000000")).intValue());
    }

    @Test
    void littleEndianToInt_03() {
        assertEquals(4294967295L, EndianUtils.littleEndianToInt(hexFormat.parseHex("ffffffff")).longValue());
    }

}