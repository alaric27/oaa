package com.yundepot.oaa.protocol.codec;

import com.yundepot.oaa.connection.Connection;
import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.protocol.ProtocolCode;
import com.yundepot.oaa.protocol.ProtocolManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;

import java.io.Serializable;

/**
 * 基于protocol code 的编码器, 主要作用是根据协议获取对应的 ProtocolEncoder进行编码
 * @author zhaiyanan
 * @date 2019/5/22 10:19
 */
@Sharable
public class ProtocolCodeBasedEncoder extends MessageToByteEncoder<Serializable> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        Protocol protocol = ctx.channel().attr(Connection.PROTOCOL).get();
        protocol.getEncoder().encode(ctx, msg, out);
    }
}
