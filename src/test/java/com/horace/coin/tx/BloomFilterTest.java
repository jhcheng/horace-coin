package com.horace.coin.tx;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class BloomFilterTest {

    @Test
    void add() {
        BloomFilter bf = new BloomFilter(10, 5, 99);
        bf.add("Hello World".getBytes(StandardCharsets.UTF_8));
        assertEquals("0000000a080000000140", HexFormat.of().formatHex(bf.filter_bytes()));
        bf.add("Goodbye!".getBytes(StandardCharsets.UTF_8));
        assertEquals("4000600a080000010940", HexFormat.of().formatHex(bf.filter_bytes()));
    }

    @Test
    void test_filterload() {
        BloomFilter bf = new BloomFilter(10, 5, 99);
        bf.add("Hello World".getBytes(StandardCharsets.UTF_8));
        bf.add("Goodbye!".getBytes(StandardCharsets.UTF_8));
        assertEquals("0a4000600a080000010940050000006300000001", HexFormat.of().formatHex(bf.filterload().serialize()));
    }

}