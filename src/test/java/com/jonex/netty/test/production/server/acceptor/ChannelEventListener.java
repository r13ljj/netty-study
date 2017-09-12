package com.jonex.netty.test.production.server.acceptor;

import io.netty.channel.Channel;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 18:21
 */
public interface ChannelEventListener {

    void onChannelConnect(final String remoteAddr, final Channel channel);

    void onChannelClose(final String remoteAddr, final  Channel channel);

    void onChannelIdle(final String remoteAddr, final  Channel channel);

    void onChannelException(final String remoteAddr, final  Channel channel);

}
