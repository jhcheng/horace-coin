package com.horace.coin.tx;

import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ScriptTest {

    @SneakyThrows
    @Test
    void parse() {
        final InputStream in = new ByteArrayInputStream(Hex.decode("6a47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937"));
        final Script script = Script.parse(in);
        assertEquals(Hex.toHexString(script.getCmds()[0]), "304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a71601");
        assertArrayEquals(script.getCmds()[1], Hex.decode("035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937"));
    }

    @Test
    @SneakyThrows
    void serialize() {
        final String want = "6a47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937";
        final Script script = Script.parse(new ByteArrayInputStream(Hex.decode(want)));
        assertEquals(want, Hex.toHexString(script.serialize()));
    }

    @SneakyThrows
    @Test
    void parse_02() {
        final Script script = Script.parse(new ByteArrayInputStream(Hex.decode("6b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccf" +
                "cf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8" +
                "e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278" +
                "a")));
        assertEquals("3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f0220" +
                "7a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01 0349fc4e631" +
                "e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a", script.toString());
    }

}