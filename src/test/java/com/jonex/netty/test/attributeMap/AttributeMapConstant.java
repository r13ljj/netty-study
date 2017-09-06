package com.jonex.netty.test.attributeMap;

import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

/**
 * Created by Guest on 2017/9/7.
 */
public class AttributeMapConstant {

    public final static AttributeKey<NettyChannel> NETTY_CHANNEL_CTX_KEY = AttributeKey.valueOf("netty.channel.context");

    public final static AttributeKey<String> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel");

}
