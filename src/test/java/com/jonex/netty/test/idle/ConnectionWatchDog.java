package com.jonex.netty.test.idle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/5 17:02
 */
public abstract class ConnectionWatchDog extends ChannelHandlerAdapter implements TimerTask, ChannelHandlerHolder {

    private volatile boolean retry = true;
    private int retrys;

    private final Timer timer;
    private final Bootstrap bootstrap;

    private final String host;
    private final int port;

    public ConnectionWatchDog(Timer timer, Bootstrap bootstrap, String host, int port) {
        this.timer = timer;
        this.bootstrap = bootstrap;
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("当前链路已经被激活，重连重试次数置为0");
        retrys = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("链路关闭");
        if (retry) {
            System.out.println("链路关闭，重试连接");
            if(retrys < 12){
                retrys ++;
                //重连的间隔时间会越来越长
                int timeout = 2 << retrys;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }
        ctx.fireChannelInactive();
    }

    public void run(Timeout timeout) throws Exception {
        ChannelFuture future;
        //重连填入handlers
        synchronized (bootstrap){
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host, port);
        }
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                boolean succeed = future.isSuccess();
                if (!succeed){
                    System.out.println("重连失败");
                    future.channel().pipeline().fireChannelInactive();
                } else {
                    System.out.println("重连成功");
                }
            }
        });
    }

    public static void main(String[] args) {
        System.out.println(2 << 0);
        System.out.println(2 << 1);
        System.out.println(2 << 2);
        System.out.println(2 << 3);
        System.out.println(2 << 4);
        System.out.println(2 << 5);
        System.out.println(2 << 6);
        System.out.println(2 << 7);
        System.out.println(2 << 8);
        System.out.println(2 << 9);
        System.out.println(2 << 10);
    }
}
