package com.horace.coin.ecc;

import com.horace.coin.Helper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.util.Arrays;

public class PrivateKey {
    private final BigInteger secret;
    @Getter
    private final S256Point point;
    private final HMac hmac;

    public PrivateKey(final BigInteger secret) {
        this.secret = secret;
        this.point = S256Constant.G.rmul(secret);
        hmac = new HMac(new SHA256Digest());
    }

    public Signature sign(final BigInteger z) {
        final BigInteger k = deterministicK(z);
        final BigInteger r = S256Constant.G.rmul(k).getX().getNum();
        final BigInteger k_inv = k.modPow(S256Constant.N.subtract(BigInteger.TWO), S256Constant.N);
        BigInteger s = z.add(r.multiply(secret)).multiply(k_inv).mod(S256Constant.N);
        if (s.compareTo(S256Constant.N.divide(BigInteger.TWO)) > 0) {
            s = S256Constant.N.subtract(s);
        }
        return new Signature(r, s);
    }

    @SneakyThrows
    private BigInteger deterministicK(final BigInteger z) {
        final byte[] k = new byte[32];
        final byte[] v = new byte[32];
        Arrays.fill(k, (byte) 0);
        Arrays.fill(v, (byte) 0x01);
        final byte[] z_bytes = z.compareTo(S256Constant.N) > 0 ? BigIntegers.asUnsignedByteArray(32, z.subtract(S256Constant.N)) : BigIntegers.asUnsignedByteArray(32, z);
        final byte[] secret_bytes = BigIntegers.asUnsignedByteArray(32, secret);
        // k = hmac.new(k, v + b'\x00' + secret_bytes + z_bytes, s256).digest()
        hmac.init(new KeyParameter(k));
        hmac.update(v, 0, v.length);
        hmac.update((byte) 0x00);
        hmac.update(secret_bytes, 0, secret_bytes.length);
        hmac.update(z_bytes, 0, z_bytes.length);
        hmac.doFinal(k, 0);
        // v = hmac.new(k, v, s256).digest()
        hmac.init(new KeyParameter(k));
        hmac.update(v, 0, v.length);
        hmac.doFinal(v, 0);
        // k = hmac.new(k, v + b'\x01' + secret_bytes + z_bytes, s256).digest()
        hmac.update(v, 0, v.length);
        hmac.update((byte) 0x01);
        hmac.update(secret_bytes, 0, secret_bytes.length);
        hmac.update(z_bytes, 0, z_bytes.length);
        hmac.doFinal(k, 0);
        // v = hmac.new(k, v, s256).digest()
        hmac.init(new KeyParameter(k));
        hmac.update(v, 0, v.length);
        hmac.doFinal(v, 0);
        while (true) {
            //  v = hmac.new(k, v, s256).digest()
            //  candidate = int.from_bytes(v, 'big')
            //  if candidate >= 1 and candidate < N:
            //      return candidate  2
            hmac.update(v, 0, v.length);
            hmac.doFinal(v, 0);
            final BigInteger candidate = BigIntegers.fromUnsignedByteArray(v);
            if (candidate.compareTo(BigInteger.ONE) >= 0 && candidate.compareTo(S256Constant.N) < 0) return candidate;
            //  k = hmac.new(k, v + b'\x00', s256).digest()
            //  v = hmac.new(k, v, s256).digest()
            hmac.update(v, 0, v.length);
            hmac.update((byte)0x00);
            hmac.doFinal(k, 0);
            hmac.init(new KeyParameter(k));
            hmac.update(v, 0, v.length);
            hmac.doFinal(v, 0);
        }
    }

    public String wif(final boolean compressed, final boolean testnet) {
        final byte[] secret_bytes = BigIntegers.asUnsignedByteArray(32, secret);
        final byte prefix = (byte) (testnet ? 0xEF : 0x80);
        final byte[] temp;
        if (compressed) {
            temp = new byte[secret_bytes.length + 2];
            temp[secret_bytes.length + 1] = 0x01;
        } else {
            temp = new byte[secret_bytes.length + 1];
        }
        temp[0] = prefix;
        System.arraycopy(secret_bytes, 0, temp, 1, secret_bytes.length);
        return Helper.encode_base58_checksum(temp);
    }

}
