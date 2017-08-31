package com.jonex.netty.test.unpack.delimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Guest on 2017/8/30.
 */
public class BaseClientHandler extends SimpleChannelInboundHandler {

    private byte[] req;
    private int counter;
    
    public BaseClientHandler() {
//        req = ("jonex is learner").getBytes();
        req = ("In this chapter you general, we recommend Java Concurrency in Practice by Brian Goetz. $$__ His book w"
                + "ill give We’ve reached an exciting point—in the next chapter we’ll $$__ discuss bootstrapping, the process "
                + "of configuring and connecting all of Netty’s components to bring $$__ your learned about threading models in ge"
                + "neral and Netty’s threading model in particular, whose performance $$__ and consistency advantages we discuss"
                + "ed in detail In this chapter you general, we recommend Java  $$__Concurrency in Practice by Brian Goetz. Hi"
                + "s book will give We’ve reached an exciting point—in the next $$__ chapter we’ll discuss bootstrapping, the"
                + " process of configuring and connecting all of Netty’s components $$__ to bring your learned about threading "
                + "models in general and Netty’s threading model in particular, $$__ whose performance and consistency advantag"
                + "es we discussed in detailIn this chapter you general, $$__ we recommend Java Concurrency in Practice by Bri"
                + "an Goetz. His book will give We’ve reached an exciting $$__ point—in the next chapter;the counter is: 1 2222"
                + "sdsa ddasd asdsadas dsadasdas" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message = null;
        /*for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }*/
        message = Unpooled.buffer(req.length);
        message.writeBytes(req);
        ctx.writeAndFlush(message);
        message = Unpooled.buffer(req.length);
        message.writeBytes(req);
        ctx.writeAndFlush(message);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        String buf = (String)msg;
        System.out.println("Now is : "+buf+"; the counter is : "+ ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
