package com.yundepot.oaa.exception;

/**
 * @author zhaiyanan
 * @date 2019/5/22 09:45
 */
public class CodecException extends Exception{
    private static final long serialVersionUID = 6278231005554266729L;

    public CodecException() {

    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
