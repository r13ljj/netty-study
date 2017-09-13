package com.jonex.netty.test.production.common;

import com.jonex.netty.test.production.serializer.SerializerHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 消息解码
 */
public class MessageDecoder extends ReplayingDecoder<MessageDecoder.State> {

    enum State {
        HEADER_MAGIC,
        HEADER_SIGN,
        HEADER_STATUS,
        HEADER_ID,
        HEADER_BODY_LENGTH,
        BODY
    }

    private final NettyCommonProtocol header = new NettyCommonProtocol();


    public MessageDecoder(){
        super(State.HEADER_MAGIC);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (super.state()){
            case HEADER_MAGIC:
                checkMagic(in.readShort());
                checkpoint(State.HEADER_SIGN);
            case HEADER_SIGN:
                header.setSign(in.readByte());
                checkpoint(State.HEADER_STATUS);
            case HEADER_STATUS:
                header.setStatus(in.readByte());
                checkpoint(State.HEADER_ID);
            case HEADER_ID:
                header.setId(in.readLong());
                checkpoint(State.HEADER_BODY_LENGTH);
            case HEADER_BODY_LENGTH:
                header.setBodyLength(in.readInt());
                checkpoint(State.BODY);
            case BODY:
                switch (header.getSign()) {
                    case NettyCommonProtocol.RESPONSE:
                    case NettyCommonProtocol.SERVICE_1:
                    case NettyCommonProtocol.SERVICE_2:
                    case NettyCommonProtocol.SERVICE_3: {
                        byte[] bytes = new byte[header.getBodyLength()];
                        in.readBytes(bytes);

                        Message msg = SerializerHolder.getSerializer().readObject(bytes, Message.class);
                        msg.setSign((short)header.getSign());
                        out.add(msg);

                        break;
                    }
                    case NettyCommonProtocol.ACK: {
                        byte[] bytes = new byte[header.getBodyLength()];
                        in.readBytes(bytes);

                        Acknowledge ack = SerializerHolder.getSerializer().readObject(bytes, Acknowledge.class);
                        out.add(ack);
                        break;
                    }
                    default:
                        throw new IllegalArgumentException();

                }
                checkpoint(State.HEADER_MAGIC);

        }
    }

    private static void checkMagic(short magic){
        if (magic != NettyCommonProtocol.MAGIC) {
            throw new IllegalArgumentException("unknow protocol header magic:"+magic);
        }
    }
}
