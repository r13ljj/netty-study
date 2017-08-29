package com.jonex.netty.test.helloworld;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HelloWorldServerHandler extends SimpleChannelInboundHandler{
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("server messageReceived...");
        String serverName = channelHandlerContext.channel().remoteAddress()+"->`";
        System.out.println(serverName+o.toString());
        channelHandlerContext.write(serverName+"server write :"+o);
        channelHandlerContext.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}

