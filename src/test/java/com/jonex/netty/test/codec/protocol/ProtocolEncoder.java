package com.jonex.netty.test.codec.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/7 15:45
 */
public class ProtocolEncoder extends MessageToByteEncoder<ProtocolMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolMsg msg, ByteBuf out) throws Exception {
        if (msg == null || msg.getProtocolHeader() == null) {
            throw new Exception("msg is null");
        }
        ProtocolHeader header = msg.getProtocolHeader();
        String body = msg.getBody();
        byte[] bodyBytes = body.getBytes(Charset.forName("UTF-8"));
        out.writeByte(header.getMagic());
        out.writeByte(header.getMsgType());
        out.writeShort(header.getReserve());
        out.writeShort(header.getSn());
        out.writeInt(bodyBytes.length);
        out.writeBytes(bodyBytes);
    }
}
