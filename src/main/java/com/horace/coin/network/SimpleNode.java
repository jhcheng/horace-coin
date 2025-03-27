package com.horace.coin.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SimpleNode {

    private static final int DEFAULT_MAIN_PORT = 8333;
    private static final int DEFAULT_TEST_PORT = 18333;

    private String host;
    private int port = DEFAULT_MAIN_PORT;
    private boolean test_net = false;
    private final MessageClientHandler clientHandler = new MessageClientHandler();

    public SimpleNode(String host, int port) {
        this.host = host;
        this.port = port;
        connect();
    }

    public SimpleNode(String host) {
        this.host = host;
        connect();
    }

    public SimpleNode(String host, boolean test_net) {
        this.host = host;
        this.port = test_net ? DEFAULT_TEST_PORT : DEFAULT_MAIN_PORT;
        this.test_net = test_net;
        connect();
    }

    private void connect() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MessageEncoder(test_net));
                    ch.pipeline().addLast(new MessageDecoder());
                    ch.pipeline().addLast(clientHandler);
                }
            });
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public void handshake() {
        clientHandler.sendMessage(VersionMessage.builder().build());
    }

    public static void main(String[] args) {
        SimpleNode simpleNode = new SimpleNode(AdvancedNSLookup.pickNode());
    }

}
