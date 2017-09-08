package com.jonex.netty.test.heartbeat2;

/**
 * Created by Guest on 2017/9/8.
 */
public class AskMsg extends BaseMsg {

    private AskParams params;


    public AskMsg(){

    }

    public AskMsg(AskParams params) {
        this.params = params;
        super.setType(MsgType.ASK);
    }

    public AskParams getParams() {
        return params;
    }

    public void setParams(AskParams params) {
        this.params = params;
    }
}
