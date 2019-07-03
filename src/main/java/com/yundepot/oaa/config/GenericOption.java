package com.yundepot.oaa.config;

/**
 * @author zhaiyanan
 * @date 2019/5/27 15:14
 */
public class GenericOption {

    public static final ConfigOption<Boolean> TCP_NODELAY = ConfigOption.valueOf("tcp.nodelay", true);
    public static final ConfigOption<Boolean> TCP_SO_REUSEADDR = ConfigOption.valueOf("tcp.so.reuseadd", true);
    public static final ConfigOption<Integer> TCP_SO_BACKLOG = ConfigOption.valueOf("tcp.so.backlog", 1024);
    public static final ConfigOption<Boolean> TCP_SO_KEEPALIVE = ConfigOption.valueOf("tcp.so.keepalive", true);
    public static final ConfigOption<Integer> NETTY_IO_RATIO = ConfigOption.valueOf("netty.io.ratio", 70);
    public static final ConfigOption<Boolean> NETTY_BUFFER_POOLED = ConfigOption.valueOf("netty.buffer.pooled", true);
    public static final ConfigOption<Integer> NETTY_BUFFER_LOW_WATERMARK = ConfigOption.valueOf("netty.buffer.low.watermark", 32 * 1024);
    public static final ConfigOption<Integer> NETTY_BUFFER_HIGH_WATERMARK = ConfigOption.valueOf("netty.buffer.high.watermark", 64 * 1024);
    public static final ConfigOption<Boolean> NETTY_EPOLL_SWITCH = ConfigOption.valueOf("netty.epoll.switch", true);
    public static final ConfigOption<Boolean> NETTY_EPOLL_LT = ConfigOption.valueOf("netty.epoll.lt", true);
    public static final ConfigOption<Boolean> TCP_HEARTBEAT_SWITCH = ConfigOption.valueOf("tcp.heartbeat.switch", true);
    public static final ConfigOption<Integer> TCP_HEARTBEAT_INTERVAL = ConfigOption.valueOf("tcp.heartbeat.interval", 15000);
    public static final ConfigOption<Integer> TCP_HEARTBEAT_MAX_RETRY = ConfigOption.valueOf("tcp.heartbeat.max.retry", 3);
    public static final ConfigOption<Integer> TCP_SERVER_IDLE = ConfigOption.valueOf("tcp.server.idle.interval", 90 * 1000);

    /** 连接管理 */
    public static final ConfigOption<Boolean> CONNECTION_MANAGE = ConfigOption.valueOf("server.manage.connection", false);
    public static final ConfigOption<Integer> CREATE_CONNECTION_TIMEOUT = ConfigOption.valueOf("create.connection.timeout", 1000);
    /** 服务同步停止 */
    public static final ConfigOption<Boolean> SERVER_SYNC_STOP = ConfigOption.valueOf("server.sync.stop", false);

    /** 重连接开关 */
    public static final ConfigOption<Boolean> CONN_RECONNECT_SWITCH = ConfigOption.valueOf("conn.reconnect.switch", false);

    /** CommandProcessor 默认线程池配置*/
    public static final ConfigOption<Integer> TP_MIN_SIZE = ConfigOption.valueOf("tp.min", 20);
    public static final ConfigOption<Integer> TP_MAX_SIZE = ConfigOption.valueOf("tp.max", 400);
    public static final ConfigOption<Integer> TP_QUEUE_SIZE = ConfigOption.valueOf("tp.queue", 600);
    public static final ConfigOption<Integer> TP_KEEPALIVE_TIME = ConfigOption.valueOf("tp.keepalive", 60);

}
