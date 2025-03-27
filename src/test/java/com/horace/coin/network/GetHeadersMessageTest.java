package com.horace.coin.network;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class GetHeadersMessageTest {

    @Test
    void serialize() {
        GetHeadersMessage gh = GetHeadersMessage.builder()
                .start_block(HexFormat.of().parseHex("0000000000000000001237f46acddf58578a37e213d2a6edc4884a2fcad05ba3"))
                .build();
        assertEquals("7f11010001a35bd0ca2f4a88c4eda6d213e2378a5758dfcd6af437120000000000000000000000000000000000000000000000000000000000000000000000000000000000",
                HexFormat.of().formatHex(gh.serialize()));
    }
}