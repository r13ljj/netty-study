package com.jonex.netty.test.production.serializer;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 13:58
 */
public interface Serializer {

    <T> byte[] writeObject(T object);

    <T> T readObject(byte[] bytes, Class<T> clazz);

}
