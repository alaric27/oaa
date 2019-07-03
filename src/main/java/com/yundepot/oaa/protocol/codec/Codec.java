package com.yundepot.oaa.protocol.codec;

import com.yundepot.oaa.protocol.ProtocolManager;
import io.netty.channel.ChannelHandler;

/**
 * @author zhaiyanan
 * @date 2019/5/22 09:29
 */
public interface Codec {

    /**
     * 新建编码器
     * @return
     */
    ChannelHandler newEncoder();

    /**
     * 新建解码器
     * @param protocolManager
     * @return
     */
    ChannelHandler newDecoder(ProtocolManager protocolManager);
}
