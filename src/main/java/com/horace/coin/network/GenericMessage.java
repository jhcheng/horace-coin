package com.horace.coin.network;

public record GenericMessage(String command, byte[] payload) {

    public byte[] serialize() {
        return payload;
    }

}
