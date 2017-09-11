package com.jonex.netty.test.production.common;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/11 15:25
 */
public class NettyCommonProtocol {

    /** 协议头长度 */
    public final static int HEAD_LENGTH = 16;
    /** Magic */
    public final static short MAGIC = (short)0xbabe;

    /** Request */
    public static final byte REQUEST = 1;
    /** Response */
    public static final byte RESPONSE = 2;

    public static final byte SERVICE_1 = 3;
    public static final byte SERVICE_2 = 4;
    public static final byte SERVICE_3 = 5;
    public static final byte SERVICE_4 = 6;


    /** Acknowledge */
    public static final byte ACK = 126;
    /** Heartbeat */
    public static final byte HEARTBEAT = 127;

    private byte sign;
    private byte status;
    private long id;
    private int bodyLength;


    public byte getSign() {
        return sign;
    }

    public void setSign(byte sign) {
        this.sign = sign;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    @Override
    public String toString() {
        return "NettyCommonProtocol{" +
                "sign=" + sign +
                ", status=" + status +
                ", id=" + id +
                ", bodyLength=" + bodyLength +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(0xbabe);
        System.out.println((short)0xbabe);
    }

}
