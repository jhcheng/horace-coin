package com.horace.coin;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelperTest {

    @Test
    void encode() {
        assertEquals("9MA8fRQrT4u8Zj8ZRd6MAiiyaxb2Y1CMpvVkHQu5hVM6", Helper.encode_base58(Hex.decode("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d")));
        assertEquals("4fE3H2E6XMp4SsxtwinF7w9a34ooUrwWe4WsW1458Pd", Helper.encode_base58(Hex.decode("eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c")));
        assertEquals("EQJsjkd6JaGwxrjEhfeqPenqHwrBmPQZjJGNSCHBkcF7", Helper.encode_base58(Hex.decode("c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab6")));
    }

    @Test
    void hash160() {
        assertEquals("9cb1656f99c65ce3be73ddef9041b9bbeaa42369", Hex.toHexString(Helper.hash160(Hex.decode("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d"))));
    }

    @Test
    void test_decode_base58() {
        String addr = "mnrVtF8DWjMu839VW3rBfgYaAfKk8983Xf";
        String h160 = HexFormat.of().formatHex(Helper.decode_base58(addr));
        assertEquals("507b27411ccf7f16f10297de6cef3f291623eddf", h160);
        assertEquals(addr, Helper.encode_base58_checksum(Arrays.concatenate(new byte[]{0x6f}, HexFormat.of().parseHex(h160))));
    }

    @Test
    void test_decode_base58_02() {
        assertEquals("62e907b15cbf27d5425399ebf6f0fb50ebb88f18", HexFormat.of().formatHex(Helper.decode_base58("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa")));
    }

    @Test
    void test_decode_base58_03() {
        assertEquals("1ec51b3654c1f1d0f4929d11a1f702937eaf50c8", HexFormat.of().formatHex(Helper.decode_base58("miKegze5FQNCnGw6PKyqUbYUeBa4x2hFeM")));
    }

}