package com.yundepot.oaa.connection;

import com.yundepot.oaa.common.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaiyanan
 * @date 2019/5/16 15:13
 */
public class ConnectionEventExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionEventExecutor.class);

    ExecutorService executor = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10000), new NamedThreadFactory("connection-event-executor",true));

    public void onEvent(Runnable runnable) {
        try {
            executor.execute(runnable);
        } catch (Throwable e) {
            logger.error("execute connection event error", e);
        }
    }
}
