package com.jonex.netty.test.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/20 16:15
 */
public class UDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String body = msg.content().toString(CharsetUtil.UTF_8);
        System.out.println("【NOTE】>>>>>> 客户端["+ctx.channel().localAddress()+"]收到服务端的数据："+body);
        //回复一条消息
        /*Thread.sleep(1000);
        ctx.writeAndFlush(
                new DatagramPacket(
                        Unpooled.copiedBuffer("Hello，我是Client，我的时间戳是"+System.currentTimeMillis(), CharsetUtil.UTF_8), msg.sender())
        ).sync();*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
