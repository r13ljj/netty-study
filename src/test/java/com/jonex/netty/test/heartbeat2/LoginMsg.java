package com.jonex.netty.test.heartbeat2;

/**
 * Created by Guest on 2017/9/8.
 */
public class LoginMsg extends BaseMsg {


    private  String userName;

    private String password;

    public LoginMsg(String userName, String password) {
        this.userName = userName;
        this.password = password;
        super.setType(MsgType.LOGIN);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
