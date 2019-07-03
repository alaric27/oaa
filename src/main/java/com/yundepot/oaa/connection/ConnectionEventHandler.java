package com.yundepot.oaa.connection;

import com.yundepot.oaa.config.ConfigManager;
import com.yundepot.oaa.config.GenericOption;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 连接处理
 * @author zhaiyanan
 * @date 2019/5/16 15:08
 */
@Sharable
public class ConnectionEventHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionEventHandler.class);

    private ConnectionManager connectionManager;

    private ConnectionEventListener connectionEventListener;

    private ConnectionEventExecutor connectionEventExecutor;

    private final ConfigManager configManager;

    public ConnectionEventHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        final Connection connection = ctx.channel().attr(Connection.CONNECTION).get();

        if (connection != null) {
            connection.onClose();
        }
        super.close(ctx, promise);
    }

    /**
     * 通道失效时关闭连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
        if (null != connection) {
            // 如果开启了连接管理
            if (connectionManager != null && configManager.getValue(GenericOption.CONNECTION_MANAGE)) {
                connectionManager.remove(connection);
                // 如果开启了重连接
                if (configManager.getValue(GenericOption.CONN_RECONNECT_SWITCH)) {
                    connectionManager.getAndCreateIfAbsent(connection.getUrl());
                }
            }
            onEvent(connection, ConnectionEventType.CLOSE);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
        if (event instanceof ConnectionEventType) {
            switch ((ConnectionEventType)event) {
                case CONNECT:
                    Channel channel = ctx.channel();
                    if (null != channel) {
                        Connection connection = channel.attr(Connection.CONNECTION).get();
                        this.onEvent(connection, ConnectionEventType.CONNECT);
                    } else {
                        logger.warn("channel null when handle user triggered event");
                    }
                    break;
                    default:
                        return;
            }
        } else {
            super.userEventTriggered(ctx, event);
        }
    }

    private void onEvent(final Connection connection, final ConnectionEventType eventType) {
        if (this.connectionEventListener != null) {
            this.connectionEventExecutor.onEvent(() ->{
                ConnectionEventHandler.this.connectionEventListener.onEvent(eventType, connection);
            });
        }
    }

    /**
     * 获取监听
     * @return
     */
    public ConnectionEventListener getConnectionEventListener() {
        return this.connectionEventListener;
    }

    /**
     * 设置监听
     * @param listener
     */
    public void setConnectionEventListener(ConnectionEventListener listener) {
        if (listener != null) {
            this.connectionEventListener = listener;
            if (this.connectionEventExecutor == null) {
                this.connectionEventExecutor = new ConnectionEventExecutor();
            }
        }
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
}
