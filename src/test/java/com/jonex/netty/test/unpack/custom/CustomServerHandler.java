package com.jonex.netty.test.unpack.custom;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/8/31 14:27
 */
public class CustomServerHandler extends SimpleChannelInboundHandler{

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        String clientName = "Client["+channelHandlerContext.channel().remoteAddress()+"]";
        if (o instanceof CustomMsg){
            CustomMsg msg = (CustomMsg)o;
            System.out.println(clientName+"send->Server:"+msg.getBody());
        } else {
            System.out.println(clientName+"send unknow message:"+o);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
