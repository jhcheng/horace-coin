package com.horace.coin.network;

public class VerAckMessage implements IMessage {

    public static VerAckMessage parse(byte[] data) {
        return new VerAckMessage();
    }

    public byte[] serialize() {
        return new byte[0];
    }

    @Override
    public String getCommand() {
        return IMessage.VER_ACK_COMMAND;
    }

}
