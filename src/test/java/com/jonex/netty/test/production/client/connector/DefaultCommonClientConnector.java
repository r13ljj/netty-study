package com.jonex.netty.test.production.client.connector;

import com.jonex.netty.test.production.ConnnectionWatchDog;
import com.jonex.netty.test.production.common.Acknowledge;
import com.jonex.netty.test.production.common.Message;
import com.jonex.netty.test.production.common.NativeSupport;
import com.jonex.netty.test.production.common.NettyCommonProtocol;
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
        return NativeSupport.isSupportNativeEt() ? new EpollEventLoopGroup(nWorkers, workerFactory) : new NioEventLoopGroup(nWorkers, workerFactory);
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



    /**
     * 消息处理
     */
    @ChannelHandler.Sharable
    class MessageHandler extends ChannelHandlerAdapter{

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof Acknowledge) {
                logger.info("收到server端的Ack信息，无需再次发送信息");

            }
        }
    }

    /**
     * **************************************************************************************************
     *                                          Protocol
     *  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
     *       2   │   1   │    1   │     8     │      4      │
     *  ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤
     *           │       │        │           │             │
     *  │  MAGIC   Sign    Status   Invoke Id   Body Length                   Body Content              │
     *           │       │        │           │             │
     *  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
     *
     * 消息头16个字节定长
     * = 2 // MAGIC = (short) 0xbabe
     * + 1 // 消息标志位, 用来表示消息类型
     * + 1 // 空
     * + 8 // 消息 id long 类型
     * + 4 // 消息体body长度, int类型
     */
    class MessageEncoder extends MessageToByteEncoder<Message>{

        @Override
        protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
            byte[] bytes = SerializerHolder.getSerializer().writeObject(msg);
            out.writeShort(NettyCommonProtocol.MAGIC)
                    .writeByte(msg.getSign())
                    .writeByte(0)
                    .writeLong(0)
                    .writeInt(bytes.length)
                    .writeBytes(bytes);
        }
    }

    /**
     * 消息解码
     */
    static class MessageDecoder extends ReplayingDecoder<MessageDecoder.State>{

        enum State {
            HEADER_MAGIC,
            HEADER_SIGN,
            HEADER_STATUS,
            HEADER_ID,
            HEADER_BODY_LENGTH,
            BODY
        }

        private final NettyCommonProtocol header = new NettyCommonProtocol();


        public MessageDecoder(){
            super(State.HEADER_MAGIC);
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            switch (super.state()){
                case HEADER_MAGIC:
                    checkMagic(in.readShort());
                    checkpoint(State.HEADER_SIGN);
                case HEADER_SIGN:
                    header.setSign(in.readByte());
                    checkpoint(State.HEADER_STATUS);
                case HEADER_STATUS:
                    header.setStatus(in.readByte());
                    checkpoint(State.HEADER_ID);
                case HEADER_ID:
                    header.setId(in.readLong());
                    checkpoint(State.HEADER_BODY_LENGTH);
                case HEADER_BODY_LENGTH:
                    header.setBodyLength(in.readInt());
                    checkpoint(State.BODY);
                case BODY:
                    switch (header.getSign()) {
                        case NettyCommonProtocol.RESPONSE:
                        case NettyCommonProtocol.SERVICE_1:
                        case NettyCommonProtocol.SERVICE_2:
                        case NettyCommonProtocol.SERVICE_3: {
                            byte[] bytes = new byte[header.getBodyLength()];
                            in.readBytes(bytes);

                            Message msg = SerializerHolder.getSerializer().readObject(bytes, Message.class);
                            msg.setSign((short)header.getSign());
                            out.add(msg);

                            break;
                        }
                        case NettyCommonProtocol.ACK: {
                            byte[] bytes = new byte[header.getBodyLength()];
                            in.readBytes(bytes);

                            Acknowledge ack = SerializerHolder.getSerializer().readObject(bytes, Acknowledge.class);
                            out.add(ack);
                            break;
                        }
                        default:
                            throw new IllegalArgumentException();

                    }
                    checkpoint(State.HEADER_MAGIC);

            }
        }

        private static void checkMagic(short magic){
            if (magic != NettyCommonProtocol.MAGIC) {
                throw new IllegalArgumentException("unknow protocol header magic:"+magic);
            }
        }
    }

    /**
     * 不用ack消息
     */
    public static class MessageNonAck {
        private final long id;

        private final Message msg;
        private final Channel channel;
        private final long timestamp = System.currentTimeMillis();

        public MessageNonAck(Message msg, Channel channel) {
            this.msg = msg;
            this.channel = channel;

            id = msg.getSequence();
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
            messagesNonAcks.put(msgNonAck.id, msgNonAck);
        }

        @Override
        public void run() {
            for(;;){
                try{
                    for (MessageNonAck m : messagesNonAcks.values()) {
                        if (System.currentTimeMillis()-m.timestamp > TimeUnit.SECONDS.toMillis(10)) {
                            //移除
                            if (messagesNonAcks.remove(m.id) == null){
                                continue;
                            }
                            if (m.channel.isActive()) {
                                logger.warn("准备重新发送信息");
                                MessageNonAck msgNonAck = new MessageNonAck(m.msg, m.channel);
                                messagesNonAcks.put(msgNonAck.id, msgNonAck);
                                m.channel.writeAndFlush(m.msg)
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
