package com.jonex.netty.test.production.client.connector;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultExecutorServiceFactory;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ExecutorServiceFactory;

import java.util.concurrent.ThreadFactory;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 15:45
 */
public abstract class NettyClientConnector implements ClientConnector {

    public final static int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private Bootstrap bootstrap;
    private EventLoopGroup worker;
    private int nWorkers;

    protected volatile ByteBufAllocator allocator;


    public NettyClientConnector(){
        this(AVAILABLE_PROCESSORS << 1);
    }

    public NettyClientConnector(int nWorkers) {
        this.nWorkers = nWorkers;
    }


    protected  void init(){
        ExecutorServiceFactory workerFactory = new DefaultExecutorServiceFactory("client.connector.");
        worker = initEventLoopGroup(nWorkers, workerFactory);
        bootstrap = new Bootstrap().group(worker);
    }

    protected Bootstrap bootstrap() {
        return bootstrap;
    }

    protected Object bootstrapLock() {
        return bootstrap;
    }

    @Override
    public void shutdownGracefully() {
        worker.shutdownGracefully();
    }

    protected abstract EventLoopGroup initEventLoopGroup(int nWorkers, ExecutorServiceFactory workerFactory);

}
