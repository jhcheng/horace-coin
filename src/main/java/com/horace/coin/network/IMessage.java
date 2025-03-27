package com.horace.coin.network;

import lombok.extern.java.Log;

import java.io.IOException;

public interface IMessage {

    String GET_HEADERS_COMMAND = "getheaders";
    String HEADERS_COMMAND = "headers";
    String PING_COMMAND = "ping";
    String PONG_COMMAND = "pong";
    String VER_ACK_COMMAND = "verack";
    String VERSION_COMMAND = "version";

    byte[] serialize();

    String getCommand();

    @Log
    class MessageParser {

        public static IMessage parse(String command, byte[] bytes) throws IOException {
            switch (command) {
                case HEADERS_COMMAND:
                    return HeadersMessage.parse(bytes);
                case PING_COMMAND:
                    return PingMessage.parse(bytes);
                case PONG_COMMAND:
                    return PongMessage.parse(bytes);
                case VER_ACK_COMMAND:
                    return VerAckMessage.parse(bytes);
                case VERSION_COMMAND:
                    return VersionMessage.parse(bytes);
                default:
                    log.warning("Unknown command: " + command);
                    return null;
            }
        }

    }

}
