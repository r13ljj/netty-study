package com.jonex.netty.test.helloworld;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class HelloWorldClientHandler extends SimpleChannelInboundHandler {

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("HelloWorldClientHandler read message:"+o.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HelloWorldClientHandler active");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
