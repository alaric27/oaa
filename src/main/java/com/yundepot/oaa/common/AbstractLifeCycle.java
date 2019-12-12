package com.yundepot.oaa.common;

import com.yundepot.oaa.exception.LifeCycleException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhaiyanan
 * @date 2019/5/31 06:25
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    @Override
    public void start() {
        if (!isStarted.compareAndSet(false, true)) {
            throw new LifeCycleException("this component has started");
        }
    }

    @Override
    public void shutdown() {
        if (!isStarted.compareAndSet(true, false)) {
            throw new LifeCycleException("this component has closed");
        }
    }

    @Override
    public boolean isStarted() {
        return isStarted.get();
    }
}
