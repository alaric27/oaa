package com.yundepot.oaa.protocol.handler;

import com.yundepot.oaa.connection.Connection;
import com.yundepot.oaa.invoke.InvokeContext;
import com.yundepot.oaa.protocol.Protocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 根据协议编码分发到不同的ProtocolHandler
 * @author zhaiyanan
 * @date 2019/5/29 18:31
 */
@ChannelHandler.Sharable
public class DispatchProtocolHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Protocol protocol = ctx.channel().attr(Connection.PROTOCOL).get();
        protocol.getProtocolHandler().handleCommand(new InvokeContext(ctx), msg);
    }
}
