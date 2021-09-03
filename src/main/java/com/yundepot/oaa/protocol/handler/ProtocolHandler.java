package com.yundepot.oaa.protocol.handler;

import com.yundepot.oaa.invoke.InvokeContext;
import com.yundepot.oaa.protocol.command.CommandCode;
import com.yundepot.oaa.protocol.command.CommandProcessor;

import java.util.concurrent.ExecutorService;

/**
 * 协议对应的处理器
 * @author zhaiyanan
 * @date 2019/5/22 10:54
 */
public interface ProtocolHandler {

    /**
     * 处理msg
     * @param ctx
     * @param msg
     * @throws Exception
     */
    void handleCommand(InvokeContext ctx, Object msg) throws Exception;

    /**
     * 注册command code 对应的 CommandProcessor
     * @param cmd
     * @param processor
     */
    void registerCommandProcessor(CommandCode cmd, CommandProcessor<?> processor);

    /**
     * 注册默认的线程执行器
     *
     * @param executor
     */
    void registerDefaultExecutor(ExecutorService executor);

    /**
     * 获取该CommandHandler对应的 Executor
     * @return
     */
    ExecutorService getDefaultExecutor();
}
