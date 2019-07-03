package com.yundepot.oaa.common;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

import java.util.concurrent.TimeUnit;

/**
 * @author zhaiyanan
 * @date 2019/5/21 14:30
 */
public class TimerHolder {
    private final static long DEFAULT_TICK_DURATION = 10;

    private TimerHolder() {

    }

    public static Timer getTimer() {
        return DefaultInstance.INSTANCE;
    }

    private static class DefaultInstance {
        private static final Timer INSTANCE = new HashedWheelTimer(new NamedThreadFactory(
                "DefaultTimer" + DEFAULT_TICK_DURATION, true), DEFAULT_TICK_DURATION, TimeUnit.MILLISECONDS);
    }
}
