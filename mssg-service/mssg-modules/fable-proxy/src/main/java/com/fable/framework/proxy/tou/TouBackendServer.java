package com.fable.framework.proxy.tou;

import com.fable.framework.proxy.DefaultUdpServer;
import io.netty.channel.ChannelPipeline;

import java.net.InetSocketAddress;

/**
 * tcp over udp backend udp server
 * udp server listen -- > tcp client send
 * --> use tcp client read --> udp client send.
 *
 * @author stormning 2017/12/4
 * @since 1.3.0
 */
public class TouBackendServer extends DefaultUdpServer {
    private final InetSocketAddress clientAddress;
    private final InetSocketAddress remoteAddress;
    private final InetSocketAddress backwardClient;
    private final InetSocketAddress backwardRemote;

    //udp server listen -- > tcp client send
    //--> use tcp client read --> udp client send

    public TouBackendServer(
            InetSocketAddress clientAddress,
            InetSocketAddress remoteAddress,
            InetSocketAddress backwardClient,
            InetSocketAddress backwardRemote
    ) {
        this.clientAddress = clientAddress;
        this.remoteAddress = remoteAddress;
        this.backwardClient = backwardClient;
        this.backwardRemote = backwardRemote;
    }

    @Override
    protected void customizeChannelPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new TouBackendServerHandler(clientAddress, remoteAddress, backwardClient, backwardRemote));
    }
}
