package com.yundepot.oaa;

import com.yundepot.oaa.invoke.InvokeCallback;
import com.yundepot.oaa.invoke.InvokeFuture;
import com.yundepot.oaa.protocol.command.Command;
import com.yundepot.oaa.protocol.command.CommandFactory;
import com.yundepot.oaa.common.TimerHolder;
import com.yundepot.oaa.connection.Connection;
import com.yundepot.oaa.util.RemotingUtil;
import io.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 基础远程调用类，提供了sync, future,call back, one way四种方式
 * InvokeFuture以Connection为载体，在调用的时候构建InvokeFuture并加入到Connection中
 * 响应到达Connection时，并设置响应结果到对应的InvokeFuture
 *
 * @author zhaiyanan
 * @date 2019/5/20 14:09
 */
public abstract class BaseRemoting {

    private static final Logger logger = LoggerFactory.getLogger(BaseRemoting.class);

    private CommandFactory commandFactory;

    public BaseRemoting(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    /**
     * 同步调用
     * @param connection
     * @param request
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
     protected Command invokeSync(final Connection connection, final Command request,
                                  final int timeoutMillis) throws InterruptedException {
        final InvokeFuture future = createInvokeFuture(request);
        connection.addInvokeFuture(future);
        final int requestId = request.getId();

        try {
            connection.getChannel().writeAndFlush(request).addListener((cf) -> {
                if (!cf.isSuccess()) {
                    future.putResponse(commandFactory.createSendFailedResponse(connection.getRemoteAddress(), cf.cause()));
                    logger.error("invoke send failed , id={}", requestId, cf.cause());
                }
            });
            Command response = future.waitResponse(timeoutMillis);
            if (response == null) {
                response = this.commandFactory.createTimeoutResponse(connection.getRemoteAddress());
                logger.error("wait response, request id={} timeout", requestId);
            }
            return response;
        } finally {
            connection.removeInvokeFuture(requestId);
        }
    }


    /**
     * 带有call back 的异步调用
     * @param connection
     * @param request
     * @param timeoutMillis
     */
    protected void invokeWithCallback(final Connection connection, final Command request, final InvokeCallback invokeCallback, final int timeoutMillis) {
        final InvokeFuture future = createInvokeFuture(connection, request, invokeCallback);
        connection.addInvokeFuture(future);
        final int requestId = request.getId();
        try {
            // 设置超时回调
            Timeout timeout = TimerHolder.getTimer().newTimeout(timeout1 -> {
                InvokeFuture invokeFuture = connection.removeInvokeFuture(requestId);
                if (invokeFuture != null) {
                    invokeFuture.putResponse(commandFactory.createTimeoutResponse(connection.getRemoteAddress()));
                    invokeFuture.tryAsyncExecuteInvokeCallbackAbnormally();
                }
            }, timeoutMillis, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);

            connection.getChannel().writeAndFlush(request).addListener((cf) -> {
                if (!cf.isSuccess()) {
                    // 如果发送失败则发送失败响应并回调
                    InvokeFuture invokeFuture = connection.removeInvokeFuture(requestId);
                    if (invokeFuture != null) {
                        invokeFuture.cancelTimeout();
                        invokeFuture.putResponse(commandFactory.createSendFailedResponse(connection.getRemoteAddress(), cf.cause()));
                        invokeFuture.tryAsyncExecuteInvokeCallbackAbnormally();
                    }
                    logger.error("invoke send failed. the address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), cf.cause());
                }
            });
        } catch (Exception e) {
            // 如果出现异常则移除InvokeFuture、取消超时、回调
            InvokeFuture invokeFuture = connection.removeInvokeFuture(requestId);
            if (invokeFuture != null) {
                invokeFuture.cancelTimeout();
                invokeFuture.putResponse(commandFactory.createSendFailedResponse(connection.getRemoteAddress(), e));
                invokeFuture.tryAsyncExecuteInvokeCallbackAbnormally();
            }
            logger.error("invoke send failed. the address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), e);
        }

    }


    /**
     * 带有 Future的异步调用
     * @param connection
     * @param request
     * @param timeoutMillis
     * @return
     */
    protected InvokeFuture invokeWithFuture(final Connection connection, final Command request, final int timeoutMillis) {
        final InvokeFuture future = createInvokeFuture(request);
        connection.addInvokeFuture(future);
        final int requestId = request.getId();

        try {
            // 超时设置
            Timeout timeout = TimerHolder.getTimer().newTimeout((timeout1 -> {
                InvokeFuture invokeFuture = connection.removeInvokeFuture(requestId);
                if (invokeFuture != null) {
                    invokeFuture.putResponse(commandFactory.createTimeoutResponse(connection.getRemoteAddress()));
                }
            }), timeoutMillis, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);

            connection.getChannel().writeAndFlush(request).addListener((cf) -> {
                if (!cf.isSuccess()) {
                    InvokeFuture invokeFuture = connection.removeInvokeFuture(requestId);
                    if (invokeFuture != null) {
                        invokeFuture.cancelTimeout();
                        invokeFuture.putResponse(commandFactory.createSendFailedResponse(connection.getRemoteAddress(), cf.cause()));
                        logger.error("invoke send failed. the address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), cf.cause());

                    }
                }
            });
        } catch (Exception e) {
            InvokeFuture invokeFuture = connection.removeInvokeFuture(requestId);
            if (invokeFuture != null) {
                invokeFuture.cancelTimeout();
                invokeFuture.putResponse(commandFactory.createSendFailedResponse(connection.getRemoteAddress(), e));
                invokeFuture.tryAsyncExecuteInvokeCallbackAbnormally();
            }
            logger.error("invoke send failed. the address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), e);
        }
        return future;
    }

    /**
     * 不需要返回值的调用
     * @param connection
     * @param request
     */
    protected void oneway(final Connection connection, final Command request) {
        try {
            connection.getChannel().writeAndFlush(request).addListener((f) -> {
                if (!f.isSuccess()) {
                    logger.error("invoke send failed. The address is {}", RemotingUtil.parseRemoteAddress(connection.getChannel()), f.cause());
                }
            });
        } catch (Exception e) {
            logger.error("Exception caught when sending invocation. The address is {}", e);
        }
    }


    protected abstract InvokeFuture createInvokeFuture(Connection connection, Command request, InvokeCallback invokeCallback);

    protected abstract InvokeFuture createInvokeFuture(final Command request);

    protected CommandFactory getCommandFactory() {
        return commandFactory;
    }
}
