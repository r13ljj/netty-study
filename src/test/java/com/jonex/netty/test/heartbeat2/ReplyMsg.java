package com.jonex.netty.test.heartbeat2;

/**
 * Created by Guest on 2017/9/8.
 */
public class ReplyMsg extends BaseMsg{

    private ReplyBody replyBody;


    public ReplyMsg(ReplyBody replyBody) {
        this.replyBody = replyBody;
        super.setType(MsgType.REPLY);
    }

    public ReplyBody getReplyBody() {
        return replyBody;
    }

    public void setReplyBody(ReplyBody replyBody) {
        this.replyBody = replyBody;
    }
}
