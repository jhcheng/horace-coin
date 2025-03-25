package com.horace.coin.tx;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    @Test
    void parse() {
        Block block = Block.parse(HexFormat.of().parseHex("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        assertEquals(0x20000002, block.version());
        assertEquals("000000000000000000fd0c220a0a8c3bc5a7b487e8c8de0dfa2373b12894c38e", HexFormat.of().formatHex(block.prevBlock()));
        assertEquals("be258bfd38db61f957315c3f9e9c5e15216857398d50402d5089a8e0fc50075b", HexFormat.of().formatHex(block.merkleRoot()));
        assertEquals(0x59a7771e, block.timestamp());
        assertArrayEquals(HexFormat.of().parseHex("e93c0118"), block.bits());
        assertArrayEquals(HexFormat.of().parseHex("a4ffd71d"), block.nonce());
    }

    @Test
    void serialize() {
        byte[] block_raw = HexFormat.of().parseHex("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d");
        Block block = Block.parse(block_raw);
        assertArrayEquals(block_raw, block.serialize());
    }

    @Test
    void test_hash() {
        Block block = Block.parse(HexFormat.of().parseHex("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        assertArrayEquals(HexFormat.of().parseHex("0000000000000000007e9e4c586439b0cdbe13b1370bdd9435d76a644d047523"), block.hash());
    }

    @Test
    void test_bip9_true() {
        Block block = Block.parse(HexFormat.of().parseHex("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        assertTrue(block.bip9());
    }

    @Test
    void test_bip9_false() {
        Block block = Block.parse(HexFormat.of().parseHex("0400000039fa821848781f027a2e6dfabbf6bda920d9ae61b63400030000000000000000ecae536a304042e3154be0e3e9a8220e5568c3433a9ab49ac4cbb74f8df8e8b0cc2acf569fb9061806652c27"));
        assertFalse(block.bip9());
    }

    @Test
    void test_bip91_true() {
        Block block = Block.parse(HexFormat.of().parseHex("1200002028856ec5bca29cf76980d368b0a163a0bb81fc192951270100000000000000003288f32a2831833c31a25401c52093eb545d28157e200a64b21b3ae8f21c507401877b5935470118144dbfd1"));
        assertTrue(block.bip91());
    }

    @Test
    void test_bip91_false() {
        Block block = Block.parse(HexFormat.of().parseHex("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        assertFalse(block.bip91());
    }


    @Test
    void test_bip141_true() {
        Block block = Block.parse(HexFormat.of().parseHex("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        assertTrue(block.bip141());
    }

    @Test
    void test_bip141_false() {
        Block block = Block.parse(HexFormat.of().parseHex("0000002066f09203c1cf5ef1531f24ed21b1915ae9abeb691f0d2e0100000000000000003de0976428ce56125351bae62c5b8b8c79d8297c702ea05d60feabb4ed188b59c36fa759e93c0118b74b2618"));
        assertFalse(block.bip141());
    }

    @Test
    void test_target() {
        Block block = Block.parse(HexFormat.of().parseHex("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        assertEquals(new BigInteger("13ce9000000000000000000000000000000000000000000", 16), block.target());
    }

    @Test
    void test_difficulty() {
        Block block = Block.parse(HexFormat.of().parseHex("020000208ec39428b17323fa0ddec8e887b4a7c53b8c0a0a220cfd0000000000000000005b0750fce0a889502d40508d39576821155e9c9e3f5c3157f961db38fd8b25be1e77a759e93c0118a4ffd71d"));
        assertEquals(888171856257d, block.difficulty().doubleValue());
    }

    @Test
    void test_check_pow_true() {
        Block block = Block.parse(HexFormat.of().parseHex("04000000fbedbbf0cfdaf278c094f187f2eb987c86a199da22bbb20400000000000000007b7697b29129648fa08b4bcd13c9d5e60abb973a1efac9c8d573c71c807c56c3d6213557faa80518c3737ec1"));
        assertTrue(block.check_pow());
    }

    @Test
    void test_check_pow_false() {
        Block block = Block.parse(HexFormat.of().parseHex("04000000fbedbbf0cfdaf278c094f187f2eb987c86a199da22bbb20400000000000000007b7697b29129648fa08b4bcd13c9d5e60abb973a1efac9c8d573c71c807c56c3d6213557faa80518c3737ec0"));
        assertFalse(block.check_pow());
    }

}