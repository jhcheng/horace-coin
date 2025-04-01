package com.horace.coin.tx;

import com.horace.coin.Helper;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.bouncycastle.util.Arrays;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public record Script(byte[]... cmds) {

    public static Script parse(final ByteBuffer buffer) throws Exception {
        // get the length of the entire field
        final int length = (int) EndianUtils.readVarInt(buffer);
        // initialize the number of bytes we've read to 0
        int count = 0;
        // initialize the cmds array
        final List<byte[]> cmds = new ArrayList<byte[]>();
        // loop until we've read length bytes
        while (count < length) {
            // get the current byte
            int current = Byte.toUnsignedInt(buffer.get());
            // increment the bytes we've read
            count += 1;
            // if the current byte is between 1 and 75 inclusive
            if (current >= 1 && current <= 75) {
                // we have an cmd set n to be the current byte
                // add the next n bytes as an cmd
                byte[] cmd = new byte[current];
                buffer.get(cmd);
                cmds.add(cmd);
                // increase the count by n
                count += current;
            } else if (current == 76) {
                // op_pushdata1
                int data_length = Byte.toUnsignedInt(buffer.get());
                byte[] cmd = new byte[data_length];
                buffer.get(cmd);
                cmds.add(cmd);
                count += data_length + 1;
            } else if (current == 77) {
                // op_pushdata2
                byte[] twoBytes = new byte[2];
                buffer.get(twoBytes);
                int data_length = EndianUtils.littleEndianToInt(twoBytes).intValueExact();
                byte[] cmd = new byte[data_length];
                buffer.get(cmd);
                cmds.add(cmd);
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
                if (isOPCode(cmd) >= 0) {
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

    public Script add(Script other) {
        final byte[][] new_cmds = new byte[cmds.length + other.cmds.length][];
        System.arraycopy(cmds, 0, new_cmds, 0, cmds.length);
        System.arraycopy(other.cmds, 0, new_cmds, cmds.length, other.cmds.length);
        return new Script(new_cmds);
    }

    public boolean evaluate(BigInteger z) {
        return evaluate(z, null);
    }

    @SneakyThrows
    public boolean evaluate(BigInteger z, byte[][] witness) {
        Stack<byte[]> stack = new Stack<byte[]>();
        Stack<byte[]> altstack = new Stack<byte[]>();
        List<byte[]> cmdList = java.util.Arrays.stream(cmds).collect(Collectors.toList());
        while (!cmdList.isEmpty()) {
            byte[] cmd = cmdList.remove(0);  // pop(0)
            int code = isOPCode(cmd);
            final OP op = new OP();
            if (code > 0) {
                final Class clazz = op.getClass();
                final String methodName = OP.OP_CODE_NAMES[code].equals("") ? "op_nop" : OP.OP_CODE_NAMES[code].toLowerCase();
                try {
                    final Method method;
                    final Object[] args;
                    if (code == 99 || code == 100) {
                        method = clazz.getMethod(methodName, Stack.class, List.class);
                        args = new Object[]{stack, cmdList};
                    } else if (code == 107 || code == 108) {
                        method = clazz.getMethod(methodName, Stack.class, Stack.class);
                        args = new Object[]{stack, altstack};
                    } else if (code == 172 || code == 173 || code == 174 || code == 175) {
                        method = clazz.getMethod(methodName, Stack.class, BigInteger.class);
                        args = new Object[]{stack, z};
                    } else {
                        method = clazz.getMethod(methodName, Stack.class);
                        args = new Object[]{stack};
                    }
                    final boolean result = (boolean) method.invoke(op, args);
                    if (!result) return false;
                } catch (NoSuchMethodException e) {
                    return false;
                } catch (InvocationTargetException e) {
                    return false;
                } catch (IllegalAccessException e) {
                    return false;
                }
            } else {
                stack.push(cmd);
                /*
                # p2sh rule. if the next three cmds are:
                # OP_HASH160 <20 byte hash> OP_EQUAL this is the RedeemScript
                # OP_HASH160 == 0xa9 and OP_EQUAL == 0x87
                 */
                if (cmdList.size() == 3 && cmdList.get(0)[0] == 0xa9 && cmdList.get(1).length == 20 && cmdList.get(2)[0] == 0x87) {
                    cmdList.remove(cmdList.size() - 1);  // pop
                    byte[] h160 = cmdList.remove(cmdList.size() - 1); // h160 = pop
                    cmdList.remove(cmdList.size() - 1);  // pop
                    if (!op.op_hash160(stack)) return false;
                    stack.push(h160);
                    if (!op.op_equal(stack)) return false;
                    if (!op.op_verify(stack)) return false;
                    byte[] redeem_script = Arrays.concatenate(EndianUtils.encodeVarInt(cmd.length), cmd);
                    cmdList.addAll(List.of(Script.parse(ByteBuffer.wrap(redeem_script)).cmds));
                }
                /*
                # witness program version 0 rule. if stack cmds are:
                # 0 <20 byte hash> this is p2wpkh
                 */
                if (stack.size() == 2 && stack.get(0).length == 0 && stack.get(1).length == 20) {
                    byte[] h160 = stack.pop();
                    stack.pop();
                    cmdList.addAll(List.of(witness));
                    cmdList.addAll(List.of(p2pkh_script(h160).cmds));
                }
                /*
                # witness program version 0 rule. if stack cmds are:
                # 0 <32 byte hash> this is p2wsh
                 */
                if (stack.size() == 2 && stack.get(0).length == 0 && stack.get(1).length == 32) {
                    byte[] s256 = stack.pop();
                    stack.pop();
                    cmdList.addAll(List.of(witness).subList(0, witness.length));
                    byte[] witness_script = witness[witness.length - 1];
                    if (Arrays.areEqual(s256, Helper.hash256(witness_script))) {
                        log.log(Level.INFO, "bad sha256 {} vs {}", new Object[]{HexFormat.of().formatHex(s256), HexFormat.of().formatHex(Helper.hash256(witness_script))});
                        return false;
                    }
                    ByteBuffer buffer = ByteBuffer.allocate(witness_script.length + 8);
                    buffer.put(EndianUtils.encodeVarInt(witness_script.length));
                    buffer.put(witness_script);
                    byte[][] witness_script_cmds = Script.parse(buffer).cmds();
                    cmdList.addAll(List.of(witness_script_cmds));
                }
            }
        }
        if (stack.isEmpty()) return false;
        if (stack.pop().length == 0) return false;
        return true;
    }

    /**
     * @param b
     * @return -1 if not OP_CODE, else return the OP_CODE
     */
    private int isOPCode(byte[] b) {
        if (b.length == 1) {
            final int n = Byte.toUnsignedInt(b[0]);
            if (n == 0 || n > 78) return n;
        }
        return -1;
    }

    private boolean is_p2pkh_script_pubkey() {
        return cmds.length == 5 && cmds[0][0] == 0x76
                && cmds[1][0] == 0xa9
                && (cmds[2].length == 20)
                && cmds[3][0] == 0x88 && cmds[4][0] == 0xa;
    }

    public boolean is_p2sh_script_pubkey() {
        return cmds.length == 3 && cmds[0][0] == 0xa9
                && cmds[1].length == 20
                && cmds[2][0] == 0x87;
    }

    public boolean is_p2wpkh_script_pubkey() {
        return cmds.length == 2 && cmds[0][0] == 0x00
                && cmds[1].length == 20;
    }

    public boolean is_p2wsh_script_pubkey() {
        return cmds.length == 2 && cmds[0][0] == 0x00
                && cmds[1].length == 32;
    }

    public static Script p2pkh_script(byte[] h160) {
        return new Script(new byte[]{0x76}, new byte[]{(byte) 0xa9}, h160, new byte[]{(byte) 0x88}, new byte[]{(byte) 0xac});
    }

    public static Script p2sh_script(byte[] h160) {
        return new Script(new byte[]{(byte) 0xa9}, h160, new byte[]{(byte) 0x87});
    }

    public static Script p2wpkh_script(byte[] h160) {
        return new Script(new byte[]{(byte) 0x00}, h160);
    }

    @Override
    public String toString() {
        HexFormat hex = HexFormat.of();
        ArrayList<String> list = new ArrayList<>();
        for (byte[] cmd : cmds) {
            if (cmd.length == 1 && Byte.toUnsignedInt(cmd[0]) >= 78) {
                int code = Byte.toUnsignedInt(cmd[0]);
                final String name = OP.OP_CODE_NAMES[code];
                if (!"".equals(name)) {
                    list.add(name);
                } else {
                    list.add(String.format("OP_[%d]", code));
                }
            } else {
                list.add(hex.formatHex(cmd));
            }
        }
        return String.join(" ", list);
    }

}
