package com.yundepot.oaa.connection;

import com.yundepot.oaa.config.ConfigManager;
import com.yundepot.oaa.config.GenericOption;
import com.yundepot.oaa.util.RemotingUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;


/**
 * 连接处理
 * @author zhaiyanan
 * @date 2019/5/16 15:08
 */
@Slf4j
@Sharable
public class ClientConnectionEventHandler extends ChannelDuplexHandler {
    private ConnectionEventListener connectionEventListener;
    private final ConfigManager configManager;
    private ConnectionManager connectionManager;

    public ClientConnectionEventHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.info("netty client connect {} => {}", RemotingUtil.parseSocketAddressToString(localAddress),
                RemotingUtil.parseSocketAddressToString(remoteAddress));
        super.connect(ctx, remoteAddress, localAddress, promise);
        final Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
        onEvent(connection, ConnectionEventType.CONNECT);
    }

    /**
     * 通道失效时关闭连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("netty client inactive {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
        // 如果开启了连接管理
        if (connectionManager != null && configManager.getValue(GenericOption.CONNECTION_MANAGE)) {
            connectionManager.remove(connection);
        }
        onEvent(connection, ConnectionEventType.CLOSE);
        super.channelInactive(ctx);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.info("netty client close {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
        final Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
        connection.onClose();
        super.close(ctx, promise);
    }

    private void onEvent(final Connection connection, final ConnectionEventType eventType) {
        if (this.connectionEventListener != null) {
            this.connectionEventListener.onEvent(eventType, connection);
        }
    }

    /**
     * 获取监听
     *
     * @return
     */
    public ConnectionEventListener getConnectionEventListener() {
        return this.connectionEventListener;
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setConnectionEventListener(ConnectionEventListener listener) {
        this.connectionEventListener = listener;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;

    }
}