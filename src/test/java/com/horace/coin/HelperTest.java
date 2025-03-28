package com.horace.coin;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

    @Test
    void test_p2pkh_address() {
        byte[] h160 = HexFormat.of().parseHex("74d691da1574e6b3c192ecfb52cc8984ee7b6c56");
        assertEquals("1BenRpVUFK65JFWcQSuHnJKzc4M8ZP8Eqa", Helper.h160_to_p2pkh_address(h160, false));
        assertEquals("mrAjisaT4LXL5MzE81sfcDYKU3wqWSvf9q", Helper.h160_to_p2pkh_address(h160, true));
    }

    @Test
    void test_p2sh_address() {
        byte[] h160 = HexFormat.of().parseHex("74d691da1574e6b3c192ecfb52cc8984ee7b6c56");
        assertEquals("3CLoMMyuoDQTPRD3XYZtCvgvkadrAdvdXh", Helper.h160_to_p2sh_address(h160, false));
        assertEquals("2N3u1R6uwQfuobCqbCgBkpsgBxvr1tZpe7B", Helper.h160_to_p2sh_address(h160, true));
    }

    @Test
    void bits_to_target() {
        BigInteger target = Helper.bitsToTarget(HexFormat.of().parseHex("e93c0118"));
        byte[] filledBytes = new byte[32];
        byte[] targetBytes = target.toByteArray();
        System.arraycopy(target.toByteArray(), 0, filledBytes, 32 - targetBytes.length, targetBytes.length);
        assertArrayEquals(HexFormat.of().parseHex("0000000000000000013ce9000000000000000000000000000000000000000000"),
                filledBytes);
    }

    @Test
    void test_calculate_new_bits() {
        byte[] prev_bits = HexFormat.of().parseHex("54d80118");
        long time_differential = 302400;
        assertArrayEquals(HexFormat.of().parseHex("00157617"), Helper.calculate_new_bits(prev_bits, time_differential));
    }

    @Test
    void test_merkle_parent() {
        assertArrayEquals(HexFormat.of().parseHex("8b30c5ba100f6f2e5ad1e2a742e5020491240f8eb514fe97c713c31718ad7ecd"),
                Helper.merkle_parent(HexFormat.of().parseHex("c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5"),
                        HexFormat.of().parseHex("c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5")));
    }

    @Test
    void test_merkle_parent_level() {
        byte[][] hashes = new byte[][]{
                HexFormat.of().parseHex("c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5"),
                HexFormat.of().parseHex("c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5"),
                HexFormat.of().parseHex("f391da6ecfeed1814efae39e7fcb3838ae0b02c02ae7d0a5848a66947c0727b0"),
                HexFormat.of().parseHex("3d238a92a94532b946c90e19c49351c763696cff3db400485b813aecb8a13181"),
                HexFormat.of().parseHex("10092f2633be5f3ce349bf9ddbde36caa3dd10dfa0ec8106bce23acbff637dae"),
                HexFormat.of().parseHex("7d37b3d54fa6a64869084bfd2e831309118b9e833610e6228adacdbd1b4ba161"),
                HexFormat.of().parseHex("8118a77e542892fe15ae3fc771a4abfd2f5d5d5997544c3487ac36b5c85170fc"),
                HexFormat.of().parseHex("dff6879848c2c9b62fe652720b8df5272093acfaa45a43cdb3696fe2466a3877"),
                HexFormat.of().parseHex("b825c0745f46ac58f7d3759e6dc535a1fec7820377f24d4c2c6ad2cc55c0cb59"),
                HexFormat.of().parseHex("95513952a04bd8992721e9b7e2937f1c04ba31e0469fbe615a78197f68f52b7c"),
                HexFormat.of().parseHex("2e6d722e5e4dbdf2447ddecc9f7dabb8e299bae921c99ad5b0184cd9eb8e5908"),
        };
        byte[][] answer = new byte[][]{
                HexFormat.of().parseHex("8b30c5ba100f6f2e5ad1e2a742e5020491240f8eb514fe97c713c31718ad7ecd"),
                HexFormat.of().parseHex("7f4e6f9e224e20fda0ae4c44114237f97cd35aca38d83081c9bfd41feb907800"),
                HexFormat.of().parseHex("ade48f2bbb57318cc79f3a8678febaa827599c509dce5940602e54c7733332e7"),
                HexFormat.of().parseHex("68b3e2ab8182dfd646f13fdf01c335cf32476482d963f5cd94e934e6b3401069"),
                HexFormat.of().parseHex("43e7274e77fbe8e5a42a8fb58f7decdb04d521f319f332d88e6b06f8e6c09e27"),
                HexFormat.of().parseHex("1796cd3ca4fef00236e07b723d3ed88e1ac433acaaa21da64c4b33c946cf3d10"),
        };
        byte[][] result = Helper.merkle_parent_level(hashes);
        assertEquals(answer.length, result.length);
        assertArrayEquals(answer, result);
    }

    @Test
    void test_merkle_root() {
        byte[][] hashes = new byte[][]{
                HexFormat.of().parseHex("c117ea8ec828342f4dfb0ad6bd140e03a50720ece40169ee38bdc15d9eb64cf5"),
                HexFormat.of().parseHex("c131474164b412e3406696da1ee20ab0fc9bf41c8f05fa8ceea7a08d672d7cc5"),
                HexFormat.of().parseHex("f391da6ecfeed1814efae39e7fcb3838ae0b02c02ae7d0a5848a66947c0727b0"),
                HexFormat.of().parseHex("3d238a92a94532b946c90e19c49351c763696cff3db400485b813aecb8a13181"),
                HexFormat.of().parseHex("10092f2633be5f3ce349bf9ddbde36caa3dd10dfa0ec8106bce23acbff637dae"),
                HexFormat.of().parseHex("7d37b3d54fa6a64869084bfd2e831309118b9e833610e6228adacdbd1b4ba161"),
                HexFormat.of().parseHex("8118a77e542892fe15ae3fc771a4abfd2f5d5d5997544c3487ac36b5c85170fc"),
                HexFormat.of().parseHex("dff6879848c2c9b62fe652720b8df5272093acfaa45a43cdb3696fe2466a3877"),
                HexFormat.of().parseHex("b825c0745f46ac58f7d3759e6dc535a1fec7820377f24d4c2c6ad2cc55c0cb59"),
                HexFormat.of().parseHex("95513952a04bd8992721e9b7e2937f1c04ba31e0469fbe615a78197f68f52b7c"),
                HexFormat.of().parseHex("2e6d722e5e4dbdf2447ddecc9f7dabb8e299bae921c99ad5b0184cd9eb8e5908"),
                HexFormat.of().parseHex("b13a750047bc0bdceb2473e5fe488c2596d7a7124b4e716fdd29b046ef99bbf0"),
        };
        assertArrayEquals(HexFormat.of().parseHex("acbcab8bcc1af95d8d563b77d24c3d19b18f1486383d75a5085c4e86c86beed6"),
                Helper.merkle_root(hashes));
    }

    @Test
    void test_bit_field_to_bytes() {
        byte[] bit_field = new byte[]{0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};
        byte[] answer = HexFormat.of().parseHex("4000600a080000010940");
        assertArrayEquals(answer, Helper.bit_field_to_bytes(bit_field));
        assertArrayEquals(bit_field, Helper.bytes_to_bit_field(answer));
    }

}