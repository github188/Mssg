package com.fable.framework.proxy.tou;

import com.fable.framework.proxy.DefaultUdpClient;
import com.fable.framework.proxy.DefaultUdpServer;
import com.fable.framework.proxy.HexDumpProxyBackendHandler;
import com.fable.framework.proxy.HexDumpProxyFrontendHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.DefaultAddressedEnvelope;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * .
 *
 * @author stormning 2017/12/4
 * @since 1.3.0
 */
@Slf4j
public class TouFrontendServerHandler extends HexDumpProxyFrontendHandler {
    private InetSocketAddress backwardServer;

    @Setter
    private List<ChannelHandler> customHandlers;

    public TouFrontendServerHandler(
            InetSocketAddress clientAddress,
            InetSocketAddress remoteAddress,
            InetSocketAddress backwardServer
    ) {
        super(clientAddress, remoteAddress);
        this.backwardServer = backwardServer;

    }

    @Override
    protected ChannelFuture getOutboundChannel(Channel inboundChannel) {
        //Start create a udp client
        DefaultUdpClient udpClient = new DefaultUdpClient();
        udpClient.setWorkerGroup(inboundChannel.eventLoop());
        ChannelFuture future = udpClient.bind(
                clientAddress,
                customHandlers == null ? null : customHandlers.toArray(new ChannelHandler[customHandlers.size()])
        );
        DefaultUdpServer udpServer = new DefaultUdpServer();
        udpServer.setIdleSeconds(1000);
        udpServer.setWorkerGroup(inboundChannel.parent().eventLoop());
        udpServer.bind(backwardServer, new HexDumpProxyBackendHandler(inboundChannel));
        return future;
    }

    @Override
    protected ChannelFuture writeAndFlush(Channel outboundChannel, InetSocketAddress remote, Object msg) {
        return outboundChannel.writeAndFlush(new DefaultAddressedEnvelope<>(msg, remoteAddress));
    }

}
