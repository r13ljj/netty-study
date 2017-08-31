package com.jonex.netty.test.unpack.custom;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/8/31 15:13
 */
public class CustomClientHandler extends SimpleChannelInboundHandler {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        String serverName = "Server["+channelHandlerContext.channel().remoteAddress()+"]";
        System.out.println(serverName+"send->client:"+o);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String body = "Hello,Netty";
        CustomMsg customMsg = new CustomMsg((byte)0xAB, (byte)0xCD, body.length(), body);
        ctx.writeAndFlush(customMsg);
        System.out.println("client send message.body:"+body);
    }

    public static void main(String[] args) {
        System.out.println((byte)0xAB);
        System.out.println((byte)0xCD);
    }
}
