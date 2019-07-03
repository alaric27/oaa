package com.yundepot.oaa.serialize;

import com.yundepot.oaa.exception.SerializationException;

/**
 * @author zhaiyanan
 * @date 2019/5/21 19:15
 */
public interface Serializer {

    /**
     * 序列化
     * @param object
     * @return
     * @throws SerializationException
     */
    byte[] serialize(final Object object) throws SerializationException;

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @param <T>
     * @return
     * @throws SerializationException
     */
    <T> T deserialize(final byte[]data, String clazz) throws SerializationException;
}
