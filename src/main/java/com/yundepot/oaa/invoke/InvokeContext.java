package com.yundepot.oaa.invoke;

import com.yundepot.oaa.connection.Connection;
import com.yundepot.oaa.protocol.command.Command;
import com.yundepot.oaa.util.ConnectionUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * ChannelHandlerContext的封装类
 * @author zhaiyanan
 * @date 2019/5/21 15:46
 */
public class InvokeContext {

    private ChannelHandlerContext channelHandlerContext;

    /**
     * 请求到达时间
     */
    private long arriveTimestamp;

    /**
     * 命令类型
     */
    private byte commandType;
    /**
     * 附加字段
     */
    private Map<String, String> attachment;

    public InvokeContext(ChannelHandlerContext ctx) {
        this.channelHandlerContext = ctx;
    }

    public ChannelFuture writeAndFlush(Command msg) {
        return this.channelHandlerContext.writeAndFlush(msg);
    }

    public Connection getConnection() {
        return ConnectionUtil.getConnectionFromChannel(channelHandlerContext.channel());
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public long getArriveTimestamp() {
        return arriveTimestamp;
    }

    public void setArriveTimestamp(long arriveTimestamp) {
        this.arriveTimestamp = arriveTimestamp;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }

    public byte getCommandType() {
        return commandType;
    }

    public void setCommandType(byte commandType) {
        this.commandType = commandType;
    }
}
