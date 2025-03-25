package com.horace.coin.ecc;

import com.horace.coin.Helper;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;

public class S256Point extends FieldElementPoint {

    /*
    private static final S256Field A = new S256Field(BigInteger.ZERO);
    private static final S256Field B = new S256Field(BigInteger.valueOf(7));
    public static final BigInteger N = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);
    public static final FieldElementPoint G = new FieldElementPoint(
            new S256Field(new BigInteger("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798", 16)),
            new S256Field(new BigInteger("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8", 16)),
            A, B
    );
     */

    public S256Point(final S256Field x, final S256Field y) {
        super(x, y, S256Constant.A, S256Constant.B);
    }

    public S256Point(final BigInteger x, final BigInteger y) {
        this(new S256Field(x), new S256Field(y));
    }

    @Override
    public S256Point rmul(BigInteger coefficient) {
        FieldElementPoint result = super.rmul(coefficient.mod(S256Constant.N));
        return new S256Point(result.getX().getNum(), result.getY().getNum());
    }

    public boolean verify(final BigInteger z, final Signature signature) {
        final BigInteger s_inv = signature.s().modPow(S256Constant.N.subtract(BigInteger.TWO), S256Constant.N);
        final BigInteger u = z.multiply(s_inv).mod(S256Constant.N);
        final BigInteger v = signature.r().multiply(s_inv).mod(S256Constant.N);
        return signature.r().equals(S256Constant.G.rmul(u).add(rmul(v)).getX().getNum());
    }

    public byte[] sec() {
        return sec(true);
    }

    public byte[] sec(final boolean compressed) {
        if (compressed) {
            final byte[] bytes = new byte[1 + 32];
            if (getY().getNum().mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                bytes[0] = 0x02;
                System.arraycopy(BigIntegers.asUnsignedByteArray(32, getX().getNum()), 0, bytes, 1, 32);
            } else {
                bytes[0] = 0x03;
                System.arraycopy(BigIntegers.asUnsignedByteArray(32, getX().getNum()), 0, bytes, 1, 32);
            }
            return bytes;
        } else {
            final byte[] bytes = new byte[1 + 32 + 32];
            bytes[0] = 0x04;
            System.arraycopy(BigIntegers.asUnsignedByteArray(32, getX().getNum()), 0, bytes, 1, 32);
            System.arraycopy(BigIntegers.asUnsignedByteArray(32, getY().getNum()), 0, bytes, 33, 32);
            return bytes;
        }
    }

    public static S256Point parse(final byte[] sec_bin) {
        if (sec_bin[0] == 0x04) {
            return new S256Point(BigIntegers.fromUnsignedByteArray(sec_bin, 1, 32), BigIntegers.fromUnsignedByteArray(sec_bin, 33, 32));
        }
        final boolean is_even = sec_bin[0] == 0x02;
        final S256Field x = new S256Field(BigIntegers.fromUnsignedByteArray(sec_bin, 1, sec_bin.length - 1));
        final FieldElement alpha = x.pow(BigInteger.valueOf(3)).add(S256Constant.B);
        final FieldElement beta = alpha.sqrt();
        final S256Field even_beta;
        final S256Field odd_beta;
        if (beta.getNum().mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            even_beta = new S256Field(beta.getNum());
            odd_beta = new S256Field(beta.getPrime().subtract(beta.getNum()));
        } else {
            odd_beta = new S256Field(beta.getNum());
            even_beta = new S256Field(beta.getPrime().subtract(beta.getNum()));
        }
        if (is_even) {
            return new S256Point(x, even_beta);
        } else {
            return new S256Point(x, odd_beta);
        }
    }

    private byte[] hash160(final boolean compressed) {
        return Helper.hash160(sec(compressed));
    }

    public String address(final boolean compressed, final boolean testnet) {
        final byte[] h160 = hash160(compressed);
        final byte[] temp = new byte[h160.length + 1];
        temp[0] = (byte) (testnet ? 0x6f : 0x00);
        System.arraycopy(h160, 0, temp, 1, h160.length);
        return Helper.encode_base58_checksum(temp);
    }

}
