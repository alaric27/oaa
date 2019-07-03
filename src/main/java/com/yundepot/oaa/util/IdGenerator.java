package com.yundepot.oaa.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhaiyanan
 * @date 2019/6/13 14:05
 */
public class IdGenerator {

    private static final AtomicInteger id = new AtomicInteger(0);

    /**
     * 获取下一个id，解决越界问题
     * @return
     */
    public static int nextId() {
        int current;
        int next;
        do {
            current = id.get();
            next = current >= Integer.MAX_VALUE ? 0 : current + 1;
        } while (!id.compareAndSet(current, next));
        return next;
    }
}