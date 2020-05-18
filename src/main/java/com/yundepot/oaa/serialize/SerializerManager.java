package com.yundepot.oaa.serialize;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaiyanan
 * @date 2019/5/23 21:36
 */
public class SerializerManager {

    private static ConcurrentHashMap<Byte, Serializer> serializers = new ConcurrentHashMap(4);
    public static final byte HESSIAN = 1;
    public static final byte STRING = 2;

    static {
        addSerializer(HESSIAN, new HessianSerializer());
        addSerializer(STRING, new StringSerializer());
    }

    /**
     * 获取序列化器
     * @param serializeCode
     * @return
     */
    public static Serializer getSerializer(byte serializeCode) {
        return serializers.get(serializeCode);
    }

    /**
     * 添加序列化器
     * @param serializeCode
     * @param serializer
     */
    public static void addSerializer(byte serializeCode, Serializer serializer) {
        Serializer exists= serializers.putIfAbsent(serializeCode, serializer);
        if (exists != null) {
            throw new RuntimeException("Serializer for code: " + serializeCode + " already exists!");
        }
    }
}
