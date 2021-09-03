package com.yundepot.oaa.protocol.codec;

import com.yundepot.oaa.connection.Connection;
import com.yundepot.oaa.exception.CodecException;
import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.protocol.ProtocolCode;
import com.yundepot.oaa.protocol.ProtocolManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 基于协议编码的解码器, 作用为解析出协议编码，并且根据协议编码找到对应的ProtocolDecoder进行解析
 * @author zhaiyanan
 * @date 2019/5/22 09:32
 */
public class ProtocolCodeBasedDecoder extends ByteToMessageDecoder {

    public static final int DEFAULT_PROTOCOL_LENGTH = 2;

    private ProtocolManager protocolManager;

    public ProtocolCodeBasedDecoder(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ProtocolCode protocolCode = decodeProtocolCode(in);
        Protocol protocol = getProtocol(ctx, protocolCode);
        if (protocol == null) {
            throw new CodecException("Unknown protocol code: " + protocolCode);
        }
        protocol.getDecoder().decode(ctx, in, out);
    }


    /**
     * 获取协议
     * @param ctx
     * @param protocolCode
     * @return
     */
    private Protocol getProtocol(ChannelHandlerContext ctx, ProtocolCode protocolCode) {
        Protocol protocol = ctx.channel().attr(Connection.PROTOCOL).get();
        if (protocol == null) {
            protocol = protocolManager.getProtocol(protocolCode);
        }
        setConnectionAttr(ctx, protocol);
        return protocol;
    }

    /**
     * 解析出协议编码
     * @param in
     * @return
     */
    protected ProtocolCode decodeProtocolCode(ByteBuf in) {
        ProtocolCode protocolCode = null;
        in.markReaderIndex();
        if (in.readableBytes() >= DEFAULT_PROTOCOL_LENGTH) {
            protocolCode = ProtocolCode.getProtocolCode(in.readByte(), in.readByte());
            in.resetReaderIndex();
        }
        return protocolCode;
    }

    /**
     * 设置channel的属性，协议和编码
     * @param ctx
     * @param protocol
     */
    private void setConnectionAttr(ChannelHandlerContext ctx, Protocol protocol) {
        if (ctx.channel().attr(Connection.PROTOCOL).get() == null) {
            ctx.channel().attr(Connection.PROTOCOL).set(protocol);
        }
    }
}
