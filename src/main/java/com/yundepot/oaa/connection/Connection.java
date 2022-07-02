package com.yundepot.oaa.connection;

import com.yundepot.oaa.invoke.InvokeFuture;
import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.util.ConcurrentHashSet;
import com.yundepot.oaa.util.RemotingUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhaiyanan
 * @date 2019/5/15 16:10
 */
@Slf4j
public class Connection {

    /**
     * netty 的 channel
     */
    private Channel channel;

    /**
     * id 和 InvokeFuture的对应
     */
    private final ConcurrentHashMap<Integer, InvokeFuture> invokeFutureMap = new ConcurrentHashMap<>(4);

    /**
     * 连接的 AttributeKey key
     */
    public static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");

    /**
     * 心跳最大重试数 AttributeKey key
     */
    public static final AttributeKey<Integer> HEARTBEAT_RETRY_COUNT = AttributeKey.valueOf("heartbeatRetryCount");

    /**
     * 心跳开关的 AttributeKey key
     */
    public static final AttributeKey<Boolean> HEARTBEAT_SWITCH = AttributeKey.valueOf("heartbeatSwitch");

    /**
     * 协议的 AttributeKey key
     */
    public static final AttributeKey<Protocol> PROTOCOL = AttributeKey.valueOf("protocol");

    /**
     * 协议
     */
    private Protocol protocol;

    /**
     * 请求连接的url
     */
    private Url url;

    /**
     * id 和 poolKey的对应
     */
    private final ConcurrentHashMap<Integer, String> id2PoolKey = new ConcurrentHashMap<>(256);

    /**
     * 所有的 pool key
     */
    private Set<String> poolKeys = new ConcurrentHashSet<>();

    /**
     * 是否已经关闭
     */
    private AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * 保存当前连接的所有属性
     */
    private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    /**
     * 当前连接的引用数
     */
    private final AtomicInteger referenceCount = new AtomicInteger();

    /**
     * 常量 表示连接没有被引用时的引用数量
     */
    private static final int NO_REFERENCE = 0;

    /**
     * 创建连接并绑定到channel上
     * @param channel
     */
    public Connection(Channel channel, Protocol protocol, Url url) {
        this.channel = channel;
        this.url = url;
        this.poolKeys.add(url.getUniqueKey());
        this.protocol = protocol;
        this.init();
    }

    public Connection(Channel channel, Url url) {
        this.channel = channel;
        this.url = url;
        this.poolKeys.add(url.getUniqueKey());

        this.channel.attr(CONNECTION).set(this);
        this.channel.attr(HEARTBEAT_RETRY_COUNT).set(0);
        this.channel.attr(HEARTBEAT_SWITCH).set(true);
    }

    private void init() {
        this.channel.attr(CONNECTION).set(this);
        this.channel.attr(HEARTBEAT_RETRY_COUNT).set(0);
        this.channel.attr(PROTOCOL).set(this.protocol);
        this.channel.attr(HEARTBEAT_SWITCH).set(true);
    }

    /**
     * 检测当前连接是否可用
     * @return
     */
    public boolean isFine() {
        return this.channel != null && this.channel.isActive();
    }

    /**
     * 增加连接的引用数
     */
    public void increaseRef() {
        this.referenceCount.getAndIncrement();
    }

    /**
     * 减少连接的引用数
     */
    public void decreaseRef() {
        this.referenceCount.getAndDecrement();
    }

    /**
     * 检测当前连接是否被引用
     */
    public boolean noRef() {
        return this.referenceCount.get() == NO_REFERENCE;
    }

    /**
     * 获取远程连接地址
     * @return
     */
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    /**
     * 获取远程连接地址
     * @return
     */
    public String getRemoteIP() {
        return RemotingUtil.parseRemoteIP(this.channel);
    }

    /**
     * 获取远程连接端口
     * @return
     */
    public int getRemotePort() {
        return RemotingUtil.parseRemotePort(this.channel);
    }

    /**
     * 获取本地连接地址
     * @return
     */
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) this.channel.localAddress();
    }

    /**
     * 获取连接本地IP
     * @return
     */
    public String getLocalIP() {
        return RemotingUtil.parseLocalIP(this.channel);
    }

    /**
     * 获取连接本地端口
     * @return
     */
    public int getLocalPort() {
        return RemotingUtil.parseLocalPort(this.channel);
    }

    /**
     * 获取当前连接对应的netty的channel
     * @return
     */
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * 根据id 获取InvokeFuture
     * @param id
     * @return
     */
    public InvokeFuture getInvokeFuture(int id) {
        return this.invokeFutureMap.get(id);
    }

    /**
     * 添加 InvokeFuture
     * @param future
     * @return
     */
    public InvokeFuture addInvokeFuture(InvokeFuture future) {
        return this.invokeFutureMap.putIfAbsent(future.invokeId(), future);
    }


    /**
     * 删除InvokeFuture
     * @param id
     * @return
     */
    public InvokeFuture removeInvokeFuture(int id) {
        return this.invokeFutureMap.remove(id);
    }


    public void onClose() {
        Iterator<Map.Entry<Integer, InvokeFuture>> iterator = invokeFutureMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, InvokeFuture> entry = iterator.next();
            iterator.remove();
            InvokeFuture future = entry.getValue();
            if (future != null) {
                future.putResponse(future.createConnectionClosedResponse(this.getRemoteAddress()));
                future.cancelTimeout();
                future.tryAsyncExecuteInvokeCallbackAbnormally();
            }
        }
    }


    public void close() {
        if (closed.compareAndSet(false, true)) {
            try {
                if (this.getChannel() != null) {
                    this.getChannel().close().addListener((channelFuture) ->{
                        log.debug("close the connection address={},result={},cause={}",
                                RemotingUtil.parseRemoteAddress(Connection.this.getChannel()),
                                channelFuture.isSuccess(),channelFuture.cause());
                    });
                }
            } catch (Exception e) {
                log.error("Exception caught when closing connection {}",RemotingUtil.parseRemoteAddress(Connection.this.getChannel()), e);
            }
        }
    }

    /**
     * InvokeFuture是否完成
     * @return
     */
    public boolean isInvokeFutureMapFinish() {
        return invokeFutureMap.isEmpty();
    }

    /**
     * 添加poolKey
     * @param poolKey
     */
    public void addPoolKey(String poolKey) {
        poolKeys.add(poolKey);
    }

    /**
     * 获取所有的 pool key
     * @return
     */
    public Set<String> getPoolKeys() {
        return new HashSet<>(poolKeys);
    }

    /**
     * 删除pool key
     * @param poolKey
     */
    public void removePoolKey(String poolKey) {
        poolKeys.remove(poolKey);
    }

    public Url getUrl() {
        return this.url;
    }

    /**
     * 添加id 和 poolkey 的映射
     * @param id
     * @param poolKey
     */
    public void addIdPoolKeyMapping(Integer id, String poolKey) {
        this.id2PoolKey.put(id, poolKey);
    }

    /**
     * 删除id 和连接的映射
     * @param id
     * @return
     */
    public String removeIdPoolKeyMapping(Integer id) {
        return this.id2PoolKey.remove(id);
    }

    /**
     * 设置属性
     * @param key
     * @param value
     */
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    /**
     * 如果不存在key，则设置属性
     * @param key
     * @param value
     * @return
     */
    public Object setAttributeIfAbsent(String key, Object value) {
        return attributes.putIfAbsent(key, value);
    }

    /**
     * 删除属性
     * @param key
     */
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    /**
     * 获取属性
     * @param key
     * @return
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * 清除属性
     */
    public void clearAttributes() {
        attributes.clear();
    }

    public ConcurrentHashMap<Integer, InvokeFuture> getInvokeFutureMap() {
        return this.invokeFutureMap;
    }
}
