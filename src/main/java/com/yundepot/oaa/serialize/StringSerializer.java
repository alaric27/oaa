package com.yundepot.oaa.serialize;

import com.yundepot.oaa.exception.DeserializationException;
import com.yundepot.oaa.exception.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * @author zhaiyanan
 * @date 2020/5/18  11:21
 */
public final class StringSerializer implements Serializer{

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        String str = (String) object;
        return str == null ? new byte[0] : str.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String deserialize(byte[] data, String clazz) throws DeserializationException {
        return data == null ? null : new String(data, StandardCharsets.UTF_8);
    }
}
