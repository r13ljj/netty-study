package com.jonex.netty.test.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

/**
 * Created by Guest on 2017/9/5.
 */
public class HeartbeatClientHandler extends SimpleChannelInboundHandler {

    private final static ByteBuf HEARTBEAT_SIGNAL = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8));

    private final static  int RETRY_COUNT = Integer.MAX_VALUE;

    private int current_retry;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("激活时间是："+new Date());
        System.out.println("HeartbeatClientHandler channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("停止时间是："+new Date());
        System.out.println("HeartbeatClientHandler channelInactive");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("循环触发时间是："+new Date());
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.WRITER_IDLE){
                if(current_retry < RETRY_COUNT){
                    System.out.println("current_retry:"+current_retry);
                    current_retry++;
                    //写入心跳消息
                    ctx.channel().writeAndFlush(HEARTBEAT_SIGNAL);
                }
            }
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        System.out.println("client receive message:"+message);
        if ("Heartbeat".equals(message)){
            ctx.write("has read message from server");
            ctx.flush();
        } else {
            ReferenceCountUtil.release(msg);
        }
    }
}
