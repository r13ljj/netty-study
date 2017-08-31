package com.jonex.netty.test.unpack.custom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/8/31 15:04
 */
public class CustomEncoder extends MessageToByteEncoder<CustomMsg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CustomMsg customMsg, ByteBuf byteBuf) throws Exception {
        if (customMsg == null)
            throw new Exception("msg is null");
        String body = customMsg.getBody();
        byte[] bodyBytes = body.getBytes("UTF-8");
        byteBuf.writeByte(customMsg.getType());
        byteBuf.writeByte(customMsg.getFlag());
        byteBuf.writeInt(bodyBytes.length);
        byteBuf.writeBytes(bodyBytes);
        System.out.println("encode message.body:"+new String(bodyBytes, "UTF-8"));
    }
}
