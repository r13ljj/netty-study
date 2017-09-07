package com.jonex.netty.test.heartbeat2;

import com.jonex.netty.test.heartbeat2.MsgType;

import java.io.Serializable;

/**
 * Created by Guest on 2017/9/8.
 */
public abstract class BaseMsg implements Serializable {

    private MsgType type;
    private String clientId;

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
