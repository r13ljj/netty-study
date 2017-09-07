package com.jonex.netty.test.heartbeat2;

import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Guest on 2017/9/8.
 */
public class NettyChannelMap {

    private static Map<String, SocketChannel> channelMap = new ConcurrentHashMap<String, SocketChannel>();

    public static void add(String clientId, SocketChannel channel){
        channelMap.put(clientId, channel);
    }

    public static SocketChannel get(String clientId){
        return channelMap.get(clientId);
    }

    public static void remove(SocketChannel socketChannel){
        for(Map.Entry<String,SocketChannel> entry : channelMap.entrySet()){
            if(entry.getValue() == socketChannel){
                channelMap.remove(entry.getKey());
            }
        }
    }

}
