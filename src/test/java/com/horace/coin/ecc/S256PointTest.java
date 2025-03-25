package com.horace.coin.ecc;

import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class S256PointTest {

    @Test
    public void test01() {
        final BigInteger z = new BigInteger("bc62d4b80d9e36da29c16c5d4d9f11731f36052c72401a76c23c0fb5a9b74423", 16);
        final BigInteger r = new BigInteger("37206a0610995c58074999cb9767b87af4c4978db68c06e8e6e81d282047a7c6", 16);
        final BigInteger s = new BigInteger("8ca63759c1157ebeaec0d03cecca119fc9a75bf8e6d0fa65c841c8e2738cdaec", 16);
        final BigInteger px = new BigInteger("04519fac3d910ca7e7138f7013706f619fa8f033e6ec6e09370ea38cee6a7574", 16);
        final BigInteger py = new BigInteger("82b51eab8c27c66e26c858a079bcdf4f1ada34cec420cafc7eac1a42216fb6c4", 16);
        final S256Point point = new S256Point(px, py);
        final BigInteger s_inv = s.modPow(S256Constant.N.subtract(BigInteger.TWO), S256Constant.N);
        final BigInteger u = z.multiply(s_inv).mod(S256Constant.N);
        final BigInteger v = r.multiply(s_inv).mod(S256Constant.N);
        assertEquals(r, S256Constant.G.rmul(u).add(point.rmul(v)).getX().getNum());
    }

    @Test
    public void test02() {
        final BigInteger z = new BigInteger("ec208baa0fc1c19f708a9ca96fdeff3ac3f230bb4a7ba4aede4942ad003c0f60", 16);
        final BigInteger r = new BigInteger("ac8d1c87e51d0d441be8b3dd5b05c8795b48875dffe00b7ffcfac23010d3a395", 16);
        final BigInteger s = new BigInteger("68342ceff8935ededd102dd876ffd6ba72d6a427a3edb13d26eb0781cb423c4", 16);
        final BigInteger px = new BigInteger("887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c", 16);
        final BigInteger py = new BigInteger("61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34", 16);
        final S256Point point = new S256Point(px, py);
        final BigInteger s_inv = s.modPow(S256Constant.N.subtract(BigInteger.TWO), S256Constant.N);
        final BigInteger u = z.multiply(s_inv).mod(S256Constant.N);
        final BigInteger v = r.multiply(s_inv).mod(S256Constant.N);
        assertEquals(r, S256Constant.G.rmul(u).add(point.rmul(v)).getX().getNum());
    }

    @Test
    public void test03() {
        final BigInteger z = new BigInteger("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d", 16);
        final BigInteger r = new BigInteger("eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c", 16);
        final BigInteger s = new BigInteger("c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab6", 16);
        final BigInteger px = new BigInteger("887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c", 16);
        final BigInteger py = new BigInteger("61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34", 16);
        final S256Point point = new S256Point(px, py);
        final BigInteger s_inv = s.modPow(S256Constant.N.subtract(BigInteger.TWO), S256Constant.N);
        final BigInteger u = z.multiply(s_inv).mod(S256Constant.N);
        final BigInteger v = r.multiply(s_inv).mod(S256Constant.N);
        assertEquals(r, S256Constant.G.rmul(u).add(point.rmul(v)).getX().getNum());
    }

    @Test
    public void testSec1() {
        final PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(5000));
        assertEquals("04ffe558e388852f0120e46af2d1b370f85854a8eb0841811ece0e3e03d282d57c315dc72890a4f10a1481c031b03b351b0dc79901ca18a00cf009dbdb157a1d10", Hex.toHexString(privateKey.getPoint().sec()));
    }

    @Test
    public void testSec2() {
        final PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(2018).pow(5));
        assertEquals("04027f3da1918455e03c46f659266a1bb5204e959db7364d2f473bdf8f0a13cc9dff87647fd023c13b4a4994f17691895806e1b40b57f4fd22581a4f46851f3b06", Hex.toHexString(privateKey.getPoint().sec()));
    }

    @Test
    public void testSec3() {
        final PrivateKey privateKey = new PrivateKey(new BigInteger("deadbeef12345", 16));
        assertEquals("04d90cd625ee87dd38656dd95cf79f65f60f7273b67d3096e68bd81e4f5342691f842efa762fd59961d0e99803c61edba8b3e3f7dc3a341836f97733aebf987121", Hex.toHexString(privateKey.getPoint().sec()));
    }

    @Test
    public void testSecCompressed1() {
        final PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(5001));
        assertEquals("0357a4f368868a8a6d572991e484e664810ff14c05c0fa023275251151fe0e53d1", Hex.toHexString(privateKey.getPoint().sec(true)));
    }

    @Test
    public void testSecCompressed2() {
        final PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(2019).pow(5));
        assertEquals("02933ec2d2b111b92737ec12f1c5d20f3233a0ad21cd8b36d0bca7a0cfa5cb8701", Hex.toHexString(privateKey.getPoint().sec(true)));
    }

    @Test
    public void testSecCompressed3() {
        final PrivateKey privateKey = new PrivateKey(new BigInteger("deadbeef54321", 16));
        assertEquals("0296be5b1292f6c856b3c5654e886fc13511462059089cdf9c479623bfcbe77690", Hex.toHexString(privateKey.getPoint().sec(true)));
    }

    @Test
    void address1() {
        final PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(5002));
        assertEquals("mmTPbXQFxboEtNRkwfh6K51jvdtHLxGeMA", privateKey.getPoint().address(false, true));
    }

    @Test
    void address2() {
        final PrivateKey privateKey = new PrivateKey(BigInteger.valueOf(2020).pow(5));
        assertEquals("mopVkxp8UhXqRYbCYJsbeE1h1fiF64jcoH", privateKey.getPoint().address(true, true));
    }

    @Test
    void address3() {
        final PrivateKey privateKey = new PrivateKey(new BigInteger("12345deadbeef", 16));
        assertEquals("1F1Pn2y6pDb68E5nYJJeba4TLg2U7B6KF1", privateKey.getPoint().address(true, false));
    }

    @SneakyThrows
    @Test
    void verify() {
        S256Point point = S256Point.parse(Hex.decode("0349fc4e631e3624a545de3f89f5d8684c7b8138bd94bdd531d2e213bf016b278a"));
        Signature signature = Signature.parse(Hex.decode("3045022100ed81ff192e75a3fd2304004dcadb746fa5e24c5031c" +
                "cfcf21320b0277457c98f02207a986d955c6e0cb35d446a89d3f56100f4d7f67801c31967743a9" +
                "c8e10615bed"));
        BigInteger z = new BigInteger("27e0c5994dec7824e56dec6b2fcb342eb7cdb0d0957c2fce9882f715e85d81a6", 16);
        assertTrue(point.verify(z, signature));
    }
}