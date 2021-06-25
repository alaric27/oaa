package com.yundepot.oaa.exception;

/**
 * 反序列化异常
 * @author zhaiyanan
 * @date 2021/6/25  21:30
 */
public class DeserializationException extends Exception{
    private static final long serialVersionUID = -5121901499404586357L;

    public DeserializationException() {

    }

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
