package com.jonex.netty.test.unpack.custom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class CustomDecoder extends LengthFieldBasedFrameDecoder {

    //判断传送客户端传送过来的数据是否按照协议传输，头部信息的大小应该是 byte+byte+int = 1+1+4 = 6
    private final static int HEADER_SIZE=6;

    /**
     * @param maxFrameLength 解码时，处理每个帧数据的最大长度
     * @param lengthFieldOffset 该帧数据中，存放该帧数据的长度的数据的起始位置
     * @param lengthFieldLength 记录该帧数据长度的字段本身的长度
     * @param lengthAdjustment  修改帧数据长度字段中定义的值，可以为负数
     * @param initialBytesToStrip 解析的时候需要跳过的字节数
     * @param failFast 为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常
     */
    public CustomDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null){
            System.out.println("decode bytebuf null");
            return null;
        }
        if (in.readableBytes() < HEADER_SIZE){
            throw new Exception("可读信息段比头部信息都小，你在逗我？");
        }
        byte type = in.readByte();
        byte flag = in.readByte();
        int length = in.readInt();
        if(in.readableBytes() < length){
            throw new Exception("body字段你告诉我长度是"+length+",但是真实情况是没有这么多，你又逗我？");
        }
        ByteBuf buf = in.readBytes(length);
        byte[] bytes = new byte[in.readableBytes()];
        buf.readBytes(bytes);
        String body = new String(bytes, "UTF-8");
        CustomMsg msg = new CustomMsg(type, flag, length, body);
        return msg;
    }
}