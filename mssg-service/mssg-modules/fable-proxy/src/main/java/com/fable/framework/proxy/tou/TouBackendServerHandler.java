package com.fable.framework.proxy.tou;

import com.fable.framework.proxy.DefaultTcpClient;
import com.fable.framework.proxy.DefaultUdpClient;
import com.fable.framework.proxy.HexDumpProxyBackendHandler;
import com.fable.framework.proxy.HexDumpProxyFrontendHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultAddressedEnvelope;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/12/4
 * @since 1.3.0
 */
@Slf4j
public class TouBackendServerHandler extends HexDumpProxyFrontendHandler {

    private final InetSocketAddress backwardClient;
    private final InetSocketAddress backwardRemote;

    public TouBackendServerHandler(
            InetSocketAddress clientAddress,
            InetSocketAddress remoteAddress,
            InetSocketAddress backwardClient,
            InetSocketAddress backwardRemote
    ) {
        super(clientAddress, remoteAddress);
        this.backwardClient = backwardClient;
        this.backwardRemote = backwardRemote;
    }

    @Override
    protected ChannelFuture getOutboundChannel(Channel inboundChannel) {

        ChannelFuture backwardClientFuture = new DefaultUdpClient().bind(backwardClient);

        DefaultTcpClient tcpClient = new DefaultTcpClient() {
            @Override
            protected void customizeChannelPipeline(ChannelPipeline pipeline) {
                pipeline.addLast(new HexDumpProxyBackendHandler(backwardClientFuture.channel()) {
                    @Override
                    protected ChannelFuture writeAndFlush(Channel channel, Object msg) {
                        return channel.writeAndFlush(new DefaultAddressedEnvelope<>(msg, backwardRemote));
                    }
                });
            }
        };
        tcpClient.setAutoRead(false);
        return tcpClient.getBootstrap().connect(remoteAddress, clientAddress);
    }
}
