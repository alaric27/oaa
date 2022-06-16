package com.yundepot.oaa.protocol.command;

import java.net.InetSocketAddress;

/**
 * 命令工厂
 * @author zhaiyanan
 * @date 2019/5/22 11:07
 */
public interface CommandFactory {

    /**
     * 创建请求命令
     * @param request
     * @param <T>
     * @return
     */
    <T extends Command> T createRequest(final Object request);

    /**
     * 创建请求命令
     * @param commandCode
     * @param request
     * @param <T>
     * @return
     */
    <T extends Command> T createRequest(short commandCode, final Object request);

    /**
     * 创建响应命令
     * @param response
     * @param request
     * @param <T>
     * @return
     */
    <T extends Command> T createResponse(Command request, final Object response);

    /**
     * 创建异常响应 -- 服务端响应
     * @param request
     * @param errMsg
     * @param <T>
     * @return
     */
    <T extends Command> T createExceptionResponse(Command request, final Throwable t, String errMsg);


    /**
     * 创建超时响应 -- 客户端响应
     * @param address
     * @param <T>
     * @return
     */
    <T extends Command> T createTimeoutResponse(final InetSocketAddress address);

    /**
     * 创建发送失败响应 -- 客户端响应
     * @param address
     * @param throwable
     * @param <T>
     * @return
     */
    <T extends Command> T createSendFailedResponse(final InetSocketAddress address, Throwable throwable);

    /**
     * 创建连接失败响应 -- 客户端响应
     * @param address
     * @param <T>
     * @return
     */
    <T extends Command> T createConnectionClosedResponse(final InetSocketAddress address);
}
