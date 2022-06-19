package com.yundepot.oaa.connection;

import com.yundepot.oaa.common.AbstractLifeCycle;
import com.yundepot.oaa.common.NamedThreadFactory;
import com.yundepot.oaa.common.Scannable;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaiyanan
 * @date 2019/6/9 13:04
 */
@Slf4j
public class ConnectionScanner extends AbstractLifeCycle {

    private ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("connection-scanner-executor", true));

    private List<Scannable> scanList = new LinkedList<Scannable>();

    @Override
    public void start() {
        scheduledService.scheduleWithFixedDelay(() ->
            scanList.forEach(scannable -> {
                try {
                    scannable.scan();
                } catch (Throwable t) {
                    log.error("error when ConnectionScanner", t);
                }
            }),10000, 10000, TimeUnit.MILLISECONDS);
    }

    public void add(Scannable scannable) {
        scanList.add(scannable);
    }

    @Override
    public void shutdown() {
        scheduledService.shutdown();
    }

}
