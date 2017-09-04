package com.jonex.netty.test.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/4 16:10
 */
public class ProtobufServerHandler extends SimpleChannelInboundHandler {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        RichManProto.RichMan req = (RichManProto.RichMan) msg;
        System.out.println(req.getName()+"他有"+req.getCarsCount()+"量车");
        List<RichManProto.RichMan.Car> lists = req.getCarsList();
        if(null != lists) {
            for(RichManProto.RichMan.Car car : lists){
                System.out.println(car.getName());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
