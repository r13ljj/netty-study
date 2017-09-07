package com.jonex.netty.test.heartbeat2;

/**
 * Created by Guest on 2017/9/8.
 */
public class ReplyServerBody extends ReplyBody {

    private String serverInfo;


    public ReplyServerBody(String serverInfo) {
        this.serverInfo = serverInfo;
    }


    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }
}
