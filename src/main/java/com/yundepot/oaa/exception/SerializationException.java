package com.yundepot.oaa.exception;

/**
 * 序列化异常
 * @author zhaiyanan
 * @date 2019/5/16 11:55
 */
public class SerializationException extends Exception{
    private static final long serialVersionUID = -8345704145413873700L;

    public SerializationException() {

    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
