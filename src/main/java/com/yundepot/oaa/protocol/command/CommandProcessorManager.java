package com.yundepot.oaa.protocol.command;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaiyanan
 * @date 2019/5/23 14:20
 */
public class CommandProcessorManager {
    private ConcurrentHashMap<CommandCode, CommandProcessor<?>> processors = new ConcurrentHashMap<>(4);

    private CommandProcessor<?> defaultProcessor;

    public void registerProcessor(CommandCode cmdCode, CommandProcessor<?> processor) {
        this.processors.put(cmdCode, processor);
    }

    /**
     * 注册默认处理器
     * @param processor
     */
    public void registerDefaultProcessor(CommandProcessor<?> processor) {
        this.defaultProcessor = processor;
    }

    /**
     * 获取命令处理器
     * @param cmdCode
     * @return
     */
    public CommandProcessor<?> getProcessor(CommandCode cmdCode) {
        CommandProcessor<?> processor = this.processors.get(cmdCode);
        if (processor != null) {
            return processor;
        }
        return this.defaultProcessor;
    }
}
