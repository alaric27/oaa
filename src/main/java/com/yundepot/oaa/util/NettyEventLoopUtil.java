package com.yundepot.oaa.util;

import com.yundepot.oaa.config.GenericOption;
import com.yundepot.oaa.config.GlobalConfigManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ThreadFactory;

/**
 * @author zhaiyanan
 * @date 2019/6/12 15:54
 */
public class NettyEventLoopUtil {

    private static boolean epollEnabled = GlobalConfigManager.getValue(GenericOption.NETTY_EPOLL_SWITCH) && Epoll.isAvailable();

    public static EventLoopGroup newEventLoopGroup(int nThreads, ThreadFactory threadFactory) {
        return epollEnabled ? new EpollEventLoopGroup(nThreads, threadFactory) : new NioEventLoopGroup(nThreads, threadFactory);
    }


    public static Class<? extends SocketChannel> getClientSocketChannelClass() {
        return epollEnabled ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    /**
     * 根据是否支持epoll 返回EpollServerSocketChannel或者NioServerSocketChannel
     * @return
     */
    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return epollEnabled ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static void enableTriggeredMode(ServerBootstrap serverBootstrap) {
        if (epollEnabled) {
            if (GlobalConfigManager.getValue(GenericOption.NETTY_EPOLL_LT)) {
                serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.LEVEL_TRIGGERED);
            } else {
                serverBootstrap.childOption(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
            }
        }
    }
}
