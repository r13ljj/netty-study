package com.jonex.netty.test.production.server.acceptor;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 18:17
 */
public class AcceptorIdleStateTrigger extends ChannelHandlerAdapter{

    private final static Logger logger = LoggerFactory.getLogger(AcceptorIdleStateTrigger.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.warn("client[{}] disconnect", ctx.channel().remoteAddress());
                throw new Exception("NO SIGNAL");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
