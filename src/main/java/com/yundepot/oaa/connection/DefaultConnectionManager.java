package com.yundepot.oaa.connection;

import com.yundepot.oaa.common.AbstractLifeCycle;
import com.yundepot.oaa.config.ConfigManager;
import com.yundepot.oaa.config.GenericOption;
import com.yundepot.oaa.exception.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhaiyanan
 * @date 2019/6/8 18:17
 */
public class DefaultConnectionManager extends AbstractLifeCycle implements ConnectionManager{

    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectionManager.class);

    private static final int DEFAULT_EXPIRE_TIME = 10 * 60 * 1000;

    /**
     * 连接池
     */
    private ConcurrentHashMap<String, ConnectionPool> connectionPools;

    /**
     * 连接选择策略
     */
    private ConnectionSelectStrategy connectionSelectStrategy;

    /**
     * 连接工厂
     */
    private ConnectionFactory connectionFactory;

    /**
     * 配置管理
     */
    private ConfigManager configManager;

    public DefaultConnectionManager(ConnectionFactory connectionFactory, ConfigManager configManager) {
        this(new RandomSelectStrategy(), connectionFactory, configManager);
    }

    public DefaultConnectionManager(ConnectionSelectStrategy connectionSelectStrategy, ConnectionFactory connectionFactory, ConfigManager configManager) {
        this.connectionSelectStrategy = connectionSelectStrategy;
        this.connectionFactory = connectionFactory;
        this.connectionPools = new ConcurrentHashMap<>(4);
        this.configManager = configManager;
    }

    @Override
    public void start() {
        super.start();
        connectionFactory.start();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        removeAll();
    }

    @Override
    public void add(Connection connection) {
        Optional.ofNullable(connection).map(conn -> conn.getPoolKeys())
                .ifPresent(poolKeys -> poolKeys.forEach(poolKey -> add(connection, poolKey)));
    }


    @Override
    public void add(Connection connection, String poolKey) {
        connectionPools.getOrDefault(poolKey, new ConnectionPool(connectionSelectStrategy)).add(connection);
    }

    @Override
    public Connection get(String poolKey) {
        return Optional.ofNullable(connectionPools.get(poolKey)).map(connectionPool -> connectionPool.get()).orElse(null);
    }

    @Override
    public void remove(Connection connection) {
        Optional.ofNullable(connection).map(conn -> conn.getPoolKeys()).ifPresent(poolKeys -> poolKeys.forEach(poolKey -> {
            Optional.ofNullable(connectionPools.get(poolKey)).ifPresent(pool -> {
                pool.removeAndTryClose(connection);
                if (pool.isEmpty()) {
                    connectionPools.remove(poolKey);
                }
            });
        }));
    }

    @Override
    public void remove(String poolKey) {
        Optional.ofNullable(poolKey).map(pk -> connectionPools.get(pk)).ifPresent(connectionPool -> connectionPool.removeAllAndTryClose());
    }

    @Override
    public void removeAll() {
        Iterator<String> iterator = this.connectionPools.keySet().iterator();
        while (iterator.hasNext()) {
            String poolKey = iterator.next();
            Optional.ofNullable(connectionPools.get(poolKey)).ifPresent(pool -> pool.removeAllAndTryClose());
            iterator.remove();
        }
    }
    @Override
    public void check(Connection connection) throws ConnectionException {
        if (connection == null) {
            throw new ConnectionException("Connection is null when do check!");
        }
        if (connection.getChannel() == null || !connection.getChannel().isActive()) {
            this.remove(connection);
            throw new ConnectionException("Check connection failed for address: " + connection.getUrl());
        }
        if (!connection.getChannel().isWritable()) {
            throw new ConnectionException("Check connection failed for address: " + connection.getUrl() + ", maybe write overflow!");
        }
    }

    @Override
    public void scan() {
        Iterator<String> iterator = this.connectionPools.keySet().iterator();
        while (iterator.hasNext()) {
            String poolKey = iterator.next();
            ConnectionPool pool = connectionPools.get(poolKey);
            if (null != pool) {
                pool.scan();
                if (pool.isEmpty()) {
                    if ((System.currentTimeMillis() - pool.getLastAccessTimestamp()) > DEFAULT_EXPIRE_TIME) {
                        iterator.remove();
                        logger.warn("Remove expired pool task of poolKey {} which is empty.", poolKey);
                    }
                }
            }
        }
    }

    @Override
    public Connection getAndCreateIfAbsent(Url url) throws ConnectionException {
        Connection connection = Optional.ofNullable(connectionPools.get(url.getUniqueKey())).map(connectionPool -> connectionPool.get()).orElse(null);
        if (connection != null) {
            return connection;
        }

        ConnectionPool pool = connectionPools.getOrDefault(url.getUniqueKey(), null);
        if (pool == null) {
            pool = new ConnectionPool(connectionSelectStrategy);
            connectionPools.put(url.getUniqueKey(), pool);
        }
        Connection conn = this.connectionFactory.createConnection(url, configManager.getValue(GenericOption.CREATE_CONNECTION_TIMEOUT));
        pool.add(conn);
        return conn;
    }

    public ConcurrentHashMap<String, ConnectionPool> getConnectionPools() {
        return connectionPools;
    }

    public ConnectionSelectStrategy getConnectionSelectStrategy() {
        return connectionSelectStrategy;
    }

    public void setConnectionSelectStrategy(ConnectionSelectStrategy connectionSelectStrategy) {
        this.connectionSelectStrategy = connectionSelectStrategy;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
}
