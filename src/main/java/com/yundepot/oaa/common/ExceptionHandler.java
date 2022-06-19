package com.yundepot.oaa.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhaiyanan
 * @date 2019/6/25 19:54
 */
@Slf4j
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 解码异常的时候由于还没有解码到具体的协议及请求id，直接关闭连接
        if (cause instanceof DecoderException) {
            log.error("DecoderException channel closed", cause);
            ctx.close();
        } else {
            log.error("ignore Unknown Exception", cause);
        }
    }
}
