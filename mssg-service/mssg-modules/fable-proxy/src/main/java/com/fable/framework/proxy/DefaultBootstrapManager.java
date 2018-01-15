package com.fable.framework.proxy;

import com.fable.framework.proxy.util.NettyUtils;
import com.fable.framework.proxy.util.PortUtils;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

/**
 * .
 *
 * @author stormning 2017/9/27
 * @since 1.3.0
 */
@Slf4j
abstract class DefaultBootstrapManager<B extends AbstractBootstrap<B, C>, C extends Channel> implements BootstrapManager, Idable, Server {
    @Getter
    protected B bootstrap;
    @Setter
    @Getter
    protected int idleSeconds = -1;

    @Setter
    private List<ChannelHandler> bootstrapHandlers;

    protected final ChannelHandler SHARED_CHANNEL_HANDLER = new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(ExceptionHandler.INSTANCE);
            //add log handler
//            pipeline.addLast(new LoggingHandler(LogLevel.ERROR));
            if (idleSeconds > 0) {
                pipeline.addLast(new IdleStateHandler(0, 0, idleSeconds));
                pipeline.addLast(IdleEventHandler.INSTANCE);
            }
            customizeChannelPipeline(pipeline);
        }
    };
    private String id;

    @Setter
    private EventLoopGroup workerGroup;
    @Setter
    private int workerCount = 0;
    private Protocol protocol;
    @Getter
    private ChannelManager channelManager;

    public DefaultBootstrapManager() {
    }

    public DefaultBootstrapManager(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
            this.workerGroup = workerGroup == null ? NettyUtils.createEventLoopGroup(workerCount) : workerGroup;
            this.bootstrap = createBootstrap(workerGroup);
            if (!CollectionUtils.isEmpty(bootstrapHandlers)) {
                for (ChannelHandler handler : bootstrapHandlers) {
                    this.bootstrap.handler(handler);
                }
            }
            this.protocol = bootstrap instanceof ServerBootstrap ? Protocol.TCP : Protocol.UDP;
            //common options
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }
    }

    protected void customizeChannelPipeline(ChannelPipeline pipeline) {
        //override it if possible
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void destroy() throws Exception {
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    @Override
    public Protocol getProtocol() {
        return this.protocol;
    }

    @Override
    public ChannelFuture bind(InetSocketAddress address, ChannelHandler... channelHandlers) {
        ChannelFuture future = bootstrap.bind(address).addListener(f -> PortUtils.usePorts(address.getPort()));
        channelManager.store(future);
        if (channelHandlers != null && channelHandlers.length > 0) {
            future.addListener(
                    (ChannelFutureListener) future1 -> future1.channel().pipeline().addLast(channelHandlers)
            );
        }
        return future;
    }

    protected abstract B createBootstrap(EventLoopGroup workerGroup);
}
