package com.jonex.netty.test.codec.protocol;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/7 15:41
 */
public class ProtocolMsg {

    private ProtocolHeader protocolHeader;

    private String body;


    public ProtocolMsg(){

    }

    public ProtocolMsg(ProtocolHeader protocolHeader, String body) {
        this.protocolHeader = protocolHeader;
        this.body = body;
    }


    public ProtocolHeader getProtocolHeader() {
        return protocolHeader;
    }

    public void setProtocolHeader(ProtocolHeader protocolHeader) {
        this.protocolHeader = protocolHeader;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
