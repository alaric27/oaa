package com.yundepot.oaa.protocol;

import com.yundepot.oaa.protocol.codec.ProtocolDecoder;
import com.yundepot.oaa.protocol.codec.ProtocolEncoder;
import com.yundepot.oaa.protocol.command.CommandFactory;
import com.yundepot.oaa.protocol.handler.ProtocolHandler;
import com.yundepot.oaa.protocol.trigger.HeartbeatTrigger;

/**
 * 协议接口
 * @author zhaiyanan
 * @date 2019/5/15 15:03
 */
public interface Protocol {

    /**
     * 获取该协议对应的编码器
     * @return
     */
    ProtocolEncoder getEncoder();

    /**
     * 获取该协议对应的解码器
     * @return
     */
    ProtocolDecoder getDecoder();

    /**
     * 获取协议对应的心跳触发器
     * @return
     */
    HeartbeatTrigger getHeartbeatTrigger();

    /**
     * 获取协议对应的 ProtocolHandler
     * @return
     */
    ProtocolHandler getProtocolHandler();

    /**
     * 获取协议对应的CommandFactory
     * @return
     */
    CommandFactory getCommandFactory();

    /**
     * 获取协议编码
     * @return
     */
    ProtocolCode getProtocolCode();
}
