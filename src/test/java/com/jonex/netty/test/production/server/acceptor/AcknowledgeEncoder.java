package com.jonex.netty.test.production.server.acceptor;

import com.jonex.netty.test.production.common.Acknowledge;
import com.jonex.netty.test.production.common.Message;
import com.jonex.netty.test.production.common.NettyCommonProtocol;
import com.jonex.netty.test.production.serializer.SerializerHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 17:47
 */
public class AcknowledgeEncoder extends MessageToByteEncoder<Acknowledge> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Acknowledge ack, ByteBuf out) throws Exception {
        byte[] bytes = SerializerHolder.getSerializer().writeObject(ack);
        out.writeShort(NettyCommonProtocol.MAGIC)
                .writeByte(NettyCommonProtocol.ACK)
                .writeByte(0)
                .writeLong(ack.getSequence())
                .writeInt(bytes.length)
                .writeBytes(bytes);
    }
}
