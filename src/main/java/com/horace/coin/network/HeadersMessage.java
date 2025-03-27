package com.horace.coin.network;

import com.horace.coin.tx.Block;
import com.horace.coin.tx.EndianUtils;
import lombok.Getter;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HeadersMessage implements IMessage {

    @Getter
    private final Block[] blocks;

    public HeadersMessage(Block[] blocks) {
        this.blocks = blocks;
    }

    public static HeadersMessage parse(final byte[] bytes) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int num_headers = (int) EndianUtils.readVarInt(buffer);
        Block[] blocks = new Block[num_headers];
        for (int i = 0; i < num_headers; i++) {
            blocks[i] = Block.parse(buffer);
            int num_txs = (int) EndianUtils.readVarInt(buffer);
            if (num_txs != 0)
                throw new IOException(String.format("Invalid number of transactions in header: %d, should be 0", num_txs));
        }
        return new HeadersMessage(blocks);
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    @Override
    public String getCommand() {
        return IMessage.HEADERS_COMMAND;
    }

}
