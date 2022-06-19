package com.yundepot.oaa.connection;

import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: zhaiyanan
 * @date: 2019/12/10 19:58
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerConnectionEventHandler extends ChannelDuplexHandler {

    private ConnectionEventListener connectionEventListener;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteUrl = RemotingUtil.parseRemoteAddress(channel);
        log.info("netty server active {}", remoteUrl);
        Connection connection = new Connection(channel, null, Url.parse(remoteUrl));
        super.channelActive(ctx);
        onEvent(connection, ConnectionEventType.CONNECT);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String remoteUrl = RemotingUtil.parseRemoteAddress(channel);
        log.info("netty server inactive {}", remoteUrl);
        Protocol protocol = channel.attr(Connection.PROTOCOL).get();
        Connection connection = new Connection(channel, protocol, Url.parse(remoteUrl));
        onEvent(connection, ConnectionEventType.CLOSE);
        super.channelInactive(ctx);
    }

    private void onEvent(final Connection connection, final ConnectionEventType eventType) {
        if (this.connectionEventListener != null) {
            this.connectionEventListener.onEvent(eventType, connection);
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
        this.connectionEventListener = listener;
    }

}
