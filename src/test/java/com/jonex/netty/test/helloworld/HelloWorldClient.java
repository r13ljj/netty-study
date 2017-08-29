package com.jonex.netty.test.helloworld;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HelloWorldClient {

    private  final static String HOST = System.getProperty("host", "127.0.0.1");
    private  final static int PORT = Integer.parseInt(System.getProperty("port", "9999"));
    private  final static int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("decoder", new StringDecoder());
                            socketChannel.pipeline().addLast("encoder", new StringEncoder());
                            socketChannel.pipeline().addLast(new HelloWorldClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
            future.channel().writeAndFlush("Hello Netty Server, i am a common client");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
