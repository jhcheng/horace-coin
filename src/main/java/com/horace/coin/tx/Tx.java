package com.horace.coin.tx;

import com.horace.coin.Helper;
import com.horace.coin.ecc.PrivateKey;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HexFormat;

@Getter
public class Tx {

    private final int version;
    private final TxIn[] txIns;
    private final TxOut[] txOuts;
    @Setter
    private int lockTime;
    private final boolean testnet;
    private final boolean segwit;
    private byte[] _hash_prevouts = null;
    private byte[] _hash_sequence = null;
    private byte[] _hash_outputs = null;

    public Tx(int version, TxIn[] txIns, TxOut[] txOuts, int lockTime, boolean testnet, boolean segwit) {
        this.version = version;
        this.txIns = txIns;
        this.txOuts = txOuts;
        this.lockTime = lockTime;
        this.testnet = testnet;
        this.segwit = segwit;
    }

    public Tx(int version, TxIn[] txIns, TxOut[] txOuts, int lockTime, boolean testnet) {
        this(version, txIns, txOuts, lockTime, testnet, false);
    }

    public Tx(int version, TxIn[] txIns, TxOut[] txOuts, int lockTime) {
        this(version, txIns, txOuts, lockTime, false, false);
    }

    @SneakyThrows
    public byte[] serialize() {
        return segwit ? serialize_segwit() : serialize_legacy();
    }

    private byte[] serialize_legacy() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);  // 100K
        buffer.put(EndianUtils.intToLittleEndian(version, 4));
        buffer.put(EndianUtils.encodeVarInt(txIns.length));
        for (TxIn txIn : txIns) {
            buffer.put(txIn.serialize());
        }
        buffer.put(EndianUtils.encodeVarInt(txOuts.length));
        for (TxOut txOut : txOuts) {
            buffer.put(txOut.serialize());
        }
        buffer.put(EndianUtils.intToLittleEndian(lockTime, 4));
        byte[] result = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, result, 0, result.length);
        return result;
    }

    private byte[] serialize_segwit() {
        return new byte[0];
    }

    @SneakyThrows
    public byte[] hash() {
        //  hash256(self.serialize())[::-1]
        return Arrays.reverse(Helper.hash256(serialize_legacy()));
    }

    public String id() {
        return Hex.toHexString(hash());
    }

    public static Tx parse(final ByteBuffer buffer, final boolean testnet) throws IOException {
        byte[] fourBytes = new byte[4];
        buffer.get(fourBytes);
        boolean segwit = (buffer.get() == 0x00) ? true : false;
        // rewind back 5 bytes
        buffer.rewind();
        return segwit ? parse_seqwit(buffer, testnet) : parse_legacy(buffer, testnet);
    }

    public static Tx parse(final ByteBuffer buffer) throws IOException {
        return parse(buffer, false);
    }

    private static Tx parse_legacy(final ByteBuffer buffer, final boolean testnet) throws IOException {
        byte[] fourBytes = new byte[4];
        buffer.get(fourBytes);
        final int version = EndianUtils.littleEndianToInt(fourBytes).intValueExact();
        final int num_inputs = (int) EndianUtils.readVarInt(buffer);
        final TxIn[] txIns = new TxIn[num_inputs];
        for (int i = 0; i < num_inputs; i++) {
            txIns[i] = TxIn.psrse(buffer);
        }
        final int num_outputs = (int) EndianUtils.readVarInt(buffer);
        final TxOut[] txOuts = new TxOut[num_outputs];
        for (int i = 0; i < num_outputs; i++) {
            txOuts[i] = TxOut.parse(buffer);
        }
        buffer.get(fourBytes);
        final int lockTime = EndianUtils.littleEndianToInt(fourBytes).intValueExact();
        return new Tx(version, txIns, txOuts, lockTime, testnet);
    }

    private static Tx parse_seqwit(final ByteBuffer buffer, boolean testnet) throws IOException {
        byte[] fourBytes = new byte[4];
        byte[] twoBytes = new byte[2];
        buffer.get(fourBytes);
        int version = EndianUtils.littleEndianToInt(fourBytes).intValueExact();
        buffer.get(twoBytes);
        short marker = EndianUtils.littleEndianToInt(twoBytes).shortValueExact();
        if (marker != 0x0001) {
            throw new RuntimeException("Not a segwit transaction: " + HexFormat.of().formatHex(twoBytes));
        }
        int num_inputs = (int) EndianUtils.readVarInt(buffer);
        TxIn[] txIns = new TxIn[num_inputs];
        for (int i = 0; i < num_inputs; i++) {
            txIns[i] = TxIn.psrse(buffer);
        }
        int num_outputs = (int) EndianUtils.readVarInt(buffer);
        TxOut[] txOuts = new TxOut[num_outputs];
        for (int i = 0; i < num_outputs; i++) {
            txOuts[i] = TxOut.parse(buffer);
        }
        for (TxIn input : txIns) {
            int num_items = (int) EndianUtils.readVarInt(buffer);
            byte[][] items = new byte[num_items][];
            for (int i = 0; i < num_items; i++) {
                int item_len = (int) EndianUtils.readVarInt(buffer);
                if (item_len == 0) {
                    items[i] = new byte[]{0};
                } else {
                    items[i] = new byte[item_len];
                    buffer.get(items[i]);
                }
            }
            input.setWitness(items);
        }
        buffer.get(fourBytes);
        int lockTime = EndianUtils.littleEndianToInt(fourBytes).intValueExact();
        return new Tx(version, txIns, txOuts, lockTime, testnet, true);
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

    private byte[] hash_prevouts() {
        if (_hash_prevouts == null) {
            ByteBuffer all_prevouts = ByteBuffer.allocate((4 + 32) * txIns.length);
            ByteBuffer all_sequence = ByteBuffer.allocate(4 * txIns.length);
            for (TxIn txIn : txIns) {
                all_prevouts.put(Arrays.reverse(txIn.getPrevTx()));
                all_prevouts.put(EndianUtils.intToLittleEndian(txIn.getSequence(), 4));
                all_sequence.put(EndianUtils.intToLittleEndian(txIn.getSequence(), 4));
            }
            _hash_prevouts = Helper.hash256(all_prevouts.array());
            _hash_sequence = Helper.hash256(all_sequence.array());
        }
        return _hash_prevouts;
    }

    private byte[] hash_sequence() {
        if (_hash_sequence == null) {
            hash_outputs();
        }
        return _hash_sequence;
    }

    private byte[] hash_outputs() {
        if (_hash_outputs == null) {
            ByteBuffer all_outputs = ByteBuffer.allocate(102400 * txOuts.length);
            for (TxOut txOut : txOuts) {
                all_outputs.put(txOut.serialize());
            }
            byte[] result = new byte[all_outputs.position()];
            System.arraycopy(all_outputs.array(), 0, result, 0, result.length);
            _hash_outputs = Helper.hash256(result);
        }
        return _hash_outputs;
    }

    public BigInteger sig_hash_bip143(int input_index, Script redeem_script, Script witness_script) throws IOException {
        TxIn txIn = txIns[input_index];
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1000);
        buffer.put(EndianUtils.intToLittleEndian(version, 4));
        buffer.put(hash_prevouts());
        buffer.put(hash_sequence());
        buffer.put(Arrays.reverse(txIn.getPrevTx()));
        buffer.put(EndianUtils.intToLittleEndian(txIn.getPrevIndex(), 4));
        if (witness_script != null) {
            buffer.put(witness_script.serialize());
        } else if (redeem_script != null) {
            buffer.put(Script.p2pkh_script(redeem_script.cmds()[1]).serialize());
        } else {
            buffer.put(Script.p2pkh_script(txIn.scriptPubkey(testnet).cmds()[1]).serialize());
        }
        buffer.put(EndianUtils.intToLittleEndian(txIn.value(testnet), 8));
        buffer.put(EndianUtils.intToLittleEndian(txIn.getSequence(), 4));
        buffer.put(hash_outputs());
        buffer.put(EndianUtils.intToLittleEndian(lockTime, 4));
        buffer.put(EndianUtils.intToLittleEndian(Helper.SIGHASH_ALL, 4));
        byte[] result = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, result, 0, result.length);
        return BigIntegers.fromUnsignedByteArray(Helper.hash256(result));
    }

    public BigInteger sig_hash_bip143(int input_index, Script redeem_script) throws IOException {
        return sig_hash_bip143(input_index, redeem_script, null);
    }

    public BigInteger sig_hash_bip143(int input_index) throws IOException {
        return sig_hash_bip143(input_index, null, null);
    }

    @SneakyThrows
    public boolean verify_input(int input_index) {
        TxIn txIn = txIns[input_index];
        Script script_pubkey = txIn.scriptPubkey(testnet);
        Script redeem_script;
        BigInteger z;
        byte[][] witness;
        if (script_pubkey.is_p2sh_script_pubkey()) {
            byte[] cmd = txIn.getScriptSig().cmds()[txIn.getScriptSig().cmds().length - 1];
            byte[] raw_redeem = Arrays.concatenate(EndianUtils.encodeVarInt(cmd.length), cmd);
            redeem_script = Script.parse(ByteBuffer.wrap(raw_redeem));
            if (redeem_script.is_p2wpkh_script_pubkey()) {
                z = sig_hash_bip143(input_index, redeem_script);
                witness = txIn.getWitness();
            } else if (redeem_script.is_p2wsh_script_pubkey()) {
                cmd = txIn.getWitness()[txIn.getWitness().length - 1];
                byte[] raw_witness = Arrays.concatenate(EndianUtils.encodeVarInt(cmd.length), cmd);
                Script witness_script = Script.parse(ByteBuffer.wrap(raw_witness));
                z = sig_hash_bip143(input_index, witness_script);
                witness = txIn.getWitness();
            } else {
                z = sig_hash(input_index, redeem_script);
                witness = null;
            }
        } else {
            if (script_pubkey.is_p2wpkh_script_pubkey()) {
                z = sig_hash_bip143(input_index);
                witness = txIn.getWitness();
            } else if (script_pubkey.is_p2wsh_script_pubkey()) {
                byte[] cmd = txIn.getWitness()[txIn.getWitness().length - 1];
                byte[] raw_witness = Arrays.concatenate(EndianUtils.encodeVarInt(cmd.length), cmd);
                Script witness_script = Script.parse(ByteBuffer.wrap(raw_witness));
                z = sig_hash_bip143(input_index, witness_script);
                witness = txIn.getWitness();
            } else {
                z = sig_hash(input_index);
                witness = null;
            }
        }
        Script combined = txIn.getScriptSig().add(script_pubkey);
        return combined.evaluate(z, witness);
    }

    public boolean verify() {
        if (fee() < 0) return false;
        for (int i = 0; i < txIns.length; i++) {
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
