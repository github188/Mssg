package com.fable.framework.proxy;

import com.fable.framework.proxy.util.NettyUtils;
import com.fable.framework.proxy.util.PortUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.proxy.Socks5ProxyHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/23
 * @since 1.3.0
 */
@Slf4j
public class HexDumpProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    protected InetSocketAddress clientAddress;

    protected InetSocketAddress remoteAddress;

    @Setter
    private InetSocketAddress proxyAddress;

    // As we use inboundChannel.eventLoop() when building the Bootstrap this does not need to be volatile as
    // the outboundChannel will use the same EventLoop (and therefore Thread) as the inboundChannel.
    protected Channel outboundChannel;

    public HexDumpProxyFrontendHandler(InetSocketAddress clientAddress, InetSocketAddress remoteAddress) {
        this.clientAddress = clientAddress;
        this.remoteAddress = remoteAddress;
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel inboundChannel = ctx.channel();
        // Start the connection attempt.
        ChannelFuture channelFuture = getOutboundChannel(inboundChannel).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    log.error("{}", future.cause());
                    inboundChannel.close();
                }
            }
        });
        this.outboundChannel = channelFuture.channel();
    }

    protected ChannelFuture getOutboundChannel(final Channel inboundChannel) {
        Bootstrap b = new Bootstrap();
        b.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
        b.group(inboundChannel.eventLoop())
                .channel(NettyUtils.getTcpClientChannelClass())
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        if (proxyAddress != null) {
                            Socks5ProxyHandler socks5ProxyHandler = new Socks5ProxyHandler(proxyAddress);
                            ch.pipeline().addFirst(socks5ProxyHandler);
                        }
                        ch.pipeline().addLast(new HexDumpProxyBackendHandler(inboundChannel));
                    }
                });
        return b.connect(remoteAddress, clientAddress).addListener(f -> PortUtils.usePorts(clientAddress.getPort()));
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel.isActive() && outboundChannel.isWritable()) {
            writeAndFlush(outboundChannel, remoteAddress, msg).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        // was able to flush out data, start to read the next chunk
                        ctx.channel().read();
                    } else {
                        log.error("{}", future.cause());
                        future.channel().close();
                    }
                }
            });
        }
    }

    protected ChannelFuture writeAndFlush(Channel outboundChannel, InetSocketAddress remote, Object msg) {
        return outboundChannel.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }
}
