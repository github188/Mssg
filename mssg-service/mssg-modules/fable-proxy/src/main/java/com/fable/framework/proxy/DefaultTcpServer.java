package com.fable.framework.proxy;

import com.fable.framework.proxy.util.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/8/28
 * @since 1.3.0
 */
@Slf4j
public class DefaultTcpServer extends AbstractServer<ServerBootstrap, ServerChannel> {

    private static final int DEFAULT_IDLE_SECONDS = 20;
    @Setter
    private EventLoopGroup bossGroup;
    @Setter
    private int bossCount = 0;

    @Setter
    private boolean autoRead = true;

    public DefaultTcpServer() {
    }

    public DefaultTcpServer(ChannelManager channelManager) {
        super(channelManager);
    }

    @Override
    protected ServerBootstrap createBootstrap(EventLoopGroup workerGroup) {
        this.bossGroup = bossGroup == null ? NettyUtils.createEventLoopGroup(bossCount) : bossGroup;
        if (getIdleSeconds() < 0) {
            setIdleSeconds(DEFAULT_IDLE_SECONDS);
        }
        return new ServerBootstrap().group(bossGroup, workerGroup)
                .channel(NettyUtils.getTcpServerChannelClass())
//                .handler(new LoggingHandler(LogLevel.ERROR))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.AUTO_READ, autoRead)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT)
                .childHandler(SHARED_CHANNEL_HANDLER);
    }

    @Override
    @SneakyThrows
    public void destroy() {
        super.destroy();
        bossGroup.shutdownGracefully().syncUninterruptibly();
    }
}
