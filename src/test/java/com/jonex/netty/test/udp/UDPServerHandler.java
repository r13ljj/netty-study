package com.jonex.netty.test.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final static HashedWheelTimer timer = new HashedWheelTimer(new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread("UDPServerTimer"+threadIndex.getAndIncrement());
        }
    });

    private final static ScheduledThreadPoolExecutor scheduleThreadPool = new ScheduledThreadPoolExecutor(10, new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread("UDPServerScheduleThread"+threadIndex.getAndIncrement());
        }
    });



    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        //读取接收的数据
        ByteBuf buf = datagramPacket.duplicate().content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String body = new String(bytes, CharsetUtil.UTF_8);
        System.out.println("【NOTE】>>>>>> 收到客户端"+datagramPacket.sender()+"的数据："+body);
        /*//等15s广播
        Thread.sleep(15000);
        //回复一条消息
        channelHandlerContext.writeAndFlush(
                new DatagramPacket(
                        Unpooled.copiedBuffer("Hello，我是Server，我的时间戳是"+System.currentTimeMillis(), CharsetUtil.UTF_8), datagramPacket.sender())
        ).sync();*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        final Channel channel = ctx.channel();
        /*timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                //回复一条消息
                long timestamp = System.currentTimeMillis();
                channel.writeAndFlush(
                        new DatagramPacket(
                                Unpooled.copiedBuffer("Hello，我是Server，我的时间戳是"+timestamp, CharsetUtil.UTF_8), new InetSocketAddress(0))
                ).sync();
                System.out.println("【NOTE】>>>>>> 服务端["+channel.localAddress()+"]广播了一条数据"+timestamp);
            }
        }, 15000, TimeUnit.MILLISECONDS);*/
        //while(true){
            scheduleThreadPool.schedule(new Runnable() {
                @Override
                public void run() {
                    //回复一条消息
                    long timestamp = System.currentTimeMillis();
                    try {
                        channel.writeAndFlush(
                                new DatagramPacket(
                                        Unpooled.copiedBuffer("Hello，我是Server，我的时间戳是"+timestamp, CharsetUtil.UTF_8), new InetSocketAddress("255.255.255.255",9999))
                        ).sync();
                        System.out.println("【NOTE】>>>>>> 服务端["+channel.localAddress()+"]广播了一条数据"+timestamp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000, TimeUnit.MILLISECONDS);
            //等15s广播
            Thread.sleep(15000);
            System.out.println("schedule pool done.");
       // }
        ctx.fireChannelActive();
    }
}
