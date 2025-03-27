package com.horace.coin.network;

import com.horace.coin.tx.EndianUtils;
import lombok.Builder;
import org.bouncycastle.util.Arrays;

import java.nio.ByteBuffer;

@Builder
public class GetHeadersMessage implements IMessage {

    @Override
    public String getCommand() {
        return IMessage.GET_HEADERS_COMMAND;
    }

    @Builder.Default
    private int version = 70015;
    @Builder.Default
    private int num_hashes = 1;
    private byte[] start_block;
    @Builder.Default
    private byte[] end_block = new byte[32];

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(8 + 9 + start_block.length + end_block.length);
        buffer.put(EndianUtils.intToLittleEndian(version, 4));
        buffer.put(EndianUtils.encodeVarInt(num_hashes));
        buffer.put(Arrays.reverse(start_block));
        buffer.put(Arrays.reverse(end_block));
        byte[] result = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, result, 0, result.length);
        return result;
    }

}
