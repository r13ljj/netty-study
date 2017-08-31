package com.jonex.netty.test.unpack.delimiter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class BaseServerHandler extends SimpleChannelInboundHandler {

    private int counter;

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        String msg = (String)o;
        System.out.println("server receive msg : " + msg + ";the counter is: " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
