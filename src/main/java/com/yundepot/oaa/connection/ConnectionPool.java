package com.yundepot.oaa.connection;

import com.yundepot.oaa.common.Scannable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 连接池
 * @author zhaiyanan
 * @date 2019/5/15 18:06
 */
public class ConnectionPool implements Scannable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    /**
     * 保存所有的连接
     */
    private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<>();

    /**
     * 连接选择策略
     */
    private ConnectionSelectStrategy strategy;

    /**
     * 最后一次访问时间
     */
    private volatile long lastAccessTimestamp;

    public ConnectionPool(ConnectionSelectStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 添加连接
     * @param connection
     */
    public void add(Connection connection) {
        markAccess();
        if (null == connection) {
            return;
        }
        if (this.connections.addIfAbsent(connection)) {
            connection.increaseRef();
        }
    }

    /**
     * 当前连接池是否存在该连接
     * @param connection
     * @return
     */
    public boolean contains(Connection connection) {
        return this.connections.contains(connection);
    }

    /**
     * 删除并且关闭连接
     * @param connection
     */
    public void removeAndTryClose(Connection connection) {
        if (null == connection) {
            return;
        }
        if (this.connections.remove(connection)) {
            connection.decreaseRef();
        }

        if (connection.noRef()) {
            connection.close();
        }
    }

    /**
     * 删除所有连接
     */
    public void removeAllAndTryClose() {
        for (Connection connection : this.connections) {
            removeAndTryClose(connection);
        }
        this.connections.clear();
    }

    /**
     * 获取连接
     * @return
     */
    public Connection get() {
        markAccess();
        if (this.connections.size() > 0) {
            return this.strategy.select(new ArrayList<>(connections));
        }
        return null;
    }

    public List<Connection> getAll() {
        markAccess();
        return new ArrayList<>(this.connections);
    }

    public int size() {
        return this.connections.size();
    }

    public boolean isEmpty() {
        return this.connections.isEmpty();
    }

    public long getLastAccessTimestamp() {
        return this.lastAccessTimestamp;
    }

    private void markAccess() {
        this.lastAccessTimestamp = System.currentTimeMillis();
    }

    @Override
    public void scan() {
        for (Connection connection : connections) {
            if (!connection.isFine()) {
                logger.warn("remove bad connection {}:{}",connection.getRemoteIP(),connection.getRemotePort());
                connection.onClose();
                this.removeAndTryClose(connection);
            }
        }
    }
}
