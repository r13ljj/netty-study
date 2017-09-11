package com.jonex.netty.test.production.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 发布订阅信息的包装类
 * Created by Guest on 2017/9/12.
 */
public class Message {

    private final static AtomicInteger sequenceGenerator = new AtomicInteger(0);

    private final long sequence;
    private short sign;
    private long version;
    private Object data;


    public Message(){
        this(sequenceGenerator.getAndIncrement());
    }

    public Message(long sequence) {
        this.sequence = sequence;
    }


    public long getSequence() {
        return sequence;
    }

    public short getSign() {
        return sign;
    }

    public void setSign(short sign) {
        this.sign = sign;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sequence=" + sequence +
                ", sign=" + sign +
                ", version=" + version +
                ", data=" + data +
                '}';
    }
}
