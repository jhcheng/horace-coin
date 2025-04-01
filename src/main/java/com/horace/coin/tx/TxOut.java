package com.horace.coin.tx;

import lombok.SneakyThrows;

import java.nio.ByteBuffer;

/**
 * @param amount # amount is an integer in 8 bytes, little endian
 *               # use Script.parse to get the ScriptPubKey
 */
public record TxOut(long amount, Script scriptPubkey) {

    @SneakyThrows
    public static TxOut parse(final ByteBuffer buffer) {
        byte[] eightBytes = new byte[8];
        buffer.get(eightBytes);
        return new TxOut(EndianUtils.littleEndianToInt(eightBytes).longValueExact(), Script.parse(buffer));
    }

    public byte[] serialize() {
        byte[] key = scriptPubkey.serialize();
        ByteBuffer buffer = ByteBuffer.allocate(key.length + 8);
        buffer.put(EndianUtils.intToLittleEndian(amount, 8));
        buffer.put(key);
        return buffer.array();
    }
}
