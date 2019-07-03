package com.yundepot.oaa.util;

import com.yundepot.oaa.connection.Connection;
import io.netty.channel.Channel;

import java.util.Optional;

/**
 * @author zhaiyanan
 * @date 2019/6/10 18:06
 */
public class ConnectionUtil {

    /**
     * 从Channel获取连接
     * @param channel
     * @return
     */
    public static Connection getConnectionFromChannel(Channel channel) {
        return Optional.ofNullable(channel)
                .map(ch -> ch.attr(Connection.CONNECTION).get())
                .orElse(null);
    }
}
