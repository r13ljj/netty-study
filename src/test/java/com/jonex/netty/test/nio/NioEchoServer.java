package com.jonex.netty.test.nio;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/8 16:17
 */
public class NioEchoServer {

    private Charset charset=Charset.forName("GBK");

    private int[] ports;

    public NioEchoServer(int[] ports){
        this.ports = ports;
        try{
            go();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void go()throws Exception{
        Selector selector = Selector.open();
        for(int i=0; i<ports.length; i++){
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            ServerSocket serverSocket = channel.socket();
            serverSocket.bind(new InetSocketAddress(ports[i]));
            channel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("go to listen at "+ports[i]);
        }
        while(true){
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();
            while(it.hasNext()){
                SelectionKey key = it.next();
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT){
                    ServerSocketChannel serverChannel = (ServerSocketChannel)key.channel();
                    SocketChannel clientChannel = serverChannel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);
                    it.remove();
                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    SocketChannel clientChannel = (SocketChannel)key.channel();
                    if (!clientChannel.isOpen()) {
                        selector = Selector.open();
                    } else {
                        ByteBuffer echoBuffer = ByteBuffer.allocate(2014);
                        while(clientChannel.read(echoBuffer) > 0){
                            System.out.println( "Echoed "+charset.decode(echoBuffer).toString()+" from "+clientChannel.socket().getInetAddress().getHostAddress() );
                            echoBuffer.flip();
                            clientChannel.write(echoBuffer);
                            echoBuffer.clear();
                        }
                        it.remove();
                        key.cancel();
                        clientChannel.close();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] ports = {9991, 9992};
        NioEchoServer echoServer = new NioEchoServer(ports);
    }

}
