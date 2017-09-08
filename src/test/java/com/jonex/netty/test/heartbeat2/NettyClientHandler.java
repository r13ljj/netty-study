package com.jonex.netty.test.heartbeat2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/8 10:26
 */
public class NettyClientHandler extends ChannelHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            switch (event.state()){
                case WRITER_IDLE:
                    PingMsg ping = new PingMsg();
                    ctx.writeAndFlush(ping);
                    System.out.println("send pint to server-------------");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof BaseMsg) {
            BaseMsg baseMsg = (BaseMsg)msg;
            MsgType msgType=baseMsg.getType();
            switch (msgType){
                case LOGIN:{
                    //向服务器发起登录
                    LoginMsg loginMsg=new LoginMsg();
                    loginMsg.setPassword("yao");
                    loginMsg.setUserName("robin");
                    ctx.writeAndFlush(loginMsg);
                }break;
                case PING:{
                    System.out.println("receive ping from server----------");
                }break;
                case ASK:{
                    ReplyClientBody replyClientBody=new ReplyClientBody("client info **** !!!");
                    ReplyMsg replyMsg=new ReplyMsg(replyClientBody);
                    ctx.writeAndFlush(replyMsg);
                }break;
                case REPLY:{
                    ReplyMsg replyMsg=(ReplyMsg)baseMsg;
                    ReplyServerBody replyServerBody=(ReplyServerBody)replyMsg.getReplyBody();
                    System.out.println("receive client msg: "+replyServerBody.getServerInfo());
                }
                default:break;
            }
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("simpleChatClient:"+incoming.remoteAddress()+"异常");
        cause.printStackTrace();
        ctx.close();
    }
}
