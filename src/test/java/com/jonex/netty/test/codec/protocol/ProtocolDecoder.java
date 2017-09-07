package com.jonex.netty.test.codec.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/7 16:02
 */
public class ProtocolDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in == null) {
            throw new Exception("decode byteBuf is null");
        }
        if (in.readableBytes() <= 0) {
            throw new Exception("decode byteBuf readable bytes is zero");
        }
        byte magic = in.readByte();
        byte msgType = in.readByte();
        short reserve = in.readShort();
        short sn = in.readShort();
        int len = in.readInt();
        byte[] bodyBytes = new byte[len];
        in.readBytes(bodyBytes);
        String body = new String(bodyBytes, "UTF-8");
        ProtocolMsg msg = new ProtocolMsg(new ProtocolHeader(magic, msgType, reserve, sn, len), body);
        out.add(msg);
    }

}
