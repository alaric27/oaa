package com.yundepot.oaa.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zhaiyanan
 * @date 2019/6/13 14:05
 */
public class MapSerializeUtil {

    public static byte[] serialize(Map<String, String> map) {
        if (null == map || map.isEmpty()) {
            return null;
        }

        int totalLength = 0;
        int kvLength;
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                kvLength = 2 + entry.getKey().getBytes(StandardCharsets.UTF_8).length + 2 + entry.getValue().getBytes(StandardCharsets.UTF_8).length;
                totalLength += kvLength;
            }
        }

        ByteBuffer content = ByteBuffer.allocate(totalLength);
        byte[] key;
        byte[] value;
        it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getKey() != null && entry.getValue() != null) {
                key = entry.getKey().getBytes(StandardCharsets.UTF_8);
                value = entry.getValue().getBytes(StandardCharsets.UTF_8);

                content.putShort((short) key.length);
                content.put(key);

                content.putShort((short) value.length);
                content.put(value);
            }
        }
        return content.array();
    }


    public static Map<String, String> deserialize(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }

        Map<String, String> map = new HashMap<>();
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        short keySize;
        byte[] keyContent;
        short valueSize;
        byte[] valueContent;
        while (byteBuffer.hasRemaining()) {
            keySize = byteBuffer.getShort();
            keyContent = new byte[keySize];
            byteBuffer.get(keyContent);

            valueSize = byteBuffer.getShort();
            valueContent = new byte[valueSize];
            byteBuffer.get(valueContent);

            map.put(new String(keyContent, StandardCharsets.UTF_8), new String(valueContent, StandardCharsets.UTF_8));
        }
        return map;
    }
}
