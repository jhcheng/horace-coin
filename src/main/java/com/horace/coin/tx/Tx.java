package com.horace.coin.tx;

import com.horace.coin.ecc.Helper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

@AllArgsConstructor
public class Tx implements Serializable {

    @Getter
    private final int version;
    @Getter
    private final TxIn[] txIns;
    @Getter
    private final TxOut[] txOuts;
    @Getter
    @Setter
    private final int lockTime;
    @Getter
    private final boolean testnet;

    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write(EndianUtils.intToLittleEndian(version, 4));
            baos.write(EndianUtils.encodeVarInt(txIns.length));
            for (TxIn txIn : txIns) {
                baos.write(txIn.serialize());
            }
            baos.write(EndianUtils.encodeVarInt(txOuts.length));
            for (TxOut txOut : txOuts) {
                baos.write(txOut.serialize());
            }
            baos.write(EndianUtils.intToLittleEndian(lockTime, 4));
            return baos.toByteArray();
        }
    }

    @SneakyThrows
    public byte[] hash() {
        //  hash256(self.serialize())[::-1]
        return Arrays.reverse(Helper.hash256(serialize()));
    }

    public String id() {
        return Hex.toHexString(hash());
    }

    /*
    public static Tx parse(byte[] data) {
        final TxBuilder txBuilder = new TxBuilder();
        txBuilder.version((int) EndianUtils.littleEndianToInt(Arrays.copyOfRange(data, 0, 4)));
        return txBuilder.build();
    }
     */

    public static Tx parse(final InputStream in, final boolean testnet) throws IOException {
        final int version = (int) EndianUtils.littleEndianToInt(in.readNBytes(4));
        final int num_inputs = (int) EndianUtils.readVarInt(in);
        final TxIn[] txIns = new TxIn[num_inputs];
        for (int i = 0; i < num_inputs; i++) {
            txIns[i] = TxIn.psrse(in);
        }
        final int num_outputs = (int) EndianUtils.readVarInt(in);
        final TxOut[] txOuts = new TxOut[num_outputs];
        for (int i = 0; i < num_outputs; i++) {
            txOuts[i] = TxOut.parse(in);
        }
        final int lockTime = (int) EndianUtils.littleEndianToInt(in.readNBytes(4));
        return new Tx(version, txIns, txOuts, lockTime, testnet);
    }

    public static Tx parse(final InputStream in) throws IOException {
        return parse(in, false);
    }

    public long fee() {
        long input_sum = 0, output_sum = 0;
        for (TxIn txIn : txIns) {
            input_sum += txIn.value(testnet);
        }
        for (TxOut txOut : txOuts) {
            output_sum += txOut.amount();
        }
        return input_sum - output_sum;
    }

}
