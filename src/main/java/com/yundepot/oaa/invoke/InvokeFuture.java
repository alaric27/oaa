package com.yundepot.oaa.invoke;

import com.yundepot.oaa.protocol.ProtocolCode;
import com.yundepot.oaa.protocol.command.Command;
import io.netty.util.Timeout;

import java.net.InetSocketAddress;

/**
 *
 * @author zhaiyanan
 * @date 2019/5/21 15:18
 */
public interface InvokeFuture {

    /**
     * 带有超时时间的等待响应
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    Command waitResponse(final long timeoutMillis) throws InterruptedException;

    /**
     * 无限时间的等待响应
     * @return
     * @throws InterruptedException
     */
    Command waitResponse() throws InterruptedException;

    /**
     * 当连接关闭的时候，创建一个响应
     * @param socketAddress
     * @return
     */
    Command createConnectionClosedResponse(InetSocketAddress socketAddress);

    /**
     * put the response to future
     * @param response
     */
    void putResponse(final Command response);

    /**
     * 获取调用ID
     * @return
     */
    int invokeId();

    /**
     * 执行回调
     */
    void executeInvokeCallback();

    /**
     * 异步执行回调
     */
    void tryAsyncExecuteInvokeCallbackAbnormally();

    /**
     * 设置异常
     * @param cause
     */
    void setCause(Throwable cause);

    /**
     * 获取异常
     * @return
     */
    Throwable getCause();

    /**
     * 获取回调接口
     * @return
     */
    InvokeCallback getInvokeCallback();

    /**
     * 添加超时执行
     * @param timeout
     */
    void addTimeout(Timeout timeout);

    /**
     * 取消超时执行
     */
    void cancelTimeout();

    /**
     * 是否执行完成
     * @return
     */
    boolean isDone();

    /**
     * 获取ClassLoader
     * @return
     */
    ClassLoader getAppClassLoader();

    /**
     * 获取协议
     * @return
     */
    ProtocolCode getProtocolCode();

}
