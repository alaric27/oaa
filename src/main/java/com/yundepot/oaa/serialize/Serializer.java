package com.yundepot.oaa.serialize;

import com.yundepot.oaa.exception.DeserializationException;
import com.yundepot.oaa.exception.SerializationException;

import java.util.Map;

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
     * @param context
     * @return
     * @throws DeserializationException
     */
    Object deserialize(final byte[]data, Map<String, String> context) throws DeserializationException;
}
