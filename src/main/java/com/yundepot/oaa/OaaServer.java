package com.yundepot.oaa;

import com.yundepot.oaa.common.AbstractLifeCycle;
import com.yundepot.oaa.common.ExceptionHandler;
import com.yundepot.oaa.common.NamedThreadFactory;
import com.yundepot.oaa.config.ConfigManager;
import com.yundepot.oaa.config.ConfigOption;
import com.yundepot.oaa.config.Configurable;
import com.yundepot.oaa.config.GenericOption;
import com.yundepot.oaa.connection.*;
import com.yundepot.oaa.protocol.handler.DispatchProtocolHandler;
import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.protocol.ProtocolManager;
import com.yundepot.oaa.protocol.codec.Codec;
import com.yundepot.oaa.protocol.codec.DefaultCodec;
import com.yundepot.oaa.util.NettyEventLoopUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaiyanan
 * @date 2019/5/22 09:17
 */
public class OaaServer extends AbstractLifeCycle implements Server {

    /**
     * 配置管理
     */
    protected final ConfigManager configManager;
    /**
     * 绑定的IP
     */
    private String ip;

    /**
     * 绑定的端口
     */
    private int port;
    protected ServerBootstrap bootstrap;
    protected final EventLoopGroup bossGroup = NettyEventLoopUtil.newEventLoopGroup(1, new NamedThreadFactory("netty-server-boss"));
    protected final EventLoopGroup workerGroup = NettyEventLoopUtil.newEventLoopGroup(
            Runtime.getRuntime().availableProcessors() * 2, new NamedThreadFactory("netty-server-worker", true));

    protected ChannelFuture channelFuture;

    /**
     * 协议的编码和解码器
     */
    protected Codec codec = new DefaultCodec();

    /**
     * 连接事件监听器
     */
    protected ConnectionEventListener connectionEventListener;

    /**
     * netty 的连接事件handler
     */
    protected ServerConnectionEventHandler connectionEventHandler;

    /**
     * 协议管理器
     */
    private ProtocolManager protocolManager;

    /**
     * 如果只设置了端口，则默认绑定 0.0.0.0 IP
     * @param port
     */
    public OaaServer(int port) {
        this(new InetSocketAddress(port).getAddress().getHostAddress(), port);
    }

    /**
     * 设置IP和端口
     * @param ip
     * @param port
     */
    public OaaServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.configManager = new ConfigManager();
        this.protocolManager = new ProtocolManager();
    }

    /**
     * 启动服务
     */
    @Override
    public void start() {
        super.start();
        try {
            doInit();
            if (!doStart()){
                throw new IllegalStateException("Failed starting server on port: " + port);
            }
        } catch (Throwable t) {
            this.shutdown();
            throw new IllegalStateException("Failed to start the Server!", t);
        }
    }


    /**
     * 停止服务
     */
    @Override
    public void shutdown() {
        if (null != this.channelFuture) {
            this.channelFuture.channel().close();
        }
        if (configManager.getValue(GenericOption.SERVER_SYNC_STOP)) {
            this.bossGroup.shutdownGracefully().awaitUninterruptibly();
        } else {
            this.bossGroup.shutdownGracefully();
        }
    }

    /**
     * 获取启动的IP
     * @return
     */
    @Override
    public String ip() {
        return ip;
    }

    /**
     * 获取绑定的端口
     * @return
     */
    @Override
    public int port() {
        return port;
    }

    @Override
    public ProtocolManager getProtocolManager() {
        return this.protocolManager;
    }

    @Override
    public <T> T getValue(ConfigOption<T> option) {
        return configManager.getValue(option);
    }

    @Override
    public <T> Configurable option(ConfigOption<T> option, T value) {
        this.configManager.option(option, value);
        return this;
    }

    private void doInit() {
        if (workerGroup instanceof NioEventLoopGroup) {
            ((NioEventLoopGroup) workerGroup).setIoRatio(configManager.getValue(GenericOption.NETTY_IO_RATIO));
        } else if (workerGroup instanceof EpollEventLoopGroup) {
            ((EpollEventLoopGroup) workerGroup).setIoRatio(configManager.getValue(GenericOption.NETTY_IO_RATIO));
        }

        this.connectionEventHandler = new ServerConnectionEventHandler();
        this.connectionEventHandler.setConnectionEventListener(this.connectionEventListener);

        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(bossGroup, workerGroup)
                .channel(NettyEventLoopUtil.getServerSocketChannelClass())
                .option(ChannelOption.SO_BACKLOG, configManager.getValue(GenericOption.TCP_SO_BACKLOG))
                .option(ChannelOption.SO_REUSEADDR, configManager.getValue(GenericOption.TCP_SO_REUSEADDR))
                .childOption(ChannelOption.TCP_NODELAY, configManager.getValue(GenericOption.TCP_NODELAY))
                .childOption(ChannelOption.SO_KEEPALIVE, configManager.getValue(GenericOption.TCP_SO_KEEPALIVE));

        // 设置netty 写buffer的low-high water mark
        initWriteBufferWaterMark();

        // 初始化 buffer分配器
        if (configManager.getValue(GenericOption.NETTY_BUFFER_POOLED)) {
            this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            this.bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }

        // 为epoll模式下设置 trigger mode
        NettyEventLoopUtil.enableTriggeredMode(bootstrap);

        // 空闲开关
        final boolean idleSwitch = configManager.getValue(GenericOption.TCP_HEARTBEAT_SWITCH);
        final int idleTime = configManager.getValue(GenericOption.TCP_SERVER_IDLE);

        final ChannelHandler serverIdleHandler = new ServerIdleHandler();
        final DispatchProtocolHandler dispatchProtocolHandler = new DispatchProtocolHandler();
        this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", codec.newDecoder(protocolManager));
                pipeline.addLast("encoder", codec.newEncoder());
                if (idleSwitch) {
                    pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, idleTime, TimeUnit.MILLISECONDS));
                    pipeline.addLast("serverIdleHandler", serverIdleHandler);
                }
                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", dispatchProtocolHandler);
                pipeline.addLast("exception", new ExceptionHandler());
            }
        });
    }

    /**
     * 启动
     * @return
     * @throws InterruptedException
     */
    private boolean doStart() throws InterruptedException{
        this.channelFuture = this.bootstrap.bind(new InetSocketAddress(ip(), port())).sync();
        return this.channelFuture.isSuccess();
    }

    /**
     * 设置netty写buffer的 low high water mark
     */
    private void initWriteBufferWaterMark() {
        int lowWaterMark = configManager.getValue(GenericOption.NETTY_BUFFER_LOW_WATERMARK);
        int highWaterMark = configManager.getValue(GenericOption.NETTY_BUFFER_HIGH_WATERMARK);
        if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException("lowWaterMark should smaller than highWaterMark");
        }
        this.bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(lowWaterMark, highWaterMark));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * 注册协议
     * @param protocol
     */
    public void registerProtocol(Protocol protocol) {
        protocolManager.registerProtocol(protocol);
    }

    /**
     * 设置连接事件监听
     * @param connectionEventListener
     */
    public void setConnectionEventListener(ConnectionEventListener connectionEventListener) {
        this.connectionEventListener = connectionEventListener;
    }
}
