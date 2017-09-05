package com.jonex.netty.test.idle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by Guest on 2017/9/5.
 */
public class HeartbeatClient {

    private final HashedWheelTimer timer = new HashedWheelTimer();

    private Bootstrap bootstrap;

    private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();

    public void connect(int port, String host) throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO));
        final ConnectionWatchDog watchDog = new ConnectionWatchDog(timer, bootstrap, host, port, true) {
            public ChannelHandler[] handlers() {
                return new ChannelHandler[]{
                    this,
                    new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                    idleStateTrigger,
                    new StringDecoder(),
                    new StringEncoder(),
                    new HeartbeatClientHandler()
                };
            }
        };

        //连接
        ChannelFuture future;
        try {
            synchronized (bootstrap){
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //初始化handler
                        ch.pipeline().addLast(watchDog.handlers());
                    }
                });
                future = bootstrap.connect(host, port);
            }
//            future.sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new Exception("connenct fail ");
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new HeartbeatClient().connect(port, "127.0.0.1");
    }

}
