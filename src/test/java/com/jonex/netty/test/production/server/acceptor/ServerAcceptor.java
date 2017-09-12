package com.jonex.netty.test.production.server.acceptor;

import java.net.SocketAddress;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 18:23
 */
public interface ServerAcceptor {

    SocketAddress localAddress();

    void start()throws InterruptedException;

    void shutdownGracefully();

    void start(boolean sync)throws InterruptedException;

}
