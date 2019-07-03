package com.yundepot.oaa.connection;

import com.yundepot.oaa.common.LifeCycle;
import com.yundepot.oaa.common.Scannable;
import com.yundepot.oaa.exception.ConnectionException;

/**
 * 连接管理器
 * @author zhaiyanan
 * @date 2019/5/15 17:42
 */
public interface ConnectionManager extends Scannable, LifeCycle {

    /**
     * 添加连接
     * @param connection
     */
    void add(Connection connection);

    /**
     * 添加连接 带有特殊的poolKey
     * @param connection
     * @param poolKey
     */
    void add(Connection connection, String poolKey);

    /**
     * 根据poolKey获取Connection
     * @param poolKey
     * @return
     */
    Connection get(String poolKey);

    /**
     * 删除连接
     * @param connection
     */
    void remove(Connection connection);

    /**
     * 删除poolKey
     * @param poolKey
     */
    void remove(String poolKey);

    /**
     * 删除和关闭所有ConnectionPool的所有连接
     */
    void removeAll();

    /**
     * 检查连接是否可用
     * @param connection
     * @throws ConnectionException
     */
    void check(Connection connection) throws ConnectionException;

    /**
     * 获取或者创建一个连接
     *
     * @param url
     * @return
     * @throws InterruptedException
     * @throws ConnectionException
     */
    Connection getAndCreateIfAbsent(Url url) throws ConnectionException;

}
