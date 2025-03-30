package com.horace.coin.network;

import com.horace.coin.tx.EndianUtils;
import lombok.AllArgsConstructor;
import org.bouncycastle.util.Arrays;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GetDataMessage implements IMessage {

    List<Data> data = new ArrayList<>();
    int dataSize = 0;

    @Override
    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(dataSize + 8);
        buffer.put(EndianUtils.encodeVarInt(data.size()));
        for (Data data : data) {
            buffer.put(EndianUtils.intToLittleEndian(data.type, 4));
            buffer.put(Arrays.reverse(data.identifier));
        }
        byte[] result = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, result, 0, result.length);
        return result;
    }

    @Override
    public String getCommand() {
        return GET_DATA_COMMAND;
    }

    public void addData(int type, byte[] identifier) {
        data.add(new Data(type, identifier));
        dataSize += identifier.length + 4;
    }

    @AllArgsConstructor
    public static class Data {
        private final int type;
        private final byte[] identifier;
    }

}
