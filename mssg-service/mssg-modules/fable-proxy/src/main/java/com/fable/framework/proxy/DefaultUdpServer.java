package com.fable.framework.proxy;

import com.fable.framework.proxy.util.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/8/28
 * @since 1.3.0
 */
@Slf4j
public class DefaultUdpServer extends AbstractServer<Bootstrap, Channel> {

    public DefaultUdpServer() {
    }

    public DefaultUdpServer(ChannelManager channelManager) {
        super(channelManager);
    }

    @Override
    protected Bootstrap createBootstrap(EventLoopGroup workerGroup) {
        return new Bootstrap().group(workerGroup)
                .channel(NettyUtils.getUdpChannelClass())
                .option(ChannelOption.SO_BROADCAST, false)
                .option(ChannelOption.SO_REUSEADDR, true)
                // TODO: need config ? 60M
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024 * 60)
                .handler(SHARED_CHANNEL_HANDLER);
    }
}
