package com.horace.coin.tx;

import lombok.SneakyThrows;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @param prevTx    prev_tx is 32 bytes, little endian
 * @param prevIndex prev_index is an integer in 4 bytes, little endian
 * @param scriptSig use Script.parse to get the ScriptSig
 * @param sequence  sequence is an integer in 4 bytes, little-endian
 */
public record TxIn(byte[] prevTx, int prevIndex, Script scriptSig, int sequence) {

    @SneakyThrows
    public static TxIn psrse(final InputStream s) {
        final byte[] prevTx = Arrays.reverse(s.readNBytes(32));
        final int prevIndex = (int) EndianUtils.littleEndianToInt(s.readNBytes(4));
        final Script sig = Script.parse(s);
        final int sequence = (int) EndianUtils.littleEndianToInt(s.readNBytes(4));
        return new TxIn(prevTx, prevIndex, sig, sequence);
    }

    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write(Arrays.reverse(prevTx));
            baos.write(EndianUtils.intToLittleEndian(prevIndex, 4));
            baos.write(scriptSig.serialize());
            baos.write(EndianUtils.intToLittleEndian(sequence, 4));
            return baos.toByteArray();
        }
    }

    public Tx fetchTx(final boolean testnet) {
        return TxFetcher.fetch(Hex.toHexString(prevTx), testnet);
    }

    public long value(final boolean testnet) {
        final Tx tx = fetchTx(testnet);
        return tx.getTxOuts()[prevIndex].amount();
    }

    public Script scriptPubkey(final boolean testnet) {
        final Tx tx = fetchTx(testnet);
        return tx.getTxOuts()[prevIndex].scriptPubkey();
    }

}
