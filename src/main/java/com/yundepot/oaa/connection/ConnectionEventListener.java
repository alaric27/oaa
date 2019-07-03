package com.yundepot.oaa.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接事件监听器
 * @author zhaiyanan
 * @date 2019/5/16 07:43
 */
public class ConnectionEventListener {

    private ConcurrentHashMap<ConnectionEventType, List<ConnectionEventProcessor>> processors = new ConcurrentHashMap<>(3);

    /**
     * 处理监听事件
     * @param type
     * @param connection
     */
    public void onEvent(ConnectionEventType type, Connection connection) {
        List<ConnectionEventProcessor> processorList = this.processors.get(type);
        if (processorList != null) {
            for (ConnectionEventProcessor processor : processorList) {
                processor.onEvent(connection);
            }
        }
    }

    /**
     * 添加连接事件处理器
     * @param type
     * @param processor
     */
    public void addConnectionEventProcessor(ConnectionEventType type, ConnectionEventProcessor processor) {
        List<ConnectionEventProcessor> processorList = this.processors.get(type);
        if (processorList == null) {
            this.processors.putIfAbsent(type, new ArrayList<>(1));
            processorList = this.processors.get(type);
        }
        processorList.add(processor);
    }
}
