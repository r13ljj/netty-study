package com.jonex.netty.test.production;

import io.netty.channel.ChannelHandler;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/8 18:58
 */
public interface ChannelHandlerHolder {

    public ChannelHandler[] handlers();

}
