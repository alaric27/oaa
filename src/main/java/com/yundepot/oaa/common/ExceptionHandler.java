package com.yundepot.oaa.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaiyanan
 * @date 2019/6/25 19:54
 */
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 解码异常的时候由于还没有解码到具体的协议及请求id，直接关闭连接
        if (cause instanceof DecoderException) {
            logger.info("DecoderException channel closed");
            ctx.close();
        } else {
            logger.info("ignore Unknown Exception");
        }
    }
}
