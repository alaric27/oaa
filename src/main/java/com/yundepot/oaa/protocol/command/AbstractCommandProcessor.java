package com.yundepot.oaa.protocol.command;

import com.yundepot.oaa.invoke.InvokeContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * 处理remoting command
 * @author zhaiyanan
 * @date 2019/5/21 16:41
 */
@Slf4j
public abstract class AbstractCommandProcessor<T extends Command> implements CommandProcessor<T> {
    private ExecutorService executor;
    private CommandFactory commandFactory;

    public AbstractCommandProcessor() {

    }

    public AbstractCommandProcessor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public AbstractCommandProcessor(ExecutorService executor, CommandFactory commandFactory) {
        this.executor = executor;
        this.commandFactory = commandFactory;
    }

    /**
     * 处理remoting command
     * @param ctx
     * @param msg
     */
    @Override
    public void process(InvokeContext ctx, T msg) {
        ExecutorService executor = this.getExecutor();

        // 如果CommandProcessor设置了线程池，则在CommandProcessor线程池中执行，
        // 否则继续在ProtocolHandler的线程中执行
        if (executor != null) {
            executor.execute(() -> {
                safeProcess(ctx, msg);
            });
        } else {
            safeProcess(ctx, msg);
        }
    }

    private void safeProcess(InvokeContext ctx, T msg) {
        try {
            AbstractCommandProcessor.this.doProcess(ctx, msg);
        } catch (Throwable e) {
            processException(ctx, msg,e);
        }
    }

    /**
     * 异常处理, 子类需要根据情况覆盖该方法
     * @param ctx
     * @param msg
     * @param e
     */
    protected void processException(InvokeContext ctx, T msg, Throwable e) {
        log.error("CommandProcessor doProcess Exception", e);
    }

    /**
     * 具体处理逻辑留给子类实现
     * @param ctx
     * @param msg
     * @throws Exception
     */
    protected abstract void doProcess(InvokeContext ctx, T msg);

    @Override
    public ExecutorService getExecutor() {
        return this.executor;
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }
}
