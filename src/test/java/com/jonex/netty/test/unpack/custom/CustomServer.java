package com.jonex.netty.test.unpack.custom;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/8/31 14:14
 */
public class CustomServer {

    private final static int MAX_FRAME_LENGTH = 1024 * 1024;
    private final static int LENGTH_FIELD_LENGTH = 4;
    private final static int LENGTH_FIELD_OFFSET = 2;
    private final static int LENGTH_ADJUSTMENT = 0;
    private final static int INITAL_BYTES_TO_STRIP = 0;

    private int port;

    public CustomServer(int port) {
        this.port = port;
    }

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new CustomDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_ADJUSTMENT, INITAL_BYTES_TO_STRIP, false));
                            socketChannel.pipeline().addLast(new CustomServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Server start listen at "+ port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 9999;
        }
        new CustomServer(port).start();
    }

}
