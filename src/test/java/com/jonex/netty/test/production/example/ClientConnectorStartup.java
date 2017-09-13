package com.jonex.netty.test.production.example;

import com.jonex.netty.test.production.client.connector.DefaultCommonClientConnector;
import com.jonex.netty.test.production.common.Message;
import com.jonex.netty.test.production.common.MessageNonAck;
import com.jonex.netty.test.production.common.NettyCommonProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lijunjun on 2017/9/14.
 */
public class ClientConnectorStartup {

    private final static Logger logger = LoggerFactory.getLogger(ClientConnectorStartup.class);

    public static void main(String[] args) {
        DefaultCommonClientConnector clientConnector = new DefaultCommonClientConnector();
        final Channel channel = clientConnector.connect("127.0.0.1", 20011);
        User user = new User(13, "jonex.lee");
        Message message = new Message();
        message.setSign(NettyCommonProtocol.REQUEST);
        message.setData(user);
        channel.writeAndFlush(message).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    logger.info("send fail,reason is {}",channelFuture.cause().getMessage());
                }
            }
        });
        MessageNonAck messageNonAck = new MessageNonAck(message, channel);
        clientConnector.addNeedAckMessageInfo(messageNonAck);
    }


    public static class User {

        private Integer id;

        private String username;


        public User(Integer id, String username) {
            this.id = id;
            this.username = username;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "User [id=" + id + ", username=" + username + "]";
        }

    }
}
