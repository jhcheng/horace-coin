package com.horace.coin.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.java.Log;

import java.util.HexFormat;

@Log
public class MessageEncoder extends MessageToByteEncoder<IMessage> {

    private boolean test_net;

    public MessageEncoder(boolean test_net) {
        this.test_net = test_net;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMessage iMessage, ByteBuf byteBuf) throws Exception {
        NetworkEnvelope envelope = new NetworkEnvelope(iMessage.getCommand().getBytes(), iMessage.serialize(), test_net);
        byte[] bytes = envelope.serialize();
        byteBuf.writeBytes(bytes);
        log.info("Send envelope of [" + envelope.getCommand() + "] : " + HexFormat.of().formatHex(bytes));
    }

}
