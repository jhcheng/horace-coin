package com.horace.coin.network;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class VersionMessageTest {

    @Test
    void serialize() {
        VersionMessage v = VersionMessage.builder().timestamp(0).nonce(new byte[8]).build();
        assertEquals("7f11010000000000000000000000000000000000000000000000000000000000000000000000ffff00000000208d000000000000000000000000000000000000ffff00000000208d0000000000000000182f70726f6772616d6d696e67626974636f696e3a302e312f0000000000",
                HexFormat.of().formatHex(v.serialize()));
    }
}