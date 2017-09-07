package com.jonex.netty.test.heartbeat2;

/**
 * Created by Guest on 2017/9/8.
 */
public class PingMsg extends BaseMsg{

    public PingMsg() {
        super.setType(MsgType.PING);
    }
}
