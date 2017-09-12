package com.jonex.netty.test.production.serializer.protostuff;

import com.jonex.netty.test.production.serializer.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author jonex [r13ljj@gmail.com]
 * @Date 2017/9/12 14:01
 */
public class ProtostuffSerializer implements Serializer {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static Objenesis objenesis = new ObjenesisStd(true);


    @Override
    public <T> byte[] writeObject(T object) {
        Class<T> cls = (Class<T>)object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        try {
            T object = objenesis.newInstance(clazz);
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(bytes, object, schema);
            return object;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private <T> Schema<T> getSchema(Class<T> cls){
        Schema<T> schema = (Schema<T>)cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(cls);
            cachedSchema.put(cls, schema);
        }
        return schema;
    }

}
