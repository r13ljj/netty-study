package com.jonex.netty.test.production.server.acceptor;

import com.jonex.netty.test.production.common.NettyEvent;
import com.jonex.netty.test.production.common.ServiceThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/13 14:52
 */
public abstract class DefaultNettyServerAcceptor extends NettyServerAcceptor {

    private final static Logger logger = LoggerFactory.getLogger(DefaultNettyServerAcceptor.class);

    private final NettyEventExecutor nettyEventExecutor = new NettyEventExecutor();


    public DefaultNettyServerAcceptor(SocketAddress socketAddress){
        super(socketAddress);
    }

    public void putNettyEvent(NettyEvent event){
        this.nettyEventExecutor.putNettyEvent(event);
    }

    protected abstract ChannelEventListener getChannelEventListener();


    /**
     * netty事件执行
     */
    class NettyEventExecutor extends ServiceThread {

        private final LinkedBlockingQueue<NettyEvent> eventQueue = new LinkedBlockingQueue<NettyEvent>();
        private final int MaxSize = 10000;

        public void putNettyEvent(NettyEvent event){
            if (this.eventQueue.size() < MaxSize){
                this.eventQueue.add(event);
            }else{
                logger.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event.toString());
            }
        }

        @Override
        public String getServiceName() {
            return NettyEventExecutor.class.getSimpleName();
        }

        @Override
        public void run() {
            logger.info(this.getServiceName() + " service started");
            ChannelEventListener listener = getChannelEventListener();
            while(!this.isStoped()){
                try{
                    NettyEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null){
                        switch (event.getType()) {
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(), event.getChannel());
                                break;
                            default:
                                break;
                        }
                    }
                }catch (Exception e){
                    logger.warn(this.getServiceName() + " service has exception. ", e);
                }
            }
        }
    }

}
