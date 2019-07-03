package com.yundepot.oaa.exception;

/**
 * @author zhaiyanan
 * @date 2019/5/16 13:39
 */
public class ConnectionException extends RemotingException{
    private static final long serialVersionUID = -4861553785742320619L;


    public ConnectionException() {

    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
