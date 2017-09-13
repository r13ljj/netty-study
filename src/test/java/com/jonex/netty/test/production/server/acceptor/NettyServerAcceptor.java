package com.jonex.netty.test.production.server.acceptor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.DefaultExecutorServiceFactory;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ExecutorServiceFactory;
import io.netty.util.internal.PlatformDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/13 9:42
 */
public abstract class NettyServerAcceptor implements ServerAcceptor {

    private final static Logger logger = LoggerFactory.getLogger(NettyServerAcceptor.class);

    private final static int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    protected final SocketAddress localAddress;

    private ServerBootstrap bootstrap;
    private EventLoopGroup boss;
    private EventLoopGroup workers;
    private int nWorkers;

    private volatile ByteBufAllocator allocator;

    protected  final HashedWheelTimer timer = new HashedWheelTimer(new ThreadFactory() {
        private AtomicInteger threadIndex;
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r,"NettySrvAcceptorExecutor_"+threadIndex.getAndIncrement());
        }
    });



    public NettyServerAcceptor(SocketAddress socketAddress){
        this(socketAddress, AVAILABLE_PROCESSORS << 1);
    }

    public NettyServerAcceptor(SocketAddress socketAddress, int nWorkers){
        this.localAddress = socketAddress;
        this.nWorkers = nWorkers;
    }


    protected void init(){
        boss = initEventLoopGroup(1, new DefaultExecutorServiceFactory("server.acceptor.boss"));
        workers = initEventLoopGroup(nWorkers, new DefaultExecutorServiceFactory("server.acceptor.worker"));
        bootstrap = new ServerBootstrap();
        bootstrap.group(boss, workers);
        //使用池化的directBuffer
        /**
         * 一般高性能的场景下,使用的堆外内存，也就是直接内存，使用堆外内存的好处就是减少内存的拷贝，和上下文的切换，
         * 缺点是堆外内存处理的不好容易发生堆外内存OOM
         * 当然也要看当前的JVM是否只是使用堆外内存，换而言之就是是否能够获取到Unsafe对象#PlatformDependent.directBufferPreferred()
         */
        allocator = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
        bootstrap.childOption(ChannelOption.ALLOCATOR, allocator);
    }

    protected abstract EventLoopGroup initEventLoopGroup(int nthread, ExecutorServiceFactory bossFactory);

    @Override
    public void start() throws InterruptedException {
        this.start(true);
    }

    @Override
    public void start(boolean sync) throws InterruptedException {
        ChannelFuture future = bind(localAddress);
        if (sync) {
            future.channel().closeFuture().sync();
        }
    }

    protected abstract ChannelFuture bind(SocketAddress localAddress);

    @Override
    public void shutdownGracefully() {
        boss.shutdownGracefully().awaitUninterruptibly();
        workers.shutdownGracefully().awaitUninterruptibly();
    }

    public SocketAddress localAddress() {
        return localAddress;
    }

    protected ServerBootstrap bootstrap() {
        return bootstrap;
    }
}
