package com.jonex.netty.test.production.serializer;

import com.jonex.netty.test.production.serializer.protostuff.ProtostuffSerializer;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 14:50
 */
public class SerializerHolder {

    private  final static Serializer serializer = new ProtostuffSerializer();

    public static Serializer getSerializer(){
        return serializer;
    }
}
