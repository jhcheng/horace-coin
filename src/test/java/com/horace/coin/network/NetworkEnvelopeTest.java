package com.horace.coin.network;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class NetworkEnvelopeTest {

    @Test
    void parse_01() {
        NetworkEnvelope envelope = NetworkEnvelope.parse(HexFormat.of().parseHex("f9beb4d976657261636b000000000000000000005df6e0e2"));
        assertEquals("verack", envelope.getCommand());
        assertEquals(0, envelope.getPayload().length);
    }

    @Test
    void parse_02() {
        byte[] msg = HexFormat.of().parseHex("f9beb4d976657273696f6e0000000000650000005f1a69d2721101000100000000000000bc8f5e5400000000010000000000000000000000000000000000ffffc61b6409208d010000000000000000000000000000000000ffffcb0071c0208d128035cbc97953f80f2f5361746f7368693a302e392e332fcf05050001");
        NetworkEnvelope envelope = NetworkEnvelope.parse(msg);
        assertEquals("version", envelope.getCommand());
        assertArrayEquals(Arrays.copyOfRange(msg, 24, msg.length), envelope.getPayload());
    }

}