package com.jonex.netty.test.attributeMap;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;

import java.util.Date;

/**
 * Created by Guest on 2017/9/7.
 */
public class AttributeMapClientHandler1 extends ChannelHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Attribute<NettyChannel> attr = ctx.attr(AttributeMapConstant.NETTY_CHANNEL_CTX_KEY);
        NettyChannel nChannel = attr.get();
        if (nChannel == null) {
            NettyChannel newNChannel = new NettyChannel("HelloWorld0Client", new Date());
            nChannel = attr.setIfAbsent(newNChannel);
        } else {
            System.out.println("channelActive attributeMap 中是有值的");
            System.out.println(nChannel.getName() + "=======" + nChannel.getCreateDate());
        }
        System.out.println("HelloWorldClientHandler Active");
        //channel attributeMap
        Attribute<String> channelAttr = ctx.channel().attr(AttributeMapConstant.NETTY_CHANNEL_KEY);
        String channelAttrValue = channelAttr.get();
        if(channelAttrValue == null){
            System.out.println("1channel attributeMap null");
            channelAttr.setIfAbsent("jonex");
        }else{
            System.out.println("1channel attributeMap:"+channelAttrValue);

        }
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Attribute<NettyChannel> attr = ctx.attr(AttributeMapConstant.NETTY_CHANNEL_CTX_KEY);
        NettyChannel nChannel = attr.get();
        if (nChannel == null) {
            NettyChannel newNChannel = new NettyChannel("HelloWorld0Client", new Date());
            nChannel = attr.setIfAbsent(newNChannel);
        } else {
            System.out.println("channelRead attributeMap 中是有值的");
            System.out.println(nChannel.getName() + "=======" + nChannel.getCreateDate());
        }
        System.out.println("HelloWorldClientHandler read Message:" + msg);

        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
