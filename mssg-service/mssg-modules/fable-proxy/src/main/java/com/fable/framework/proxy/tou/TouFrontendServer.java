package com.fable.framework.proxy.tou;

import com.fable.framework.proxy.DefaultTcpServer;
import io.netty.channel.ChannelPipeline;

import java.net.InetSocketAddress;

/**
 * tcp over udp frontend tcp server
 * tcp server listen -- > udp client send
 * --> use udp server listen --> tcp client send back to tcp channel
 *
 * @author stormning 2017/12/4
 * @since 1.3.0
 */
public class TouFrontendServer extends DefaultTcpServer {

    private final InetSocketAddress clientAddress;
    private final InetSocketAddress remoteAddress;
    private final InetSocketAddress backwardServer;

    //tcp server listen -- > udp client send
    //--> use udp server listen --> tcp client send back to tcp channel

    public TouFrontendServer(
            InetSocketAddress clientAddress,
            InetSocketAddress remoteAddress,
            InetSocketAddress backwardServer
    ) {
        this.clientAddress = clientAddress;
        this.remoteAddress = remoteAddress;
        this.backwardServer = backwardServer;
    }

    @Override
    protected void customizeChannelPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new TouFrontendServerHandler(clientAddress, remoteAddress, backwardServer));
    }
}
