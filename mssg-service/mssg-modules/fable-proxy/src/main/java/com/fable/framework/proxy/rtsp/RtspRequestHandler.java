package com.fable.framework.proxy.rtsp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO.
 *
 * @author stormning 2017/10/23
 * @since 1.3.0
 */
@Slf4j
public class RtspRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof DefaultFullHttpRequest) {
            DefaultFullHttpRequest request = (DefaultFullHttpRequest) msg;
            log.debug("newProp request ========>\n{}\n\n{}", request.toString(), request.content().toString(CharsetUtil.UTF_8));
        }
    }
}
