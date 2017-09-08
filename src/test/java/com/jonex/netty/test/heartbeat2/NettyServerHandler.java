package com.jonex.netty.test.heartbeat2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/8 14:04
 */
public class NettyServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof BaseMsg) {
            BaseMsg baseMsg = (BaseMsg)msg;
            //登录消息
            if (MsgType.LOGIN.equals(baseMsg.getType())) {
                LoginMsg loginMsg = (LoginMsg) baseMsg;
                if ("robin".equals(loginMsg.getUserName()) && "yao".equals(loginMsg.getPassword())) {
                    NettyChannelMap.add(loginMsg.getClientId(), (SocketChannel) ctx.channel());
                    System.out.println("client" + loginMsg.getClientId() + " 登录成功");
                }
            } else {
                if (NettyChannelMap.get(baseMsg.getClientId()) == null) {
                    // 说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
                    LoginMsg loginMsg = new LoginMsg();
                    ctx.channel().writeAndFlush(loginMsg);
                }
            }
            //登录后
            switch (baseMsg.getType()) {
                case LOGIN:
                    PingMsg pingMsg=(PingMsg)msg;
                    PingMsg replyPing=new PingMsg();
                    NettyChannelMap.get(pingMsg.getClientId()).writeAndFlush(replyPing);
                    break;
                case ASK:
                    //收到客户端的请求
                    AskMsg askMsg=(AskMsg)msg;
                    if("authToken".equals(askMsg.getParams().getAuth())){
                        ReplyServerBody replyBody=new ReplyServerBody("server info");
                        ReplyMsg replyMsg=new ReplyMsg(replyBody);
                        NettyChannelMap.get(askMsg.getClientId()).writeAndFlush(replyMsg);
                    }
                    break;
                case REPLY:
                    //收到客户端回复
                    ReplyMsg replyMsg=(ReplyMsg)msg;
                    ReplyClientBody clientBody=(ReplyClientBody)replyMsg.getReplyBody();
                    System.out.println("receive client msg: "+clientBody.getClientInfo());
                    break;
                default:
                    break;
            }
        }
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("server SimpleChatClient:"+incoming.remoteAddress()+"异常");
        cause.printStackTrace();
        ctx.close();
    }
}
