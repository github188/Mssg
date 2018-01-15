package com.fable.framework.proxy;

import io.netty.channel.ChannelHandlerContext;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
public interface MessageFilter<M> {
    boolean doFilter(ChannelHandlerContext ctx, M msg);
}
