package com.horace.coin.tx;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bouncycastle.util.Arrays;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;

/**
 * prev_tx is 32 bytes, little endian
 * prev_index is an integer in 4 bytes, little endian
 * use Script.parse to get the ScriptSig
 * sequence is an integer in 4 bytes, little-endian
 */
@AllArgsConstructor
@Getter
public class TxIn {

    private final byte[] prevTx;
    private final int prevIndex;
    @Setter
    private Script scriptSig;
    private final long sequence;

    public TxIn(byte[] prevTx, int prevIndex) {
        this(prevTx, prevIndex, new Script(),  4294967295L);
    }

    @SneakyThrows
    public static TxIn psrse(final InputStream s) {
        final byte[] prevTx = Arrays.reverse(s.readNBytes(32));
        final int prevIndex = EndianUtils.littleEndianToInt(s.readNBytes(4)).intValue();
        final Script sig = Script.parse(s);
        final int sequence = EndianUtils.littleEndianToInt(s.readNBytes(4)).intValue();
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
        HexFormat hexFormat = HexFormat.of();
        return TxFetcher.fetch(hexFormat.formatHex(prevTx), testnet);
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
