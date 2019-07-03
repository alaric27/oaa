package com.yundepot.oaa.invoke;

import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.protocol.ProtocolCode;
import com.yundepot.oaa.protocol.ProtocolManager;
import com.yundepot.oaa.protocol.command.Command;
import com.yundepot.oaa.protocol.command.CommandFactory;
import io.netty.util.Timeout;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhaiyanan
 * @date 2019/5/23 15:13
 */
public class DefaultInvokeFuture implements InvokeFuture{

    /**
     * 调用ID
     */
    private int invokeId;

    /**
     * 回调接口
     */
    private InvokeCallback callback;

    /**
     * 响应命令
     */
    private volatile Command response;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * 保证只调用一次
     */
    private final AtomicBoolean callbackOnce = new AtomicBoolean(false);
    private Timeout timeout;
    private Throwable cause;
    private ClassLoader classLoader;

    /**
     * 协议代码
     */
    private Protocol protocol;

    /**
     * 命令工厂
     */
    private CommandFactory commandFactory;

    public DefaultInvokeFuture(int invokeId, InvokeCallback callback, Protocol protocol, CommandFactory commandFactory) {
        this.invokeId = invokeId;
        this.callback = callback;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.protocol = protocol;
        this.commandFactory = commandFactory;
    }

    @Override
    public Command waitResponse(long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.response;
    }

    @Override
    public Command waitResponse() throws InterruptedException {
        this.countDownLatch.await();
        return this.response;
    }

    @Override
    public Command createConnectionClosedResponse(InetSocketAddress socketAddress) {
        return this.commandFactory.createConnectionClosedResponse(socketAddress);
    }

    /**
     * 设置响应
     * @param response
     */
    @Override
    public void putResponse(Command response) {
        this.response = response;
        this.countDownLatch.countDown();
    }

    @Override
    public int invokeId() {
        return this.invokeId;
    }

    /**
     * 执行回调
     */
    @Override
    public void executeInvokeCallback() {
        if (callback != null && this.callbackOnce.compareAndSet(false, true)) {
            callback.onResponse(response);
        }
    }

    /**
     * 异步执行回调
     */
    @Override
    public void tryAsyncExecuteInvokeCallbackAbnormally() {
        ExecutorService executorService = Optional.ofNullable(protocol)
                .map(protocol -> protocol.getProtocolHandler())
                .map(handler -> handler.getDefaultExecutor()).orElse(null);
        if (executorService != null) {
            executorService.execute(this::executeInvokeCallback);
        } else {
            executeInvokeCallback();
        }
    }

    @Override
    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    @Override
    public InvokeCallback getInvokeCallback() {
        return this.callback;
    }

    @Override
    public void addTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    @Override
    public void cancelTimeout() {
        if (this.timeout != null) {
            this.timeout.cancel();
        }
    }

    /**
     * 是否完成
     * @return
     */
    @Override
    public boolean isDone() {
        return this.countDownLatch.getCount() <= 0;
    }

    @Override
    public ClassLoader getAppClassLoader() {
        return this.classLoader;
    }

    @Override
    public ProtocolCode getProtocolCode() {
        return this.protocol.getProtocolCode();
    }
}
