package com.horace.coin.network;

import lombok.Getter;

public class PingMessage implements IMessage {

    @Getter
    private final byte[] nonce;

    public PingMessage(byte[] nonce) {
        this.nonce = nonce;
    }

    public static PingMessage parse(byte[] data) {
        return new PingMessage(data);
    }

    public byte[] serialize() {
        return nonce;
    }

    @Override
    public String getCommand() {
        return IMessage.PING_COMMAND;
    }

}
