package com.yundepot.oaa.connection;

import com.yundepot.oaa.util.RemotingUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 空闲服务处理器
 * @author zhaiyanan
 * @date 2019/5/23 14:43
 */
@ChannelHandler.Sharable
public class ServerIdleHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerIdleHandler.class);

    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            try {
                logger.warn("Connection idle, close it from server side: {}", RemotingUtil.parseRemoteAddress(ctx.channel()));
                ctx.close();
            } catch (Exception e) {
                logger.warn("Exception caught when closing connection in ServerIdleHandler.", e);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
