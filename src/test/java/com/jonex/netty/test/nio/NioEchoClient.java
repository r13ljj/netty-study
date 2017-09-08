package com.jonex.netty.test.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/8 17:09
 */
public class NioEchoClient {

    private final static Charset charset = Charset.forName("GBK");


    public static void main(String[] args) throws Exception{
        //1.连接服务端
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);
        clientChannel.connect(new InetSocketAddress(9991));
        Selector selector = Selector.open();
        clientChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);

        boolean isFinished = false;

        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> it = selectionKeys.iterator();
        while(it.hasNext()) {
            SelectionKey key = it.next();
            while (!isFinished) {
                if (key.isConnectable()) {  //2.连接事件
                    SocketChannel channel = (SocketChannel) key.channel();
                    channel.configureBlocking(false);
                    channel.finishConnect();
                    //注册读事件
                    channel.register(selector, SelectionKey.OP_READ);
                    //写入数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    buffer.put(charset.encode("好了克隆技术杜洛克防水堵漏开发!"));
                    buffer.flip();
                    channel.write(buffer);
                    buffer.clear();
                } else if (key.isValid() && key.isReadable()) { //3.读取事件
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.read(buffer);
                    buffer.flip();
                    System.out.println("echo server return:" + charset.decode(buffer).toString());
                    buffer.clear();

                    isFinished = true;
                    key.cancel();
                    clientChannel.close();
                    selector.close();
                }
            }

        }
    }
}
