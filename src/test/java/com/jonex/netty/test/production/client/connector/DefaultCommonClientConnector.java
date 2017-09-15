package com.jonex.netty.test.production.client.connector;

import com.jonex.netty.test.production.ConnnectionWatchDog;
import com.jonex.netty.test.production.common.*;
import com.jonex.netty.test.production.exception.ConnectFailedException;
import com.jonex.netty.test.production.serializer.SerializerHolder;
import com.jonex.netty.test.production.server.acceptor.AcknowledgeEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.ExecutorServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 13:48
 */
public class DefaultCommonClientConnector extends NettyClientConnector {

    private  final static Logger logger = LoggerFactory.getLogger(DefaultCommonClientConnector.class);

    private final static HashedWheelTimer timer = new HashedWheelTimer(new ThreadFactory() {
        private AtomicInteger threadIndex = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread("NettyClientConnectorExecutor_"+threadIndex.getAndIncrement());
        }
    });

    private volatile Channel channel;


    private final ConcurrentMap<Long, MessageNonAck> messagesNonAcks = new ConcurrentHashMap<Long, MessageNonAck>();

    //心跳触发器
    private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();
    //ack
    private final AcknowledgeEncoder ackEncoder = new AcknowledgeEncoder();



    public DefaultCommonClientConnector() {
        init();
    }

    protected void init(){
        super.init();
        super.bootstrap().channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, allocator)
                .option(ChannelOption.SO_KEEPALIVE, true)   //测试链接的状态，用于可能长时间没有数据交流的
                .option(ChannelOption.TCP_NODELAY, true)    //禁用Nagle算法
                .option(ChannelOption.ALLOW_HALF_CLOSURE, false)    //防止Netty在SocketChannel.read(..)返回-1时自动关闭连接
                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)  //消息大小评估?
                .option(ChannelOption.SO_REUSEADDR, true)   //允许重复使用本地地址和端口
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) TimeUnit.SECONDS.toMillis(3));   //连接超时设置
    }


    @Override
    protected EventLoopGroup initEventLoopGroup(int nWorkers, ExecutorServiceFactory workerFactory) {
        return NativeSupport.isLinuxPlatform() ? new EpollEventLoopGroup(nWorkers, workerFactory) : new NioEventLoopGroup(nWorkers, workerFactory);
    }

    @Override
    public Channel connect(String host, int port) {
        final Bootstrap boot = super.bootstrap();
        //重连watchdog
        final ConnnectionWatchDog watchDog = new ConnnectionWatchDog(boot, timer, host, port) {
            @Override
            public ChannelHandler[] handlers() {
                return new ChannelHandler[]{
                        //将自己[ConnectionWatchdog]装载到handler链中，当链路断掉之后，会触发ConnectionWatchdog #channelInActive方法
                        this,
                        //每隔30s的时间触发一次userEventTriggered的方法，并且指定IdleState的状态位是WRITER_IDLE
                        new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS),
                        //实现userEventTriggered方法，并在state是WRITER_IDLE的时候发送一个心跳包到sever端，告诉server端我还活着
                        idleStateTrigger,
                        new MessageDecoder(),
                        new MessageEncoder(),
                        ackEncoder,
                        new MessageHandler()
                };
            }


        };
        //设置重试
        watchDog.canRetry(true);
        try {
            ChannelFuture future;
            synchronized (bootstrapLock()){
                boot.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(watchDog.handlers());
                    }
                });
                future = boot.connect("127.0.0.1", 20011);
            }
            future.sync();
            channel = future.channel();
        }catch (Throwable t) {
            throw new ConnectFailedException("connects to [" + host + ":"+port+"] fails", t);
        }
        return channel;
    }

    public void addNeedAckMessageInfo(MessageNonAck msgNonAck) {
        messagesNonAcks.put(msgNonAck.getId(), msgNonAck);
    }



    /**
     * 消息处理
     */
    @ChannelHandler.Sharable
    class MessageHandler extends ChannelHandlerAdapter{

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("client connector handler remote:{}", ctx.channel().remoteAddress());
            if (msg instanceof Acknowledge) {
                logger.info("收到server端的Ack信息，无需再次发送信息");

            }else{
                super.channelRead(ctx, msg);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.info("default acceptor client connector exception", cause);
            ctx.close();
        }
    }






    /**
     * ack超时扫描任务
     */
    private class AckTimeoutScanner implements Runnable {

        {
            Thread t = new Thread(new AckTimeoutScanner(), "ack.timeout.scanner");
            t.setDaemon(true);
            t.start();
        }

        public void addNeedAckMessageInfo(MessageNonAck msgNonAck) {
            messagesNonAcks.put(msgNonAck.getId(), msgNonAck);
        }

        @Override
        public void run() {
            for(;;){
                try{
                    for (MessageNonAck m : messagesNonAcks.values()) {
                        if (System.currentTimeMillis()-m.getTimestamp() > TimeUnit.SECONDS.toMillis(10)) {
                            //移除
                            if (messagesNonAcks.remove(m.getId()) == null){
                                continue;
                            }
                            if (m.getChannel().isActive()) {
                                logger.warn("准备重新发送信息");
                                MessageNonAck msgNonAck = new MessageNonAck(m.getMsg(), m.getChannel());
                                messagesNonAcks.put(msgNonAck.getId(), msgNonAck);
                                m.getChannel().writeAndFlush(m.getMsg())
                                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                            }
                        }
                    }
                    Thread.sleep(300);
                }catch (Throwable t){
                    logger.error("An exception has been caught while scanning the timeout acknowledges {}.", t);
                }
            }
        }
    }
}
