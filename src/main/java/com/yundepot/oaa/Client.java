package com.yundepot.oaa;

import com.yundepot.oaa.common.LifeCycle;
import com.yundepot.oaa.config.Configurable;
import com.yundepot.oaa.protocol.Protocol;

/**
 * @author zhaiyanan
 * @date 2019/5/27 15:52
 */
public interface Client extends Configurable, LifeCycle {

    /**
     * 获取协议
     * @return
     */
    Protocol getProtocol();
}
