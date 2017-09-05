package com.jonex.netty.test.idle;

import io.netty.channel.ChannelHandler;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/5 16:40
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] handlers();

}
