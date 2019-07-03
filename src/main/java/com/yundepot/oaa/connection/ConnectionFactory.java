package com.yundepot.oaa.connection;

import com.yundepot.oaa.common.LifeCycle;
import com.yundepot.oaa.exception.ConnectionException;

/**
 * @author zhaiyanan
 * @date 2019/5/15 13:45
 */
public interface ConnectionFactory extends LifeCycle {

    /**
     * 创建连接
     *
     * @param url
     * @param connectTimeout
     * @return
     * @throws InterruptedException
     * @throws ConnectionException
     */
    Connection createConnection(Url url, int connectTimeout) throws ConnectionException;
}
