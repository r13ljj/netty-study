package com.jonex.netty.test.production.client.connector;

import com.jonex.netty.test.production.common.Heartbeats;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 11:30
 */
public class ConnectorIdleStateTrigger extends ChannelHandlerAdapter{

    private static final Logger logger = LoggerFactory.getLogger(ConnectorIdleStateTrigger.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.WRITER_IDLE){
                logger.info("need send heartbeats");
                ctx.channel().writeAndFlush(Heartbeats.heartbeatContent());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
