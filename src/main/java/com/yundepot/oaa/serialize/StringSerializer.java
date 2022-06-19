package com.yundepot.oaa.serialize;

import com.yundepot.oaa.exception.DeserializationException;
import com.yundepot.oaa.exception.SerializationException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author zhaiyanan
 * @date 2020/5/18  11:21
 */
public final class StringSerializer implements Serializer{

    @Override
    public byte[] serialize(Object object, Map<String, String> context) throws SerializationException {
        String str = (String) object;
        return str == null ? new byte[0] : str.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object deserialize(byte[] data, Map<String, String> context) throws DeserializationException {
        return data == null ? null : new String(data, StandardCharsets.UTF_8);
    }
}
