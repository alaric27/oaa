package com.yundepot.oaa.connection;


import com.yundepot.oaa.common.AbstractLifeCycle;
import com.yundepot.oaa.common.NamedThreadFactory;
import com.yundepot.oaa.config.ConfigManager;
import com.yundepot.oaa.config.GenericOption;
import com.yundepot.oaa.exception.ConnectionException;
import com.yundepot.oaa.protocol.handler.DispatchProtocolHandler;
import com.yundepot.oaa.protocol.Protocol;
import com.yundepot.oaa.protocol.codec.Codec;
import com.yundepot.oaa.protocol.codec.DefaultCodec;
import com.yundepot.oaa.util.NettyEventLoopUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaiyanan
 * @date 2019/5/15 13:52
 */
public class DefaultConnectionFactory extends AbstractLifeCycle implements ConnectionFactory{
    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectionFactory.class);

    private static final EventLoopGroup workerGroup = NettyEventLoopUtil
                    .newEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1,
                    new NamedThreadFactory("remoting-netty-client-worker", true));

    private final ConfigManager configManager;
    private final Codec codec;
    private final ChannelHandler heartbeatHandler;
    private final ChannelHandler handler;
    protected Bootstrap bootstrap;
    private Protocol protocol;
    private ClientConnectionEventHandler connectionEventHandler;

    public DefaultConnectionFactory(ClientConnectionEventHandler connectionEventHandler, ConfigManager configManager, Protocol protocol) {
        this.configManager = configManager;
        this.codec = new DefaultCodec();
        this.heartbeatHandler = new HeartbeatHandler();
        this.handler = new DispatchProtocolHandler();
        this.protocol = protocol;
        this.connectionEventHandler = connectionEventHandler;
    }


    @Override
    public void start() {
        super.start();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NettyEventLoopUtil.getClientSocketChannelClass())
                .option(ChannelOption.TCP_NODELAY, configManager.getValue(GenericOption.TCP_NODELAY))
                .option(ChannelOption.SO_REUSEADDR, configManager.getValue(GenericOption.TCP_SO_REUSEADDR))
                .option(ChannelOption.SO_KEEPALIVE, configManager.getValue(GenericOption.TCP_SO_KEEPALIVE));

        // 初始化netty write buffer water mark
        initWriteBufferWaterMark();

        if (configManager.getValue(GenericOption.NETTY_BUFFER_POOLED)) {
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        } else {
            bootstrap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("decoder", codec.newDecoder(null));
                pipeline.addLast("encoder", codec.newEncoder());

                boolean idleSwitch = configManager.getValue(GenericOption.TCP_HEARTBEAT_SWITCH);
                if (idleSwitch) {
                    pipeline.addLast("idleStateHandler", new IdleStateHandler(
                            0,0, configManager.getValue(GenericOption.TCP_HEARTBEAT_INTERVAL), TimeUnit.MILLISECONDS));
                    pipeline.addLast("heartbeatHandler", heartbeatHandler);
                }
                pipeline.addLast("connectionEventHandler", connectionEventHandler);
                pipeline.addLast("handler", handler);
            }
        });

    }

    @Override
    public Connection createConnection(Url url, int connectTimeout) throws ConnectionException {
        Channel channel = doCreateChannel(url.getIp(), url.getPort(), connectTimeout);
        Connection connection = new Connection(channel, protocol,url);
        return connection;
    }

    /**
     * 创建通道
     * @param targetIp
     * @param targetPort
     * @param connectTimeout
     * @return
     * @throws ConnectionException
     */
    private Channel doCreateChannel(String targetIp, int targetPort, int connectTimeout) throws ConnectionException{
        connectTimeout = Math.max(connectTimeout, 1000);
        String address = targetIp + ":" + targetPort;
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);

        ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetIp, targetPort));
        future.awaitUninterruptibly();

        if (!future.isDone()) {
            logger.error("create connection to " + address +" timeout");
            throw new ConnectionException("create connection to " + address +" timeout");
        }

        if (future.isCancelled()) {
            logger.error("create connection to " + address + " cancelled by user");
            throw new ConnectionException("create connection to " + address + " cancelled by user");
        }

        if (!future.isSuccess()) {
            logger.error("create connection to " + address + " error");
            throw new ConnectionException("create connection to " + address + " error", future.cause());
        }

        return future.channel();
    }

    private void initWriteBufferWaterMark() {
        int lowWaterMark = configManager.getValue(GenericOption.NETTY_BUFFER_LOW_WATERMARK);
        int highWaterMark = configManager.getValue(GenericOption.NETTY_BUFFER_HIGH_WATERMARK);
        if (lowWaterMark > highWaterMark) {
            throw new IllegalArgumentException(String.format("lowWaterMark should smaller than highWaterMark"));
        }
        this.bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(lowWaterMark, highWaterMark));
    }
}
