package com.horace.coin.tx;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @param amount # amount is an integer in 8 bytes, little endian
 *               # use Script.parse to get the ScriptPubKey
 */
public record TxOut(long amount, Script scriptPubkey) {

    @SneakyThrows
    public static TxOut parse(final InputStream s) {
        return new TxOut(EndianUtils.littleEndianToInt(s.readNBytes(8)), Script.parse(s));
    }

    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write(EndianUtils.intToLittleEndian(amount, 8));
            baos.write(scriptPubkey.serialize());
            return baos.toByteArray();
        }
    }
}
