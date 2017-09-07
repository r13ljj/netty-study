package com.jonex.netty.test.heartbeat2;

/**
 * Created by Guest on 2017/9/8.
 */
public class ReplyClientBody extends ReplyBody {

    private String clientInfo;

    public ReplyClientBody(String clientInfo) {
        this.clientInfo = clientInfo;
    }


    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }
}
