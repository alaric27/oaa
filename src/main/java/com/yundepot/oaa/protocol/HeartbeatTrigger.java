package com.yundepot.oaa.protocol;

import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳触发器
 * @author zhaiyanan
 * @date 2019/5/16 11:41
 */
public interface HeartbeatTrigger {

    /**
     * 心跳触发
     * @param ctx
     * @throws Exception
     */
    void heartbeat(final ChannelHandlerContext ctx) throws Exception;
}
