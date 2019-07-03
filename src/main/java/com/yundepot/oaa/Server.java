package com.yundepot.oaa;

import com.yundepot.oaa.common.LifeCycle;
import com.yundepot.oaa.config.Configurable;
import com.yundepot.oaa.protocol.ProtocolManager;

/**
 * @author zhaiyanan
 * @date 2019/5/21 17:02
 */
public interface Server extends Configurable, LifeCycle {

    /**
     * 服务的ip
     * @return
     */
    String ip();

    /**
     * 服务的端口
     * @return
     */
    int port();

    /**
     * 获取协议管理器
     * @return
     */
    ProtocolManager getProtocolManager();
}
