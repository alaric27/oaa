package com.yundepot.oaa.invoke;

import com.yundepot.oaa.protocol.command.Command;

/**
 * @author zhaiyanan
 * @date 2019/5/21 15:40
 */
public interface InvokeCallback {

    /**
     * 响应到达时执行
     * @param response
     */
    void onResponse(Command response);
}
