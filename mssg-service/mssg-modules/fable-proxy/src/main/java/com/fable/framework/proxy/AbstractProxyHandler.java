package com.fable.framework.proxy;

import com.fable.framework.proxy.sip.session.SessionAttributeHolder;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 抽象代理服务器处理过程.
 *
 * @author stormning 2017/11/3
 * @since 1.3.0
 */
@Slf4j
public abstract class AbstractProxyHandler<H, M, O> extends SimpleChannelInboundHandler<M> {

    @Setter
    protected ClientListener clientListener;
    @Setter
    @Getter
    private InetSocketAddress remoteAddress;
    @Setter
    @Getter
    private InetSocketAddress clientAddress;
    @Setter
    private ChannelManager channelManager;

    @Setter
    private boolean isRtpOrRtcp = false;

    @Setter
    private CacheManager cacheManager;

    @Setter
    @Getter
    private InetSocketAddress backServerAddress;

    @Setter
    @Getter
    private List<MessageFilter<M>> messageFilters;

    private ChannelHandler[] clientHandlers;

    public AbstractProxyHandler(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel {} active", ctx.channel());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext ctx, M msg) throws Exception {
        try {
            //message filter
            if (messageFilters != null) {
                for (MessageFilter<M> filter : messageFilters) {
                    if (!filter.doFilter(ctx, msg)) {
                        return;
                    }
                }
            }
            //handle message
            H handled = handle(ctx, msg);
            if (handled != null) {
                InetSocketAddress local = getLocalAddress(ctx, handled);
                InetSocketAddress remote = getRemoteAddress(ctx, handled);
                assert remote != null;
                boolean isUdp = ctx.channel() instanceof DatagramChannel;
                O toSend = getOutboundMessage(handled);
                Object envelope;
                ChannelFuture channelToWrite;
                if (isUdp) {
                    if (isRtpOrRtcp) {
                        Cache cache = cacheManager.getCache("listenandremoteport");
                        Cache.ValueWrapper wrapper = cache.get(((InetSocketAddress) ctx.channel().localAddress()).getPort());
                        Object object = wrapper.get();
                        List<InetSocketAddress> remoteAddressList = (List<InetSocketAddress>) object;
                        for (InetSocketAddress remoteAddress : remoteAddressList) {
                            envelope = new DefaultAddressedEnvelope(toSend, remoteAddress);
                            channelToWrite = channelManager.selectUdpChannel(ctx, local, clientHandlers);
                            writeToRemoteAddress(channelToWrite, envelope, ctx);
                            System.out.println(remoteAddress.getHostString() + ":" + remoteAddress.getPort());
                        }
                    } else {
                        envelope = new DefaultAddressedEnvelope(toSend, remote);
                        channelToWrite = channelManager.selectUdpChannel(ctx, local, clientHandlers);
                        writeToRemoteAddress(channelToWrite, envelope, ctx);
                    }

                } else {
                    envelope = toSend;
                    channelToWrite = channelManager.selectTcpChannel(ctx, clientListener, local, remote);
                    writeToRemoteAddress(channelToWrite, envelope, ctx);
                }
//                log.info("Channel to write {}", channelToWrite);
            }
        } finally {
            SessionAttributeHolder.resetSessionAttributes();
        }
    }

    private void writeToRemoteAddress(ChannelFuture channelToWrite, Object envelope, ChannelHandlerContext ctx) {
        if (channelToWrite.isDone()) {
            writeInFuture(ctx, channelToWrite, envelope);
        } else {
            channelToWrite.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    writeInFuture(ctx, future, envelope);
                } else {
                    log.error("Get channel {} error", future.cause());
                }
            });
        }
    }

    private void writeInFuture(ChannelHandlerContext ctx, ChannelFuture future, Object toSend) {
        Channel channel = future.channel();
//        log.info("channel is {}", channel);
//        log.info("channel is open : {}", channel.isOpen());
//        log.info("channel is active : {}", channel.isActive());
//        log.info("channel is writable : {}", channel.isWritable());
        channel.writeAndFlush(toSend).addListener((ChannelFutureListener) f -> {
            if (f.isSuccess()) {
                // was able to flush out data, start to read the next chunk
//                if (!isUDP) ctx.channel().read();
//                future.channel().read();
            } else {
                log.error("write error", f.cause());
                future.channel().close();
            }
        });
    }

    protected InetSocketAddress getLocalAddress(ChannelHandlerContext ctx, H msg) {
        return clientAddress == null ? (InetSocketAddress) ctx.channel().localAddress() : clientAddress;
    }

    protected abstract H handle(ChannelHandlerContext ctx, M msg);

    protected abstract O getOutboundMessage(H handled);

    protected InetSocketAddress getRemoteAddress(ChannelHandlerContext ctx, H handled) {
        return remoteAddress == null ? (InetSocketAddress) ctx.channel().remoteAddress() : remoteAddress;
    }

    public void setClientHandlers(ChannelHandler... clientHandlers) {
        this.clientHandlers = clientHandlers;
    }
}