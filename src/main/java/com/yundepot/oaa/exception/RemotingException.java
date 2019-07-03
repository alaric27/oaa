package com.yundepot.oaa.exception;

/**
 * 远程调用异常
 * @author zhaiyanan
 * @date 2019/5/16 11:51
 */
public class RemotingException extends Exception{
    private static final long serialVersionUID = 4155889043006790873L;

    public RemotingException() {

    }

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
