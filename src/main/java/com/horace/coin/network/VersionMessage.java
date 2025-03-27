package com.horace.coin.network;

import com.horace.coin.tx.EndianUtils;
import lombok.Builder;

import java.nio.ByteBuffer;
import java.util.random.RandomGenerator;

@Builder
public class VersionMessage implements IMessage {

    @Builder.Default
    private int version = 70015;
    @Builder.Default
    private long services = 0;
    @Builder.Default
    private long timestamp = System.currentTimeMillis() / 1000;
    @Builder.Default
    private long receiver_services = 0;
    @Builder.Default
    private byte[] receiver_ip = new byte[4];
    @Builder.Default
    private int receiver_port = 8333;
    @Builder.Default
    private long sender_services = 0;
    @Builder.Default
    private byte[] sender_ip = new byte[4];
    @Builder.Default
    private int sender_port = 8333;
    @Builder.Default
    private byte[] nonce = EndianUtils.intToLittleEndian(RandomGenerator.getDefault().nextLong(0, 2 ^ 64), 8);
    @Builder.Default
    private String user_agent = "/programmingbitcoin:0.1/";
    @Builder.Default
    private int last_block = 0;
    @Builder.Default
    private boolean relay = false;

    private static final int FIX_LENGTH = 4 + 8 + 8 + 8 + 16 + 2 + 8 + 16 + 2 + 8 + 4 + 1;
    private static final byte[] IP_PREFIX = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0xff, (byte) 0xff};

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(FIX_LENGTH + user_agent.getBytes().length + 9);
        buffer.put(EndianUtils.intToLittleEndian(version, 4));
        buffer.put(EndianUtils.intToLittleEndian(services, 8));
        buffer.put(EndianUtils.intToLittleEndian(timestamp, 8));
        buffer.put(EndianUtils.intToLittleEndian(receiver_services, 8));
        buffer.put(IP_PREFIX);
        buffer.put(receiver_ip);
        byte[] rPort = EndianUtils.intToLittleEndian(receiver_port, 2);
        buffer.put(new byte[]{rPort[1], rPort[0]});
        buffer.put(EndianUtils.intToLittleEndian(sender_services, 8));
        buffer.put(IP_PREFIX);
        buffer.put(sender_ip);
        byte[] sPort = EndianUtils.intToLittleEndian(sender_port, 2);
        buffer.put(new byte[]{sPort[1], sPort[0]});
        buffer.put(nonce);
        buffer.put(EndianUtils.encodeVarInt(user_agent.getBytes().length));
        buffer.put(user_agent.getBytes());
        buffer.put(EndianUtils.intToLittleEndian(last_block, 4));
        buffer.put((byte) (relay ? 0x01 : 0x00));
        byte[] result = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, result, 0, result.length);
        return result;
    }

    public static VersionMessage parse(byte[] bytes) {
        return VersionMessage.builder().build();
    }

    @Override
    public String getCommand() {
        return IMessage.VERSION_COMMAND;
    }

}
