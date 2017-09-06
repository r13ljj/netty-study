package com.jonex.netty.test.idle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/5 18:04
 */
@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelHandlerAdapter {

    private static final ByteBuf HEARTBEAT_SIGNAL = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state() == IdleState.WRITER_IDLE){
                ctx.channel().writeAndFlush(HEARTBEAT_SIGNAL.duplicate());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
