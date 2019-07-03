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
     * 超时丢弃请求
     */
    private boolean timeoutDiscard = true;

    /**
     * 请求到达时间
     */
    private long arriveTimestamp;

    /**
     * 超时时间
     */
    private int timeout;

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

    /**
     * 请求是否超时
     * @return
     */
    public boolean isRequestTimeout() {
        if (this.timeout > 0 && (System.currentTimeMillis() - this.arriveTimestamp) > this.timeout) {
            return true;
        }
        return false;
    }

    public Connection getConnection() {
        return ConnectionUtil.getConnectionFromChannel(channelHandlerContext.channel());
    }

    public boolean isTimeoutDiscard() {
        return timeoutDiscard;
    }

    public InvokeContext setTimeoutDiscard(boolean failFastEnabled) {
        this.timeoutDiscard = failFastEnabled;
        return this;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }
}
