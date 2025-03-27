package com.horace.coin.network;

import com.horace.coin.Helper;
import com.horace.coin.tx.EndianUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NetworkEnvelope {

    private static final byte[] NETWORK_MAGIC = new byte[]{(byte) 0xf9, (byte) 0xbe, (byte) 0xb4, (byte) 0xd9};
    private static final byte[] TESTNET_NETWORK_MAGIC = new byte[]{0x0b, 0x11, 0x09, 0x07};

    private final byte[] magic;
    private final byte[] command;
    @Getter
    private final byte[] payload;

    public NetworkEnvelope(byte[] command, byte[] payload, boolean testnet) {
        this.magic = testnet ? TESTNET_NETWORK_MAGIC : NETWORK_MAGIC;
        this.command = Arrays.copyOf(command, 12);
        this.payload = payload;
    }

    public String getCommand() {
        return new String(command, StandardCharsets.UTF_8).trim();
    }

    public static NetworkEnvelope parse(ByteBuf buffer) {
        byte[] magic = new byte[4];
        buffer.readBytes(magic);
        byte[] command = new byte[12];
        buffer.readBytes(command);
        byte[] length = new byte[4];
        buffer.readBytes(length);
        int payloadLen = EndianUtils.littleEndianToInt(length).intValue();
        byte[] payloadCheckSum = new byte[4];
        buffer.readBytes(payloadCheckSum);
        byte[] payload = new byte[payloadLen];
        buffer.readBytes(payload);
        boolean testnet = Arrays.equals(magic, TESTNET_NETWORK_MAGIC);
        return new NetworkEnvelope(command, payload, testnet);
    }

    public static NetworkEnvelope parse(ByteBuffer buffer) {
        return parse(Unpooled.wrappedBuffer(buffer));
        /*
        byte[] magic = new byte[4];
        buffer.get(magic);
        byte[] command = new byte[12];
        buffer.get(command);
        byte[] length = new byte[4];
        buffer.get(length);
        int payloadLen = EndianUtils.littleEndianToInt(length).intValue();
        byte[] payloadCheckSum = new byte[4];
        buffer.get(payloadCheckSum);
        byte[] payload = new byte[payloadLen];
        buffer.get(payload);
        boolean testnet = Arrays.equals(magic, TESTNET_NETWORK_MAGIC);
        return new NetworkEnvelope(command, payload, testnet);
         */
    }

    public static NetworkEnvelope parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(magic.length + command.length + 4 + 4 + payload.length);
        buffer.put(magic);
        buffer.put(command);
        buffer.put(EndianUtils.intToLittleEndian(payload.length, 4));
        byte[] checkSum = Arrays.copyOf(Helper.hash256(payload), 4);
        buffer.put(checkSum);
        buffer.put(payload);
        return buffer.array();
    }

}
