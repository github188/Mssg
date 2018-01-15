package com.fable.framework.proxy;

import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

/**
 * TODO.
 *
 * @author stormning 2017/11/3
 * @since 1.3.0
 */
@Slf4j
public class ChannelManagerImpl implements ChannelManager, InitializingBean {

    private static final Map<SocketAddress, ChannelFuture> UDP_CHANNELS = Maps.newConcurrentMap();

    private static final Map<String, ChannelFuture> TCP_CHANNELS = Maps.newConcurrentMap();

    private DefaultUdpClient udpClient;

    @Override
    public void store(ChannelFuture channel) {
        if (channel.isDone()) {
            UDP_CHANNELS.putIfAbsent(channel.channel().localAddress(), channel);
        } else {
            channel.addListener(
                    (ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            UDP_CHANNELS.putIfAbsent(channel.channel().localAddress(), channel);
                        } else {
                            log.error("channel future {} error", future, future.cause());
                        }
                    }
            );
        }
    }

    @Override
    public ChannelFuture selectTcpChannel(
            ChannelHandlerContext ctx,
            ClientListener clientListener,
            InetSocketAddress local,
            InetSocketAddress remote
    ) {

        Channel inboundChannel = ctx.channel();
        Bootstrap bootstrap = new Bootstrap()
                .group(inboundChannel.eventLoop())
                .channel(inboundChannel.getClass())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true);

        if (clientListener != null) {
            clientListener.onBootstrap(ctx, bootstrap);
        }
        log.info("Prepare to connect L:{} R:{}", local, remote);
        return bootstrap.connect(remote).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close();
                    log.error("Create new tcp connection from {} to {} error", local, remote, future.cause());
                }
            }
        });
    }

    @Override
    @SneakyThrows
    public ChannelFuture selectUdpChannel(
            ChannelHandlerContext ctx,
            InetSocketAddress local,
            ChannelHandler[] clientEncoders
    ) {
        ChannelFuture future = UDP_CHANNELS.get(local);
        if (future == null) {
            future = udpClient.bind(local, clientEncoders);
        }
        return future;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.udpClient = new DefaultUdpClient(this);
        udpClient.afterPropertiesSet();
    }

}
