package com.jonex.netty.test.production.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 心跳包
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/11 15:23
 */
public class Heartbeats {

    private final static ByteBuf HEARTBEAT_BUF;

    static {
        ByteBuf buf = Unpooled.buffer(NettyCommonProtocol.HEAD_LENGTH);
        buf.writeShort(NettyCommonProtocol.MAGIC);
        buf.writeByte(NettyCommonProtocol.HEARTBEAT);
        buf.writeByte(0);
        buf.writeLong(0);
        buf.writeInt(0);
        HEARTBEAT_BUF = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(buf));
    }

    public static ByteBuf heartbeatContent(){
        return HEARTBEAT_BUF.duplicate();
    }

}
