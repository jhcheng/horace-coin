package com.horace.coin.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.java.Log;

import java.util.HexFormat;
import java.util.List;

@Log
public class MessageDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        NetworkEnvelope envelope = NetworkEnvelope.parse(byteBuf);
        log.info("Received envelope of [" + envelope.getCommand() + "]: " + HexFormat.of().formatHex(envelope.serialize()));
        IMessage message = IMessage.MessageParser.parse(envelope.getCommand(), envelope.getPayload());
        if (message != null) {
            list.add(message);
        }
    }
}
