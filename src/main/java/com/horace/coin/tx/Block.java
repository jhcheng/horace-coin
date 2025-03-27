package com.horace.coin.tx;

import com.horace.coin.Helper;
import org.bouncycastle.util.Arrays;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;

public record Block(int version, byte[] prevBlock, byte[] merkleRoot, int timestamp, byte[] bits, byte[] nonce) {

    public static Block parse(ByteBuffer buffer) {
        byte[] tmp = new byte[4];
        buffer.get(tmp, 0, 4);
        int version = EndianUtils.littleEndianToInt(tmp).intValue();
        byte[] prevBlock = new byte[32];
        buffer.get(prevBlock, 0, 32);
        byte[] merkleRoot = new byte[32];
        buffer.get(merkleRoot, 0, 32);
        buffer.get(tmp, 0, 4);
        int timestamp = EndianUtils.littleEndianToInt(tmp).intValue();
        byte[] bits = new byte[4];
        buffer.get(bits, 0, 4);
        byte[] nonce = new byte[4];
        buffer.get(nonce, 0, 4);
        return new Block(version, Arrays.reverse(prevBlock), Arrays.reverse(merkleRoot), timestamp, bits, nonce);
    }

    public static Block parse(byte[] block) {
        return parse(ByteBuffer.wrap(block));
    }

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(80);
        buffer.put(EndianUtils.intToLittleEndian(version, 4));
        buffer.put(Arrays.reverse(prevBlock));
        buffer.put(Arrays.reverse(merkleRoot));
        buffer.put(EndianUtils.intToLittleEndian(timestamp, 4));
        buffer.put(bits);
        buffer.put(nonce);
        return buffer.array();
    }

    public byte[] hash() {
        return Arrays.reverse(Helper.hash256(serialize()));
    }

    public boolean bip9() {
        return version >> 29 == 0b001;
    }

    public boolean bip91() {
        return ((version >> 4) & 1) == 1;

    }

    public boolean bip141() {
        return ((version >> 1) & 1) == 1;
    }

    public BigInteger target() {
        return Helper.bitsToTarget(bits);
    }

    public BigDecimal difficulty() {
        return BigDecimal.valueOf(0xffff).multiply(BigDecimal.valueOf(256).pow(0x1d - 3)).divide(new BigDecimal(target()), RoundingMode.HALF_UP);
    }

    public boolean check_pow() {
        BigInteger proof = EndianUtils.littleEndianToInt(Helper.hash256(serialize()));
        return proof.compareTo(target()) < 0;
    }
}
