package com.horace.coin.tx;

import com.horace.coin.Helper;
import com.horace.coin.ecc.PrivateKey;
import com.horace.coin.ecc.S256Point;
import com.horace.coin.ecc.Signature;
import lombok.SneakyThrows;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class TxTest {

    @SneakyThrows
    @Test
    void test_parse_version() {
        String raw_tx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Tx tx = Tx.parse(new ByteArrayInputStream(Hex.decode(raw_tx)));
        assertEquals(1, tx.getVersion());
    }

    @SneakyThrows
    @Test
    void test_parse_inputs() {
        String raw_tx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Tx tx = Tx.parse(new ByteArrayInputStream(Hex.decode(raw_tx)));
        assertEquals(1, tx.getTxIns().length);
        String prev_tx_raw = "d1c789a9c60383bf715f3f6ad9d14b91fe55f3deb369fe5d9280cb1a01793f81";
        assertEquals(prev_tx_raw, Hex.toHexString(tx.getTxIns()[0].getPrevTx()));
        assertEquals(0, tx.getTxIns()[0].getPrevIndex());
        assertEquals("6b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a",
                Hex.toHexString(tx.getTxIns()[0].getScriptSig().serialize()));
        assertEquals(0xfffffffe, tx.getTxIns()[0].getSequence());
    }

    @SneakyThrows
    @Test
    void test_parse_outputs() {
        String raw_tx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Tx tx = Tx.parse(new ByteArrayInputStream(Hex.decode(raw_tx)));
        assertEquals(2, tx.getTxOuts().length);
        assertEquals(32454049, tx.getTxOuts()[0].amount());
        assertEquals("1976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac",
                Hex.toHexString(tx.getTxOuts()[0].scriptPubkey().serialize()));
        assertEquals(10011545, tx.getTxOuts()[1].amount());
        assertEquals("1976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac",
                Hex.toHexString(tx.getTxOuts()[1].scriptPubkey().serialize()));
    }

    @SneakyThrows
    @Test
    void test_parse_outputs_02() {
        String raw_tx = "010000000456919960ac691763688d3d3bcea9ad6ecaf875df5339e" +
                "148a1fc61c6ed7a069e010000006a47304402204585bcdef85e6b1c6af5c2669d4830ff86e42dd" +
                "205c0e089bc2a821657e951c002201024a10366077f87d6bce1f7100ad8cfa8a064b39d4e8fe4e" +
                "a13a7b71aa8180f012102f0da57e85eec2934a82a585ea337ce2f4998b50ae699dd79f5880e253" +
                "dafafb7feffffffeb8f51f4038dc17e6313cf831d4f02281c2a468bde0fafd37f1bf882729e7fd" +
                "3000000006a47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1c" +
                "dc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a716012" +
                "1035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937feffffff567" +
                "bf40595119d1bb8a3037c356efd56170b64cbcc160fb028fa10704b45d775000000006a4730440" +
                "2204c7c7818424c7f7911da6cddc59655a70af1cb5eaf17c69dadbfc74ffa0b662f02207599e08" +
                "bc8023693ad4e9527dc42c34210f7a7d1d1ddfc8492b654a11e7620a0012102158b46fbdff65d0" +
                "172b7989aec8850aa0dae49abfb84c81ae6e5b251a58ace5cfeffffffd63a5e6c16e620f86f375" +
                "925b21cabaf736c779f88fd04dcad51d26690f7f345010000006a47304402200633ea0d3314bea" +
                "0d95b3cd8dadb2ef79ea8331ffe1e61f762c0f6daea0fabde022029f23b3e9c30f080446150b23" +
                "852028751635dcee2be669c2a1686a4b5edf304012103ffd6f4a67e94aba353a00882e563ff272" +
                "2eb4cff0ad6006e86ee20dfe7520d55feffffff0251430f00000000001976a914ab0c0b2e98b1a" +
                "b6dbf67d4750b0a56244948a87988ac005a6202000000001976a9143c82d7df364eb6c75be8c80" +
                "df2b3eda8db57397088ac46430600";
        Tx tx = Tx.parse(new ByteArrayInputStream(Hex.decode(raw_tx)));
        assertEquals(2, tx.getTxOuts().length);
        assertEquals("304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008" +
                "b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a71601 035d5c93d9ac9" +
                "6881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937",
                tx.getTxIns()[1].getScriptSig().toString());
        assertEquals(40000000, tx.getTxOuts()[1].amount());
        assertEquals("OP_DUP OP_HASH160 ab0c0b2e98b1ab6dbf67d4750b0a56244948a879 OP_EQUALVERIFY OP_CHECKSIG"
                , tx.getTxOuts()[0].scriptPubkey().toString());
    }

    @SneakyThrows
    @Test
    void test_parse_locktime() {
        String raw_tx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Tx tx = Tx.parse(new ByteArrayInputStream(Hex.decode(raw_tx)));
        assertEquals(410393, tx.getLockTime());
    }

    @SneakyThrows
    @Test
    void test_fee_01() {
        String raw_tx = "0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600";
        Tx tx = Tx.parse(new ByteArrayInputStream(Hex.decode(raw_tx)));
        assertEquals(40000, tx.fee());
    }

    @SneakyThrows
    @Test
    void test_fee_02() {
        String raw_tx = "010000000456919960ac691763688d3d3bcea9ad6ecaf875df5339e148a1fc61c6ed7a069e010000006a47304402204585bcdef85e6b1c6af5c2669d4830ff86e42dd205c0e089bc2a821657e951c002201024a10366077f87d6bce1f7100ad8cfa8a064b39d4e8fe4ea13a7b71aa8180f012102f0da57e85eec2934a82a585ea337ce2f4998b50ae699dd79f5880e253dafafb7feffffffeb8f51f4038dc17e6313cf831d4f02281c2a468bde0fafd37f1bf882729e7fd3000000006a47304402207899531a52d59a6de200179928ca900254a36b8dff8bb75f5f5d71b1cdc26125022008b422690b8461cb52c3cc30330b23d574351872b7c361e9aae3649071c1a7160121035d5c93d9ac96881f19ba1f686f15f009ded7c62efe85a872e6a19b43c15a2937feffffff567bf40595119d1bb8a3037c356efd56170b64cbcc160fb028fa10704b45d775000000006a47304402204c7c7818424c7f7911da6cddc59655a70af1cb5eaf17c69dadbfc74ffa0b662f02207599e08bc8023693ad4e9527dc42c34210f7a7d1d1ddfc8492b654a11e7620a0012102158b46fbdff65d0172b7989aec8850aa0dae49abfb84c81ae6e5b251a58ace5cfeffffffd63a5e6c16e620f86f375925b21cabaf736c779f88fd04dcad51d26690f7f345010000006a47304402200633ea0d3314bea0d95b3cd8dadb2ef79ea8331ffe1e61f762c0f6daea0fabde022029f23b3e9c30f080446150b23852028751635dcee2be669c2a1686a4b5edf304012103ffd6f4a67e94aba353a00882e563ff2722eb4cff0ad6006e86ee20dfe7520d55feffffff0251430f00000000001976a914ab0c0b2e98b1ab6dbf67d4750b0a56244948a87988ac005a6202000000001976a9143c82d7df364eb6c75be8c80df2b3eda8db57397088ac46430600";
        Tx tx = Tx.parse(new ByteArrayInputStream(Hex.decode(raw_tx)));
        assertEquals(140500, tx.fee());
    }

    @SneakyThrows
    @Test
    void test_sig_hash() {
        Tx tx = TxFetcher.fetch("452c629d67e41baec3ac6f04fe744b4b9617f8f859c63b3002f8684e7a4fee03");
        assertEquals(new BigInteger("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6", 16), tx.sig_hash(0));
    }

    @Test
    void test_verify_p2pkh() {
        Tx tx = TxFetcher.fetch("452c629d67e41baec3ac6f04fe744b4b9617f8f859c63b3002f8684e7a4fee03");
        assertTrue(tx.verify());
    }

    @Test
    void test_verify_p2pkh_testnet() {
        Tx tx = TxFetcher.fetch("5418099cc755cb9dd3ebc6cf1a7888ad53a1a3beb5a025bce89eb1bf7f1650a2", true);
        assertTrue(tx.verify());
    }

    @Test
    void test_verify_p2sh() {
        Tx tx = TxFetcher.fetch("46df1a9484d0a81d03ce0ee543ab6e1a23ed06175c104a178268fad381216c2b");
        assertTrue(tx.verify());
    }

    @SneakyThrows
    @Test
    void test_sign_input() {
        PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(8675309));
        Tx tx = Tx.parse(new ByteArrayInputStream(HexFormat.of().parseHex("010000000199a24308080ab26e6fb65c4eccfadf76749bb5bfa8cb08f291320b3c21e56f0d0d00000000ffffffff02408af701000000001976a914d52ad7ca9b3d096a38e752c2018e6fbc40cdf26f88ac80969800000000001976a914507b27411ccf7f16f10297de6cef3f291623eddf88ac00000000")), true);
        assertTrue(tx.sign_input(0, privateKey));
        assertEquals("010000000199a24308080ab26e6fb65c4eccfadf76749bb5bfa8cb08f291320b3c21e56f0d0d0000006b4830450221008ed46aa2cf12d6d81065bfabe903670165b538f65ee9a3385e6327d80c66d3b502203124f804410527497329ec4715e18558082d489b218677bd029e7fa306a72236012103935581e52c354cd2f484fe8ed83af7a3097005b2f9c60bff71d35bd795f54b67ffffffff02408af701000000001976a914d52ad7ca9b3d096a38e752c2018e6fbc40cdf26f88ac80969800000000001976a914507b27411ccf7f16f10297de6cef3f291623eddf88ac00000000",
                HexFormat.of().formatHex(tx.serialize()));
    }

    @SneakyThrows
    @Test
    void test_tx_01() {
        HexFormat format = HexFormat.of();
        byte[] prevTx = format.parseHex("75a1c4bc671f55f626dda1074c7725991e6f68b8fcefcfca7b64405ca3b45f1c");
        int prevIdx = 1;
        String target_address = "miKegze5FQNCnGw6PKyqUbYUeBa4x2hFeM";
        double target_amount = 0.01;
        String change_address = "mzx5YhAH9kNHtcN481u6WkjeHjYtVeKVh2";
        double change_amount = 0.009;
        int secret = 8675309;
        PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(secret));
        TxIn[] txIns = new TxIn[] {new TxIn(prevTx, prevIdx)};
        TxOut[] txOuts = new TxOut[2];
        txOuts[0] = new TxOut((long) (target_amount * 100000000L), Script.p2pkh_script(Helper.decode_base58(target_address)));
        txOuts[1] = new TxOut((long) (change_amount * 100000000L), Script.p2pkh_script(Helper.decode_base58(change_address))); // amount will be 89999, not 900000
        Tx tx = new Tx(1, txIns, txOuts, 0, true);
        assertTrue(tx.sign_input(0, privateKey));
        assertEquals("01000000011c5fb4a35c40647bcacfeffcb8686f1e9925774c07a1dd26f6551f67bcc4a1750100" +
                "00006b483045022100a08ebb92422b3599a2d2fcdaa11f8f807a66ccf33e7f4a9ff0a3c51f1b1e" +
                "c5dd02205ed21dfede5925362b8d9833e908646c54be7ac6664e31650159e8f69b6ca539012103" +
                "935581e52c354cd2f484fe8ed83af7a3097005b2f9c60bff71d35bd795f54b67ffffffff024042" +
                "0f00000000001976a9141ec51b3654c1f1d0f4929d11a1f702937eaf50c888ac9fbb0d00000000" +
                "001976a914d52ad7ca9b3d096a38e752c2018e6fbc40cdf26f88ac00000000"
                , format.formatHex(tx.serialize()));
    }

    @SneakyThrows
    @Test
    void test_tx_02() {
        HexFormat format = HexFormat.of();
        String target_address = "mwJn1YPMq7y5F8J3LkC5Hxg9PHyZ5K4cFv";
        double target_amount = 0.0429;
        int secret = 8675309;
        PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(secret));
        TxIn[] txIns = new TxIn[] {
                new TxIn(format.parseHex("11d05ce707c1120248370d1cbf5561d22c4f83aeba0436792c82e0bd57fe2a2f"), 1),
                new TxIn(format.parseHex("51f61f77bd061b9a0da60d4bedaaf1b1fad0c11e65fdc744797ee22d20b03d15"), 1),
        };
        TxOut[] txOuts = new TxOut[] {
                new TxOut((long) (target_amount * 100000000L), Script.p2pkh_script(Helper.decode_base58(target_address)))
        };
        Tx tx = new Tx(1, txIns, txOuts, 0, true);
        assertTrue(tx.sign_input(0, privateKey));
        assertTrue(tx.sign_input(1, privateKey));
        assertEquals("01000000022f2afe57bde0822c793604baae834f2cd26155bf1c0d37480212c107e75cd0110100" +
                        "00006a47304402204cc5fe11b2b025f8fc9f6073b5e3942883bbba266b71751068badeb8f11f03" +
                        "64022070178363f5dea4149581a4b9b9dbad91ec1fd990e3fa14f9de3ccb421fa5b26901210393" +
                        "5581e52c354cd2f484fe8ed83af7a3097005b2f9c60bff71d35bd795f54b67ffffffff153db020" +
                        "2de27e7944c7fd651ec1d0fab1f1aaed4b0da60d9a1b06bd771ff651010000006b483045022100" +
                        "b7a938d4679aa7271f0d32d83b61a85eb0180cf1261d44feaad23dfd9799dafb02205ff2f366dd" +
                        "d9555f7146861a8298b7636be8b292090a224c5dc84268480d8be1012103935581e52c354cd2f4" +
                        "84fe8ed83af7a3097005b2f9c60bff71d35bd795f54b67ffffffff01d0754100000000001976a9" +
                        "14ad346f8eb57dee9a37981716e498120ae80e44f788ac00000000"
                , format.formatHex(tx.serialize()));
    }

    @SneakyThrows
    @Test
    void test_redeem_script() {
        HexFormat hexFormat = HexFormat.of();
        String hex_tx = "0100000001868278ed6ddfb6c1ed3ad5f8181eb0c7a385aa0836f01d5e4789e6" +
                "bd304d87221a000000db00483045022100dc92655fe37036f47756db8102e0d7d5e28b3beb83a8" +
                "fef4f5dc0559bddfb94e02205a36d4e4e6c7fcd16658c50783e00c341609977aed3ad00937bf4e" +
                "e942a8993701483045022100da6bee3c93766232079a01639d07fa869598749729ae323eab8eef" +
                "53577d611b02207bef15429dcadce2121ea07f233115c6f09034c0be68db99980b9a6c5e754022" +
                "01475221022626e955ea6ea6d98850c994f9107b036b1334f18ca8830bfff1295d21cfdb702103" +
                "b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb7152aeffffffff04" +
                "d3b11400000000001976a914904a49878c0adfc3aa05de7afad2cc15f483a56a88ac7f40090000" +
                "0000001976a914418327e3f3dda4cf5b9089325a4b95abdfa0334088ac722c0c00000000001976" +
                "a914ba35042cfe9fc66fd35ac2224eebdafd1028ad2788acdc4ace020000000017a91474d691da" +
                "1574e6b3c192ecfb52cc8984ee7b6c568700000000";
        String hex_sec = "03b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7fbdbd4bb71";
        String hex_der = "3045022100da6bee3c93766232079a01639d07fa869598749729ae323eab8ee" +
                "f53577d611b02207bef15429dcadce2121ea07f233115c6f09034c0be68db99980b9a6c5e754022";
        String hex_redeem_script = "475221022626e955ea6ea6d98850c994f9107b036b1334f18ca88" +
                "30bfff1295d21cfdb702103b287eaf122eea69030a0e9feed096bed8045c8b98bec453e1ffac7f" +
                "bdbd4bb7152ae";
        Tx tx_obj = Tx.parse(new ByteArrayInputStream(hexFormat.parseHex(hex_tx)));
        Script redeem_script = Script.parse(new ByteArrayInputStream(hexFormat.parseHex(hex_redeem_script)));
        byte[] s = Arrays.concatenate(EndianUtils.intToLittleEndian(tx_obj.getVersion(), 4), EndianUtils.encodeVarInt(tx_obj.getTxIns().length));
        TxIn i = tx_obj.getTxIns()[0];
        s = Arrays.concatenate(s, new TxIn(i.getPrevTx(), i.getPrevIndex(), redeem_script, i.getSequence()).serialize());
        s = Arrays.concatenate(s, EndianUtils.encodeVarInt(tx_obj.getTxOuts().length));
        for (TxOut o : tx_obj.getTxOuts()) {
            s = Arrays.concatenate(s, o.serialize());
        }
        s = Arrays.concatenate(s, EndianUtils.intToLittleEndian(tx_obj.getLockTime(), 4));
        s = Arrays.concatenate(s, EndianUtils.intToLittleEndian(Helper.SIGHASH_ALL, 4));
        BigInteger z = BigIntegers.fromUnsignedByteArray(Helper.hash256(s));
        S256Point point = S256Point.parse(hexFormat.parseHex(hex_sec));
        Signature signature = Signature.parse(hexFormat.parseHex(hex_der));
        assertTrue(point.verify(z, signature));
    }

    @SneakyThrows
    @Test
    void test_is_coinbase() {
        Tx tx = Tx.parse(new ByteArrayInputStream(HexFormat.of().parseHex("01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff5e03d71b07254d696e656420627920416e74506f6f6c20626a31312f4542312f4144362f43205914293101fabe6d6d678e2c8c34afc36896e7d9402824ed38e856676ee94bfdb0c6c4bcd8b2e5666a0400000000000000c7270000a5e00e00ffffffff01faf20b58000000001976a914338c84849423992471bffb1a54a8d9b1d69dc28a88ac00000000")));
        assertTrue(tx.is_coinbase());
    }

    @SneakyThrows
    @Test
    void test_coinbase_height_01() {
        Tx tx = Tx.parse(new ByteArrayInputStream(HexFormat.of().parseHex("01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff5e03d71b07254d696e656420627920416e74506f6f6c20626a31312f4542312f4144362f43205914293101fabe6d6d678e2c8c34afc36896e7d9402824ed38e856676ee94bfdb0c6c4bcd8b2e5666a0400000000000000c7270000a5e00e00ffffffff01faf20b58000000001976a914338c84849423992471bffb1a54a8d9b1d69dc28a88ac00000000")));
        assertEquals(465879, tx.coinbase_height());
    }

    @SneakyThrows
    @Test
    void test_coinbase_height_02() {
        Tx tx = Tx.parse(new ByteArrayInputStream(HexFormat.of().parseHex("0100000001813f79011acb80925dfe69b3def355fe914bd1d96a3f5f71bf8303c6a989c7d1000000006b483045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031ccfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9c8e10615bed01210349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278afeffffff02a135ef01000000001976a914bc3b654dca7e56b04dca18f2566cdaf02e8d9ada88ac99c39800000000001976a9141c4bc762dd5423e332166702cb75f40df79fea1288ac19430600")));
        assertNull(tx.coinbase_height());
    }

}