package com.yundepot.oaa.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 协议解码器
 * @author zhaiyanan
 * @date 2019/5/22 11:04
 */
public interface ProtocolDecoder {


    /**
     * 解码 ByteBuf 为实体对象
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception;
}
