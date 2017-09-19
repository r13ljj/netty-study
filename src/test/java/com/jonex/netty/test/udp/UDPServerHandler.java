package com.jonex.netty.test.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        //读取接收的数据
        ByteBuf buf = datagramPacket.duplicate().content();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.writeBytes(bytes);
        String body = new String(bytes, CharsetUtil.UTF_8);
        System.out.println("【NOTE】>>>>>> 收到客户端的数据："+body);
        //回复一条消息
        channelHandlerContext.writeAndFlush(
                new DatagramPacket(
                Unpooled.copiedBuffer("Hello，我是Server，我的时间戳是"+System.currentTimeMillis(), CharsetUtil.UTF_8), datagramPacket.sender())
            ).sync();
    }
}
