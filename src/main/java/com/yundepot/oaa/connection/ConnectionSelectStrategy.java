package com.yundepot.oaa.connection;

import java.util.List;

/**
 * @author zhaiyanan
 * @date 2019/6/8 18:29
 */
public interface ConnectionSelectStrategy {

    /**
     * 选择一个连接
     * @param connectionList
     * @return
     */
    Connection select(List<Connection> connectionList);
}
