package com.jonex.netty.test.codec.protocol;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/7 15:39
 */
public class ProtocolHeader {

    //魔数
    private byte magic;
    //消息类型
    private byte msgType;

    private short reserve;

    private short sn;

    private int len;


    public ProtocolHeader(byte magic, byte msgType, short reserve, short sn, int len) {
        this.magic = magic;
        this.msgType = msgType;
        this.reserve = reserve;
        this.sn = sn;
        this.len = len;
    }


    public byte getMagic() {
        return magic;
    }

    public void setMagic(byte magic) {
        this.magic = magic;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public short getReserve() {
        return reserve;
    }

    public void setReserve(short reserve) {
        this.reserve = reserve;
    }

    public short getSn() {
        return sn;
    }

    public void setSn(short sn) {
        this.sn = sn;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
}
