package com.jonex.netty.test.production.client.connector;

import io.netty.channel.Channel;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/11 14:30
 */
public interface ClientConnector {

    public Channel connect(String host, int port);

    public void shutdownGracefully();

}
