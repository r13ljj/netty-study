package com.jonex.netty.test.heartbeat2;

import java.io.Serializable;

/**
 * Created by Guest on 2017/9/8.
 */
public class AskParams implements Serializable {

    private String auth;

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}
