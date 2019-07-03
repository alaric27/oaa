package com.yundepot.oaa.connection;

import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.protocol.ProtocolCode;
import com.yundepot.oaa.protocol.ProtocolManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳handler，根据不同的协议编码分发到不同的HeartbeatTrigger
 * @author zhaiyanan
 * @date 2019/5/29 18:44
 */
@ChannelHandler.Sharable
public class HeartbeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Protocol protocol = ctx.channel().attr(Connection.PROTOCOL).get();
            protocol.getHeartbeatTrigger().heartbeat(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
