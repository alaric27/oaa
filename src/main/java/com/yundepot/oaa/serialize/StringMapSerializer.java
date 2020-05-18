package com.yundepot.oaa.serialize;

import com.yundepot.oaa.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaiyanan
 * @date 2020/5/18  11:26
 */
public final class StringMapSerializer {

    /**
     * 编码
     * @param map
     * @return
     */
    public byte[] encode(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                writeString(buf, key);
                writeString(buf, value);
            }
        }
        return buf.array();
    }


    /**
     * 解码
     * @param bytes
     * @return
     */
    public Map<String, String> decode(byte[] bytes) {
        Map<String, String> map = new HashMap<>();
        if (bytes == null || bytes.length == 0) {
            return map;
        }

        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        while (buf.readableBytes() > 0) {
            String key = readString(buf);
            String value = readString(buf);
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                map.put(key, value);
            }
        }
        return map;
    }

    private void writeString(ByteBuf buf, String str) {
        byte[] bs = StringSerializer.encode(str);
        buf.writeInt(bs.length);
        buf.writeBytes(bs);
    }

    protected String readString(ByteBuf buf) {
        int length = buf.readInt();
        if (length < 0) {
            return null;
        } else if (length == 0) {
            return StringUtils.EMPTY;
        } else {
            byte[] value = new byte[length];
            buf.readBytes(value);
            return StringSerializer.decode(value);
        }
    }
}
