package com.fable.framework.proxy;

import com.fable.framework.proxy.util.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/8/30
 * @since 1.3.0
 */
@Slf4j
public class DefaultUdpClient extends DefaultBootstrapManager implements Client {

    public DefaultUdpClient() {
    }

    public DefaultUdpClient(ChannelManager channelManager) {
        super(channelManager);
    }

    @Override
    protected Bootstrap createBootstrap(EventLoopGroup workerGroup) {
        return new Bootstrap().group(workerGroup)
                .channel(NettyUtils.getUdpChannelClass())
                .option(ChannelOption.SO_BROADCAST, false)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(SHARED_CHANNEL_HANDLER);
    }
}
