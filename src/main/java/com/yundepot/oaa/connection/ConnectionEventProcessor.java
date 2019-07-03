package com.yundepot.oaa.connection;

/**
 * 连接事件处理器
 * @author zhaiyanan
 * @date 2019/5/16 07:39
 */
public interface ConnectionEventProcessor {

    void onEvent(Connection connection);
}
