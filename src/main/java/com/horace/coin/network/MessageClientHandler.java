package com.horace.coin.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.java.Log;

@Log
public class MessageClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IMessage message = (IMessage) msg;
        switch (message.getCommand()) {
            case IMessage.VERSION_COMMAND:
                ctx.writeAndFlush(new VerAckMessage());
                break;
            case IMessage.PING_COMMAND:
                PingMessage pingMessage = (PingMessage) message;
                ctx.writeAndFlush(new PongMessage(pingMessage.getNonce()));
                break;
            default:
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        ctx.writeAndFlush(VersionMessage.builder().build());
    }

    public void sendMessage(IMessage message) {
        ctx.writeAndFlush(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
