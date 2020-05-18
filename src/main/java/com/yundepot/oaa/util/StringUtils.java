package com.yundepot.oaa.util;

import java.nio.charset.StandardCharsets;

/**
 * @author zhaiyanan
 * @date 2019/6/10 18:13
 */
public class StringUtils {


    public static final String EMPTY = "";

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtils.isEmpty(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

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
