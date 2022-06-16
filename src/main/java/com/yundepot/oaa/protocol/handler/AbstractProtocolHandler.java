package com.yundepot.oaa.protocol.handler;

import com.yundepot.oaa.common.NamedThreadFactory;
import com.yundepot.oaa.config.GenericOption;
import com.yundepot.oaa.config.GlobalConfigManager;
import com.yundepot.oaa.invoke.InvokeContext;
import com.yundepot.oaa.protocol.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaiyanan
 * @date 2019/5/29 18:51
 */
public abstract class AbstractProtocolHandler implements ProtocolHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProtocolHandler.class);
    private CommandProcessorManager commandProcessorManager;
    private ExecutorService executorService;

    private int minPoolSize = GlobalConfigManager.getValue(GenericOption.TP_MIN_SIZE);
    private int maxPoolSize = GlobalConfigManager.getValue(GenericOption.TP_MAX_SIZE);
    private int queueSize = GlobalConfigManager.getValue(GenericOption.TP_QUEUE_SIZE);
    private long keepAliveTime = GlobalConfigManager.getValue(GenericOption.TP_KEEPALIVE_TIME);


    public AbstractProtocolHandler() {
        executorService = new ThreadPoolExecutor(minPoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize), new NamedThreadFactory("protocol-handler-executor", true));

        this.commandProcessorManager = new CommandProcessorManager();
        commandProcessorManager.registerDefaultProcessor(new AbstractCommandProcessor<Command>() {
            @Override
            public void doProcess(InvokeContext ctx, Command msg) {
                logger.error("No processor available for command code {}, msgId {}", msg.getCommandCode(), msg.getId());
            }
        });
    }

    @Override
    public void handleCommand(InvokeContext ctx, Object msg) {
        final Command cmd = (Command) msg;
        final CommandProcessor processor = commandProcessorManager.getProcessor(cmd.getCommandCode());
        try {
            executorService.execute(() -> processor.process(ctx, cmd));
        } catch (Throwable t) {
            logger.error("handle command exception requestId{}", cmd.getId(), t);
            processCommandException(ctx, msg, t);
        }
    }

    /**
     * 异常处理
     * @param ctx
     * @param msg
     * @param t
     */
    protected abstract void  processCommandException(InvokeContext ctx, Object msg, Throwable t);

    /**
     * 注册处理
     * @param commandCode
     * @param processor
     */
    @Override
    public void registerCommandProcessor(short commandCode, CommandProcessor processor) {
        this.commandProcessorManager.registerProcessor(commandCode, processor);
    }

    /**
     * 注册默认线程执行器
     * @param executor
     */
    @Override
    public void registerDefaultExecutor(ExecutorService executor) {
        this.executorService = executor;
    }

    /**
     * 获取默认线程执行器
     * @return
     */
    @Override
    public ExecutorService getDefaultExecutor() {
        return this.executorService;
    }

}
