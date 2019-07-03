package com.yundepot.oaa.protocol.command;

import com.yundepot.oaa.exception.SerializationException;
import com.yundepot.oaa.protocol.ProtocolCode;

import java.io.Serializable;

/**
 * @author zhaiyanan
 * @date 2019/5/21 14:53
 */
public interface Command extends Serializable {

    /**
     * 获取协议编码
     * @return
     */
    ProtocolCode getProtocolCode();

    /**
     * 获取命令编码
     * @return
     */
    CommandCode getCommandCode();

    /**
     * 获取请求ID
     * @return
     */
    int getId();

    /**
     * 获取序列化类型
     * @return
     */
    byte getSerializer();

    /**
     * 序列化RemotingCommand的所有内容
     * @throws SerializationException
     */
    void serialize() throws SerializationException;

    /**
     * 反序列化RemotingCommand的所有内容
     * @throws SerializationException
     */
    void deserialize() throws SerializationException;
}
