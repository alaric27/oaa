package com.yundepot.oaa.util;

import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author zhaiyanan
 * @date 2019/5/21 17:02
 */
public class RemotingUtil {

    /**
     * 从Channel中获取远程地址
     * @param channel
     * @return
     */
    public static String parseRemoteAddress(final Channel channel) {
        if (null == channel) {
            return StringUtils.EMPTY;
        }
        final SocketAddress remote = channel.remoteAddress();
        return doParse(remote != null ? remote.toString().trim() : StringUtils.EMPTY);
    }

    /**
     * 从Channel中获取本地地址
     * @param channel
     * @return
     */
    public static String parseLocalAddress(final Channel channel) {
        if (null == channel) {
            return StringUtils.EMPTY;
        }
        final SocketAddress local = channel.localAddress();
        return doParse(local != null ? local.toString().trim() : StringUtils.EMPTY);
    }

    /**
     * 从Channel中获取远程ip
     * @param channel
     * @return
     */
    public static String parseRemoteIP(final Channel channel) {
        if (null == channel) {
            return StringUtils.EMPTY;
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getAddress().getHostAddress();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 从Channel中获取远程hostname
     * @param channel
     * @return
     */
    public static String parseRemoteHostName(final Channel channel) {
        if (null == channel) {
            return StringUtils.EMPTY;
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getAddress().getHostName();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 从Channel获取本地ip
     * @param channel
     * @return
     */
    public static String parseLocalIP(final Channel channel) {
        if (null == channel) {
            return StringUtils.EMPTY;
        }
        final InetSocketAddress local = (InetSocketAddress) channel.localAddress();
        if (local != null) {
            return local.getAddress().getHostAddress();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 从Channel获取远程端口
     * @param channel
     * @return
     */
    public static int parseRemotePort(final Channel channel) {
        if (null == channel) {
            return -1;
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getPort();
        }
        return -1;
    }

    /**
     * 从Channel中获取本地端口
     * @param channel
     * @return
     */
    public static int parseLocalPort(final Channel channel) {
        if (null == channel) {
            return -1;
        }
        final InetSocketAddress local = (InetSocketAddress) channel.localAddress();
        if (local != null) {
            return local.getPort();
        }
        return -1;
    }

    /**
     * 从SocketAddress中解析出地址
     * @param socketAddress
     * @return
     */
    public static String parseSocketAddressToString(SocketAddress socketAddress) {
        if (socketAddress != null) {
            return doParse(socketAddress.toString().trim());
        }
        return StringUtils.EMPTY;
    }

    /**
     * 从SocketAddress中解析出本地Ip
     * @param socketAddress
     * @return
     */
    public static String parseSocketAddressToHostIp(SocketAddress socketAddress) {
        final InetSocketAddress addrs = (InetSocketAddress) socketAddress;
        if (addrs != null) {
            InetAddress addr = addrs.getAddress();
            if (null != addr) {
                return addr.getHostAddress();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 解析地址
     * @param addr
     * @return
     */
    private static String doParse(String addr) {
        if (StringUtils.isBlank(addr)) {
            return StringUtils.EMPTY;
        }
        if (addr.charAt(0) == '/') {
            return addr.substring(1);
        } else {
            int len = addr.length();
            for (int i = 1; i < len; ++i) {
                if (addr.charAt(i) == '/') {
                    return addr.substring(i + 1);
                }
            }
            return addr;
        }
    }
}
