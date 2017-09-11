package com.jonex.netty.test.production.common;

/**
 * ACK确认
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/11 15:22
 */
public class Acknowledge {

    //ACK序列号
    private long sequence;


    public Acknowledge(long sequence) {
        this.sequence = sequence;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
