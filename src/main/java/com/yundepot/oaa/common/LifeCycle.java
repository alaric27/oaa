package com.yundepot.oaa.common;

/**
 * @author zhaiyanan
 * @date 2019/5/31 06:23
 */
public interface LifeCycle {

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void shutdown();

    /**
     * 是否启动
     * @return
     */
    boolean isStarted();
}
