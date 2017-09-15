package com.jonex.netty.test.production;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/8 18:59
 */
public abstract class ConnnectionWatchDog extends ChannelHandlerAdapter implements TimerTask, ChannelHandlerHolder{

    private Logger logger = LoggerFactory.getLogger(ConnnectionWatchDog.class);

    private volatile boolean retry = false;
    private int retrys = 0;

    private final Bootstrap bootstrap;
    private final Timer timer;
    private final String host;
    private final int port;

    public ConnnectionWatchDog(Bootstrap bootstrap, Timer timer, String host, int port) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.host = host;
        this.port = port;
    }

    public void canRetry(boolean flag){
        this.retry = flag;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        retrys = 0;
        logger.info("Connects with {}.", channel);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel inactive:{}, port: {},host {}.", ctx.channel(), ctx.channel(), port,host);
        boolean doRetry = retry;
        if (doRetry) {
            if (retrys < 12) {
                retrys ++;
                long timeout = 2 << retrys;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            } else {
                System.out.println("");
                ctx.channel().close();
            }
        }
        logger.warn("Disconnects with {}, port: {},host {}, reconnect: {}.", ctx.channel(), port,host, doRetry);
        ctx.fireChannelInactive();
    }


    @Override
    public void run(Timeout timeout) throws Exception {
        ChannelFuture future = null;
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host, port).sync();
        }
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                boolean succeed = future.isSuccess();
                logger.warn("Reconnects with {}, {}.", host+":"+port, succeed ? "succeed" : "failed");
                if (!future.isSuccess()) {
                    future.channel().pipeline().fireChannelInactive();
                }
            }
        });
    }
}
