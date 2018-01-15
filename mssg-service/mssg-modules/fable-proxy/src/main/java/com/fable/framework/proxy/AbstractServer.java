package com.fable.framework.proxy;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/9/18
 * @since 1.3.0
 */
@Slf4j
public abstract class AbstractServer<B extends AbstractBootstrap<B, C>, C extends Channel>
        extends DefaultBootstrapManager<B, C> implements Server {

    public AbstractServer() {
    }

    public AbstractServer(ChannelManager channelManager) {
        super(channelManager);
    }
}