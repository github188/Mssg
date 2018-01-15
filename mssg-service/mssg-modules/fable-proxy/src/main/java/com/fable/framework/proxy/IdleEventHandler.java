package com.fable.framework.proxy;

import com.fable.framework.core.ApplicationContextHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: yuhl Created on 11:15 2017/9/18 0018
 */
@ChannelHandler.Sharable
@Slf4j
public class IdleEventHandler extends ChannelInboundHandlerAdapter {

    public static IdleEventHandler INSTANCE = new IdleEventHandler();

    private IdleEventHandler() {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            ApplicationContextHolder.get().publishEvent(channel);
            log.info("Close idled channel {} ", channel);
            channel.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
