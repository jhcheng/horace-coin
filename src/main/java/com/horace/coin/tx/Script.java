package com.horace.coin.tx;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Script {

    @Getter
    private byte[][] cmds;

    public Script(final byte[]... cmds) {
        this.cmds = cmds;
    }

    public static Script parse(final InputStream in) throws Exception {
        // get the length of the entire field
        final int length = (int) EndianUtils.readVarInt(in);
        // initialize the number of bytes we've read to 0
        int count = 0;
        // initialize the cmds array
        final List<byte[]> cmds = new ArrayList<byte[]>();
        // loop until we've read length bytes
        while (count < length) {
            // get the current byte
            int current = Byte.toUnsignedInt(in.readNBytes(1)[0]);
            // increment the bytes we've read
            count += 1;
            // if the current byte is between 1 and 75 inclusive
            if (current >= 1 && current <= 75) {
                // we have an cmd set n to be the current byte
                // add the next n bytes as an cmd
                cmds.add(in.readNBytes(current));
                // increase the count by n
                count += current;
            } else if (current == 76) {
                // op_pushdata1
                int data_length = (int) EndianUtils.littleEndianToInt(in.readNBytes(1));
                cmds.add(in.readNBytes(data_length));
                count += data_length + 1;
            } else if (current == 77) {
                // op_pushdata2
                int data_length = (int) EndianUtils.littleEndianToInt(in.readNBytes(2));
                cmds.add(in.readNBytes(data_length));
                count += data_length + 2;
            } else {
                // we have an opcode. set the current byte to op_code
                // add the op_code to the list of cmds
                cmds.add(new byte[]{(byte) current});
            }
        }
        if (count != length) {
            throw new Exception("parsing script failed");
        }
        return new Script(cmds.toArray(new byte[0][]));
    }

    public byte[] raw_serialize() throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (byte[] cmd : cmds) {
                if (cmd.length == 1 && (Byte.toUnsignedInt(cmd[0]) > 78 || Byte.toUnsignedInt(cmd[0]) == 0)) {
                    out.write(EndianUtils.intToLittleEndian(cmd[0], 1));
                } else {
                    int len = cmd.length;
                    // for large lengths, we have to use a pushdata opcode
                    if (len <= 75) {
                        // turn the length into a single byte integer
                        out.write(EndianUtils.intToLittleEndian(len, 1));
                    } else if (len > 75 && len < 0x100) {
                        //  76 is pushdata1
                        out.write(EndianUtils.intToLittleEndian(76, 1));
                        out.write(EndianUtils.intToLittleEndian(len, 1));
                    } else if (len >= 0x100 && len <= 520) {
                        // 77 is pushdata2
                        out.write(EndianUtils.intToLittleEndian(77, 1));
                        out.write(EndianUtils.intToLittleEndian(len, 2));
                    } else throw new Exception("too long an cmd");
                    out.write(cmd);
                }
            }
            return out.toByteArray();
        }
    }

    @SneakyThrows
    public byte[] serialize() {
        byte[] result = raw_serialize();
        return Arrays.concatenate(EndianUtils.encodeVarInt(result.length), result);
    }

    @Override
    public String toString() {
        ArrayList<String> list = new ArrayList<>();
        for (byte[] cmd : cmds) {
            if (cmd.length == 1 && Byte.toUnsignedInt(cmd[0]) >= 78) {
                int code = Byte.toUnsignedInt(cmd[0]);
                OP.OP_CODE_NAMES names = OP.OP_CODE_NAMES.find(code);
                if (names != null) {
                    list.add(names.toString());
                } else {
                    list.add(String.format("OP_[%d]", code));
                }
            } else {
                list.add(Hex.toHexString(cmd));
            }
        }
        return list.stream().collect(Collectors.joining(" "));
    }

}
