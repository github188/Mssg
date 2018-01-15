package com.fable.framework.proxy;

import com.fable.framework.proxy.util.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/11/25
 * @since 1.3.0
 */
@Slf4j
public class DefaultTcpClient extends DefaultBootstrapManager<Bootstrap, Channel> implements Client {

    @Setter
    private boolean autoRead = true;

    @Override
    protected Bootstrap createBootstrap(EventLoopGroup workerGroup) {
        return new Bootstrap()
                .group(NettyUtils.createEventLoopGroup(0))
                .channel(NettyUtils.getTcpClientChannelClass())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.AUTO_READ, autoRead);

    }
}
