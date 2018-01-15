package com.fable.framework.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/11/6
 * @since 1.3.0
 */
@Slf4j
public class DownstreamHandler extends SimpleChannelInboundHandler {

    private Channel inboundChannel;

    public DownstreamHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ReferenceCountUtil.retain(msg);
        inboundChannel.writeAndFlush(msg).addListener(f -> {
            if (!f.isSuccess()) {
                log.error("write msg error", f.cause());
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        releaseChannel(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught when handing send message to upstream", cause);
        releaseChannel(ctx);
    }

    private void releaseChannel(ChannelHandlerContext ctx) {
        log.info("Release channel {}", ctx.channel());
        ctx.channel().close();
        inboundChannel.close();
    }
}
