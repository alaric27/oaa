package com.yundepot.oaa.protocol.codec;

import com.yundepot.oaa.protocol.ProtocolManager;
import io.netty.channel.ChannelHandler;

/**
 * 默认的编码解码器
 * @author zhaiyanan
 * @date 2019/5/22 14:44
 */
public class DefaultCodec implements Codec{

    @Override
    public ChannelHandler newEncoder() {
        return new ProtocolCodeBasedEncoder();
    }

    @Override
    public ChannelHandler newDecoder(ProtocolManager protocolManager) {
        return new ProtocolCodeBasedDecoder(protocolManager);
    }
}
