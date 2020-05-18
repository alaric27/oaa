package com.yundepot.oaa.serialize;

import java.nio.charset.StandardCharsets;

/**
 * @author zhaiyanan
 * @date 2020/5/18  11:21
 */
public final class StringSerializer {


    /**
     * 编码
     * @param s
     * @return
     */
    public static byte[] encode(String s) {
        return s == null ? new byte[0] : s.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 解码
     * @param data
     * @return
     */
    public static String decode(byte[] data) {
        return data == null ? null : new String(data, StandardCharsets.UTF_8);
    }
}
