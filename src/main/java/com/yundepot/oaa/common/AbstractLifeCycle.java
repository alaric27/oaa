package com.yundepot.oaa.common;

import com.yundepot.oaa.exception.LifeCycleException;

/**
 * @author zhaiyanan
 * @date 2019/5/31 06:25
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    private volatile boolean isStarted = false;

    @Override
    public void start() {
        if (!isStarted) {
            isStarted = true;
            return;
        }
        throw new LifeCycleException("this component has started");
    }

    @Override
    public void shutdown() {
        if (isStarted) {
            isStarted = false;
            return;
        }
        throw new LifeCycleException("this component has closed");
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }
}
