package com.horace.coin.tx;

import com.horace.coin.Helper;
import com.horace.coin.MurmurHash3;
import com.horace.coin.network.GenericMessage;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class BloomFilter {

    //public static int BIP37_CONSTANT = 0xfba4c795;
    private static final BigInteger BIP37_CONSTANT = new BigInteger("fba4c795", 16); // 0xfba4c795


    private final int size;
    private final int function_count;
    private final int tweak;
    private final byte[] bit_field;

    public BloomFilter(int size, int function_count, int tweak) {
        this.size = size;
        this.function_count = function_count;
        this.tweak = tweak;
        this.bit_field = new byte[size * 8];
    }

    public void add(byte[] item) {
        for (int i = 0; i < function_count; i++) {
            BigInteger seed = BigInteger.valueOf(i).multiply(BIP37_CONSTANT).add(BigInteger.valueOf(tweak));
            BigInteger h = MurmurHash3.murmurhash3_x86_32(item, seed);
            int bit = h.mod(BigInteger.valueOf(size*8)).intValue();
            bit_field[bit] = 1;
        }
    }

    public byte[] filter_bytes() {
        return Helper.bit_field_to_bytes(bit_field);
    }

    public GenericMessage filterload() {
        return filterload(1);
    }

    public GenericMessage filterload(int flag) {
        ByteBuffer buffer = ByteBuffer.allocate(8 + bit_field.length + 4 + 4 + 1);
        buffer.put(EndianUtils.encodeVarInt(size));
        buffer.put(filter_bytes());
        buffer.put(EndianUtils.intToLittleEndian(function_count, 4));
        buffer.put(EndianUtils.intToLittleEndian(tweak, 4));
        buffer.put(EndianUtils.intToLittleEndian(flag, 1));
        byte[] payload = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, payload, 0, payload.length);
        return new GenericMessage("filterload", payload);
    }

}
