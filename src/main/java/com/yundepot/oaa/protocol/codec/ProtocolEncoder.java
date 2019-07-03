package com.yundepot.oaa.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 * 协议编码器
 * @author zhaiyanan
 * @date 2019/5/22 10:58
 */
public interface ProtocolEncoder {

    /**
     * 编码msg为ByteBuf
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception;
}
