package com.jonex.netty.test.keepalive;

import java.io.Serializable;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/8 15:19
 */
public class KeepAliveMessage implements Serializable{

    private String sn;

    private int code;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
