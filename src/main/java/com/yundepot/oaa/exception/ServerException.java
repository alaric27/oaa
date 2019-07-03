package com.yundepot.oaa.exception;

/**
 * @author zhaiyanan
 * @date 2019/6/26 16:14
 */
public class ServerException extends RemotingException{

    public ServerException() {
    }

    public ServerException(String msg) {
        super(msg);
    }

    public ServerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
