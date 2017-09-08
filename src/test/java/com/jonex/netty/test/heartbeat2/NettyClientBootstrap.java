package com.jonex.netty.test.heartbeat2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.TimeUnit;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/8 9:39
 */
public class NettyClientBootstrap {


    private String host;
    private int port;

    private SocketChannel socketChannel;

    private final static EventExecutorGroup executorGroup = new DefaultEventExecutorGroup(20);



    public NettyClientBootstrap(String host, int port) throws Exception{
        this.host = host;
        this.port = port;
        start();
    }

    private void start()throws Exception{
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.remoteAddress(host, port);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new IdleStateHandler(20, 10, 0));
                ch.pipeline().addLast(new ObjectEncoder());
                ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                ch.pipeline().addLast(new NettyClientHandler());
            }
        });
        ChannelFuture future = bootstrap.connect(host, port).sync();
        if (future.isSuccess()){
            socketChannel = (SocketChannel) future.channel();
            System.out.println("client connect sucess!");
        }
    }

    public static void main(String[] args) throws Exception{
        Constants.setClientId("001");
        NettyClientBootstrap bootstrap = new NettyClientBootstrap("localhost", 18080);

        LoginMsg loginMsg = new LoginMsg();
        loginMsg.setPassword("yao");
        loginMsg.setUserName("robin");
        loginMsg.setClientId(Constants.getClientId());
        bootstrap.socketChannel.writeAndFlush(loginMsg);
        while (true) {
            TimeUnit.SECONDS.sleep(3);
            AskMsg askMsg = new AskMsg();
            AskParams askParams = new AskParams();
            askParams.setAuth("authToken");
            askMsg.setParams(askParams);
            bootstrap.socketChannel.writeAndFlush(askMsg);
        }
    }

}
