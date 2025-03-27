package com.horace.coin.network;

public class PongMessage implements IMessage {

    private final byte[] nonce;

    public PongMessage(byte[] nonce) {
        this.nonce = nonce;
    }

    public static PongMessage parse(byte[] data) {
        return new PongMessage(data);
    }

    public byte[] serialize() {
        return nonce;
    }

    @Override
    public String getCommand() {
        return IMessage.PONG_COMMAND;
    }

}
