package com.jonex.netty.test.production.example;

import com.jonex.netty.test.production.server.acceptor.ChannelEventListener;
import com.jonex.netty.test.production.server.acceptor.DefaultCommonServerAcceptor;
import io.netty.channel.Channel;

/**
 * Created by lijunjun on 2017/9/14.
 */
public class ServerAcceptorStartup {
    public static void main(String[] args) throws Exception {
        DefaultCommonServerAcceptor serverAcceptor = new DefaultCommonServerAcceptor(20011, new ChannelEventListener() {
            @Override
            public void onChannelConnect(String remoteAddr, Channel channel) {
                System.out.println("========channel connect event save======");
            }

            @Override
            public void onChannelClose(String remoteAddr, Channel channel) {
                System.out.println("========channel close event save======");
            }

            @Override
            public void onChannelIdle(String remoteAddr, Channel channel) {
                System.out.println("========channel idle event save======");
            }

            @Override
            public void onChannelException(String remoteAddr, Channel channel) {
                System.out.println("========channel exception event save======");
            }
        });
        serverAcceptor.start();
    }
}
