package com.yundepot.oaa.protocol.trigger;

import com.yundepot.oaa.common.TimerHolder;
import com.yundepot.oaa.config.GenericOption;
import com.yundepot.oaa.config.GlobalConfigManager;
import com.yundepot.oaa.connection.Connection;
import com.yundepot.oaa.invoke.DefaultInvokeFuture;
import com.yundepot.oaa.invoke.InvokeFuture;
import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.protocol.command.Command;
import com.yundepot.oaa.protocol.command.CommandFactory;
import com.yundepot.oaa.util.RemotingUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author: zhaiyanan
 * @date: 2019/12/10 21:16
 */
public class ProtocolHeartbeatTrigger implements HeartbeatTrigger{
    private static final Logger logger = LoggerFactory.getLogger(ProtocolHeartbeatTrigger.class);

    /**
     * 心跳最大重试次数
     */
    public static final Integer maxRetry = GlobalConfigManager.getValue(GenericOption.TCP_HEARTBEAT_MAX_RETRY);

    private static final long heartbeatTimeoutMillis = 1000;
    private CommandFactory commandFactory;
    private Protocol protocol;

    public ProtocolHeartbeatTrigger(CommandFactory commandFactory, Protocol protocol) {
        this.commandFactory = commandFactory;
        this.protocol = protocol;
    }

    @Override
    public void heartbeat(final ChannelHandlerContext ctx) {
        // 如果心跳开关关闭则不做处理
        if (!ctx.channel().attr(Connection.HEARTBEAT_SWITCH).get()) {
            return;
        }

        Integer retryCount = ctx.channel().attr(Connection.HEARTBEAT_RETRY_COUNT).get();
        final Connection conn = ctx.channel().attr(Connection.CONNECTION).get();
        // 如果心跳失败次数大于最大尝试次数，则关闭连接
        if (retryCount >= maxRetry) {
            conn.close();
            logger.error("Heartbeat failed for {} times, close the connection from client side: {}", maxRetry, RemotingUtil.parseRemoteAddress(ctx.channel()));
        } else {
            Command command = commandFactory.createHeartBeatRequest();
            final InvokeFuture future = new DefaultInvokeFuture(command.getId(),
                    (response) -> {
                        // 如果成功则重置尝试次数，否则失败次数加一
                        if (commandFactory.checkResponse(response)) {
                            ctx.channel().attr(Connection.HEARTBEAT_RETRY_COUNT).set(0);
                        } else {
                            Integer times = ctx.channel().attr(Connection.HEARTBEAT_RETRY_COUNT).get();
                            ctx.channel().attr(Connection.HEARTBEAT_RETRY_COUNT).set(times + 1);
                        }
                    }, protocol, this.commandFactory);

            final int heartbeatId = command.getId();
            conn.addInvokeFuture(future);
            ctx.writeAndFlush(command).addListener(cf -> {
                if (!cf.isSuccess()) {
                    logger.error("Send heartbeat failed! Id={}, to remoteAddr={}", heartbeatId, RemotingUtil.parseRemoteAddress(ctx.channel()));
                }
            });

            TimerHolder.getTimer().newTimeout(timeout -> {
                InvokeFuture invokeFuture = conn.removeInvokeFuture(heartbeatId);
                if (invokeFuture != null) {
                    invokeFuture.putResponse(commandFactory.createTimeoutResponse(conn.getRemoteAddress()));
                    invokeFuture.tryAsyncExecuteInvokeCallbackAbnormally();
                }
            }, heartbeatTimeoutMillis, TimeUnit.MILLISECONDS);
        }
    }
}

