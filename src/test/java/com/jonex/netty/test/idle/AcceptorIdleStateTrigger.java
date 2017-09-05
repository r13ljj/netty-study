package com.jonex.netty.test.idle;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/5 16:42
 */
public class AcceptorIdleStateTrigger extends ChannelHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state() == IdleState.READER_IDLE){
                throw new Exception("idle excepion");
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

}
