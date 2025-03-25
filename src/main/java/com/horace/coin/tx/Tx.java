package com.horace.coin.tx;

import com.horace.coin.Helper;
import com.horace.coin.ecc.PrivateKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class Tx implements Serializable {

    private final int version;
    private final TxIn[] txIns;
    private final TxOut[] txOuts;
    @Setter
    private int lockTime;
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

    public static Tx parse(final InputStream in, final boolean testnet) throws IOException {
        final int version = EndianUtils.littleEndianToInt(in.readNBytes(4)).intValueExact();
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
        final int lockTime = EndianUtils.littleEndianToInt(in.readNBytes(4)).intValueExact();
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

    public BigInteger sig_hash(int input_index, Script redeem_script) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            out.write(EndianUtils.intToLittleEndian(version, 4));
            out.write(EndianUtils.encodeVarInt(txIns.length));
            for (int i = 0; i < txIns.length; i++) {
                final TxIn txIn = txIns[i];
                Script script_sig;
                if (i == input_index) {
                    if (redeem_script != null) {
                        script_sig = redeem_script;
                    } else {
                        script_sig = txIn.scriptPubkey(testnet);
                    }
                } else {
                    script_sig = new Script();
                }
                out.write(new TxIn(txIn.getPrevTx(), txIn.getPrevIndex(), script_sig, txIn.getSequence()).serialize());
            }
            out.write(EndianUtils.encodeVarInt(txOuts.length));
            for (TxOut txOut : txOuts) {
                out.write(txOut.serialize());
            }
            out.write(EndianUtils.intToLittleEndian(lockTime, 4));
            out.write(EndianUtils.intToLittleEndian(Helper.SIGHASH_ALL, 4));
            byte[] h256 = Helper.hash256(out.toByteArray());
            return BigIntegers.fromUnsignedByteArray(h256);
        }
    }

    public BigInteger sig_hash(int input_index) throws IOException {
        return sig_hash(input_index, null);
    }

    @SneakyThrows
    public boolean verify_input(int input_index) {
        TxIn txIn = txIns[input_index];
        Script script_pubkey = txIn.scriptPubkey(testnet);
        Script redeem_script;
        if (script_pubkey.is_p2sh_script_pubkey()) {
            byte[] cmd = txIn.getScriptSig().cmds()[txIn.getScriptSig().cmds().length - 1];
            byte[] raw_redeem = Arrays.concatenate(EndianUtils.encodeVarInt(cmd.length), cmd);
            redeem_script = Script.parse(new ByteArrayInputStream(raw_redeem));
        } else redeem_script = null;
        BigInteger z = sig_hash(input_index, redeem_script);
        Script combined = txIn.getScriptSig().add(script_pubkey);
        return combined.evaluate(z);
    }

    public boolean verify() {
        if (fee() < 0) return false;
        for (int i = 0 ; i < txIns.length ; i++) {
            if (!verify_input(i)) return false;
        }
        return true;
    }

    @SneakyThrows
    public boolean sign_input(int input_index, PrivateKey privateKey) {
        BigInteger z = sig_hash(input_index);
        byte[] der = privateKey.sign(z).der();
        byte[] sig = Arrays.concatenate(der, new byte[]{(byte) Helper.SIGHASH_ALL});
        byte[] sec = privateKey.getPoint().sec();
        txIns[input_index].setScriptSig(new Script(sig, sec));
        return verify_input(input_index);
    }

    public boolean is_coinbase() {
        return txIns.length == 1
                && java.util.Arrays.equals(txIns[0].getPrevTx(), new byte[32])
                && txIns[0].getPrevIndex() == 0xffffffff
                ;
    }

    public Integer coinbase_height() {
        if (!is_coinbase()) return null;
        return EndianUtils.littleEndianToInt(txIns[0].getScriptSig().cmds()[0]).intValueExact();
    }

}
