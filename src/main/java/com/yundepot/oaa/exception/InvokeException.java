package com.yundepot.oaa.exception;

/**
 * @author zhaiyanan
 * @date 2019/6/26 16:12
 */
public class InvokeException extends RemotingException{

    private static final long serialVersionUID = 5601927936545486625L;

    public InvokeException() {

    }

    public InvokeException(String msg) {
        super(msg);
    }

    public InvokeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
