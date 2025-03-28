package com.horace.coin.tx;

import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class MerkleBlockTest {

    @Test
    void parse() {
        MerkleBlock block = MerkleBlock.parse(HexFormat.of().parseHex("00000020df3b053dc46f162a9b00c7f0d5124e2676d47bbe7c5d0793a500000000000000ef445fef2ed495c275892206ca533e7411907971013ab83e3b47bd0d692d14d4dc7c835b67d8001ac157e670bf0d00000aba412a0d1480e370173072c9562becffe87aa661c1e4a6dbc305d38ec5dc088a7cf92e6458aca7b32edae818f9c2c98c37e06bf72ae0ce80649a38655ee1e27d34d9421d940b16732f24b94023e9d572a7f9ab8023434a4feb532d2adfc8c2c2158785d1bd04eb99df2e86c54bc13e139862897217400def5d72c280222c4cbaee7261831e1550dbb8fa82853e9fe506fc5fda3f7b919d8fe74b6282f92763cef8e625f977af7c8619c32a369b832bc2d051ecd9c73c51e76370ceabd4f25097c256597fa898d404ed53425de608ac6bfe426f6e2bb457f1c554866eb69dcb8d6bf6f880e9a59b3cd053e6c7060eeacaacf4dac6697dac20e4bd3f38a2ea2543d1ab7953e3430790a9f81e1c67f5b58c825acf46bd02848384eebe9af917274cdfbb1a28a5d58a23a17977def0de10d644258d9c54f886d47d293a411cb6226103b55635"));
        assertEquals(0x20000000, block.getVersion());
        assertArrayEquals(HexFormat.of().parseHex("ef445fef2ed495c275892206ca533e7411907971013ab83e3b47bd0d692d14d4"), Arrays.reverse(block.getMerkleRoot()));
        assertArrayEquals(HexFormat.of().parseHex("df3b053dc46f162a9b00c7f0d5124e2676d47bbe7c5d0793a500000000000000"), Arrays.reverse(block.getPrevBlock()));
        assertEquals(EndianUtils.littleEndianToInt(HexFormat.of().parseHex("dc7c835b")).intValue(), block.getTimestamp());
        assertArrayEquals(HexFormat.of().parseHex("67d8001a"), block.getBits());
        assertArrayEquals(HexFormat.of().parseHex("c157e670"), block.getNonce());
        assertEquals(EndianUtils.littleEndianToInt(HexFormat.of().parseHex("bf0d0000")).intValue(), block.getTotal());
        byte[][] hashes = new byte[][]{
                HexFormat.of().parseHex("ba412a0d1480e370173072c9562becffe87aa661c1e4a6dbc305d38ec5dc088a"),
                HexFormat.of().parseHex("7cf92e6458aca7b32edae818f9c2c98c37e06bf72ae0ce80649a38655ee1e27d"),
                HexFormat.of().parseHex("34d9421d940b16732f24b94023e9d572a7f9ab8023434a4feb532d2adfc8c2c2"),
                HexFormat.of().parseHex("158785d1bd04eb99df2e86c54bc13e139862897217400def5d72c280222c4cba"),
                HexFormat.of().parseHex("ee7261831e1550dbb8fa82853e9fe506fc5fda3f7b919d8fe74b6282f92763ce"),
                HexFormat.of().parseHex("f8e625f977af7c8619c32a369b832bc2d051ecd9c73c51e76370ceabd4f25097"),
                HexFormat.of().parseHex("c256597fa898d404ed53425de608ac6bfe426f6e2bb457f1c554866eb69dcb8d"),
                HexFormat.of().parseHex("6bf6f880e9a59b3cd053e6c7060eeacaacf4dac6697dac20e4bd3f38a2ea2543"),
                HexFormat.of().parseHex("d1ab7953e3430790a9f81e1c67f5b58c825acf46bd02848384eebe9af917274c"),
                HexFormat.of().parseHex("dfbb1a28a5d58a23a17977def0de10d644258d9c54f886d47d293a411cb62261"),
        };
        byte[][] reverseHashes = java.util.Arrays.stream(hashes).map(Arrays::reverse).toArray(byte[][]::new);
        assertArrayEquals(reverseHashes, block.getHashes());
        assertArrayEquals(HexFormat.of().parseHex("b55635"), block.getFlags());
    }

    @Test
    void test_is_valid() {
        MerkleBlock block = MerkleBlock.parse(HexFormat.of().parseHex("00000020df3b053dc46f162a9b00c7f0d5124e2676d47bbe7c5d0793a500000000000000ef445fef2ed495c275892206ca533e7411907971013ab83e3b47bd0d692d14d4dc7c835b67d8001ac157e670bf0d00000aba412a0d1480e370173072c9562becffe87aa661c1e4a6dbc305d38ec5dc088a7cf92e6458aca7b32edae818f9c2c98c37e06bf72ae0ce80649a38655ee1e27d34d9421d940b16732f24b94023e9d572a7f9ab8023434a4feb532d2adfc8c2c2158785d1bd04eb99df2e86c54bc13e139862897217400def5d72c280222c4cbaee7261831e1550dbb8fa82853e9fe506fc5fda3f7b919d8fe74b6282f92763cef8e625f977af7c8619c32a369b832bc2d051ecd9c73c51e76370ceabd4f25097c256597fa898d404ed53425de608ac6bfe426f6e2bb457f1c554866eb69dcb8d6bf6f880e9a59b3cd053e6c7060eeacaacf4dac6697dac20e4bd3f38a2ea2543d1ab7953e3430790a9f81e1c67f5b58c825acf46bd02848384eebe9af917274cdfbb1a28a5d58a23a17977def0de10d644258d9c54f886d47d293a411cb6226103b55635"));
        assertTrue(block.is_valid());
    }
}