package com.jonex.netty.test.attributeMap;

import java.util.Date;

/**
 * Created by Guest on 2017/9/7.
 */
public class NettyChannel {

    private String name;


    private Date createDate;


    public NettyChannel(String name,Date createDate) {
        this.name = name;
        this.createDate = createDate;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}
