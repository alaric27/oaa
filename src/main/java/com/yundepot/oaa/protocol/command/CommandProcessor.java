package com.yundepot.oaa.protocol.command;

import com.yundepot.oaa.invoke.InvokeContext;

import java.util.concurrent.ExecutorService;

/**
 * Command 处理器
 * @author zhaiyanan
 * @date 2019/5/10 16:10
 */
public interface CommandProcessor<T extends Command> {

    /**
     * 处理remoting command
     * @param ctx
     * @param msg
     * @throws Exception
     */
    void process(InvokeContext ctx, T msg);

    /**
     * 获取执行器
     * @return
     */
    ExecutorService getExecutor();

    /**
     * 设置执行器
     * @param executor
     */
    void setExecutor(ExecutorService executor);

}
