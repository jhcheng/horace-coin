package com.horace.coin.tx;

import lombok.SneakyThrows;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class ScriptTest {

    @SneakyThrows
    @Test
    void parse() {
        final InputStream in = new ByteArrayInputStream(Hex.decode("6a47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937"));
        final Script script = Script.parse(in);
        assertEquals(Hex.toHexString(script.cmds()[0]), "304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a71601");
        assertArrayEquals(script.cmds()[1], Hex.decode("035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937"));
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

    @Test
    void evaluate() {
        BigInteger z = new BigInteger("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d", 16);
        byte[] sec = Hex.decode("04887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e" +
                "4da568744d06c61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34");
        byte[] sig = Hex.decode("3045022000eff69ef2b1bd93a66ed5219add4fb51e11a840f4048" +
                "76325a1e8ffe0529a2c022100c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fd" +
                "dbdce6feab601");
        Script script_pubkey = new Script(sec, new byte[]{(byte) 0xac});
        Script script_sig = new Script(sig);
        Script combined_script = script_sig.add(script_pubkey);
        assertTrue(combined_script.evaluate(z));
    }

    @Test
    void evaluate_01() {
        Script script_pubkey = new Script(new byte[]{0x76}, new byte[]{0x76}, new byte[]{(byte) 0x95}, new byte[]{(byte) 0x93}, new byte[]{0x56}, new byte[]{(byte) 0x87});
        Script script_sig = new Script(new byte[]{0x52});
        Script combined_script = script_sig.add(script_pubkey);
        assertTrue(combined_script.evaluate(BigInteger.ZERO));
    }

    @SneakyThrows
    @Test
    void evaluate_02() {
        Script script_pubkey = Script.parse(new ByteArrayInputStream(Arrays.concatenate(EndianUtils.encodeVarInt(8), Hex.decode("6e879169a77ca787"))));
        byte[] collision1 = Hex.decode("255044462d312e330a25e2e3cfd30a0a0a312030206f626a0a3c3c2f576964746820" +
                "32203020522f4865696768742033203020522f547970652034203020522f537562747970652035" +
                "203020522f46696c7465722036203020522f436f6c6f7253706163652037203020522f4c656e67" +
                "74682038203020522f42697473506572436f6d706f6e656e7420383e3e0a73747265616d0affd8" +
                "fffe00245348412d3120697320646561642121212121852fec092339759c39b1a1c63c4c97e1ff" +
                "fe017f46dc93a6b67e013b029aaa1db2560b45ca67d688c7f84b8c4c791fe02b3df614f86db169" +
                "0901c56b45c1530afedfb76038e972722fe7ad728f0e4904e046c230570fe9d41398abe12ef5bc" +
                "942be33542a4802d98b5d70f2a332ec37fac3514e74ddc0f2cc1a874cd0c78305a215664613097" +
                "89606bd0bf3f98cda8044629a1");
        byte[] collision2 = Hex.decode("255044462d312e330a25e2e3cfd30a0a0a312030206f626a0a3c3c2f576964746820" +
                "32203020522f4865696768742033203020522f547970652034203020522f537562747970652035" +
                "203020522f46696c7465722036203020522f436f6c6f7253706163652037203020522f4c656e67" +
                "74682038203020522f42697473506572436f6d706f6e656e7420383e3e0a73747265616d0affd8" +
                "fffe00245348412d3120697320646561642121212121852fec092339759c39b1a1c63c4c97e1ff" +
                "fe017346dc9166b67e118f029ab621b2560ff9ca67cca8c7f85ba84c79030c2b3de218f86db3a9" +
                "0901d5df45c14f26fedfb3dc38e96ac22fe7bd728f0e45bce046d23c570feb141398bb552ef5a0" +
                "a82be331fea48037b8b5d71f0e332edf93ac3500eb4ddc0decc1a864790c782c76215660dd3097" +
                "91d06bd0af3f98cda4bc4629b1");
        Script script_sig = new Script(collision1, collision2);
        Script combined_script = script_sig.add(script_pubkey);
        assertTrue(combined_script.evaluate(BigInteger.ZERO));
    }

}