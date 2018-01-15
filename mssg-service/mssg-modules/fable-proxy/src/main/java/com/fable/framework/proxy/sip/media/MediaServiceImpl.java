package com.fable.framework.proxy.sip.media;

import com.fable.framework.proxy.*;
import com.fable.framework.proxy.sip.Address;
import com.fable.framework.proxy.sip.session.RSnapshot;
import com.fable.framework.proxy.sip.session.ReSnapshot;
import com.fable.framework.proxy.sip.session.Setup;
import com.fable.framework.proxy.sip.session.SipSessionKeeper;
import com.fable.framework.proxy.socks.SocksServerHandler;
import com.fable.framework.proxy.util.NettyUtils;
import com.fable.framework.proxy.util.PortUtils;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * .
 *
 * @author stormning 2017/11/9
 * @since 1.3.0
 */
public class MediaServiceImpl implements MediaService, InitializingBean, DisposableBean {

    private DefaultUdpServer udpServer;

    @Setter
    private CacheManager cacheManager;

    private Cache listenAndRemotePortCache;
    private Cache dsCodeAndMediaportCache;
    private Cache dsCodeCountCache;

    @Setter
    private String backwardHostname;

    @Setter
    private String backwardClientHostname;

    @Setter
    private String forwardHostname;

    @Setter
    private String forwardClientHostname;

    @Setter
    private int idleSeconds = 100;

    @Setter
    private ChannelManager channelManager;

    @Setter
    private InetSocketAddress proxyAddress;

    @Setter
    @Getter
    private List<MessageFilter<DatagramPacket>> udpMessageFilters = Lists.newArrayList();


    private EventLoopGroup workerGroup = NettyUtils.createEventLoopGroup(0);

    private EventLoopGroup bossGroup = NettyUtils.createEventLoopGroup(0);

    @Override
    public void createMediaChannels(SipSessionKeeper session) {
        RSnapshot rs = session.getRequestSnapshot();
        if (rs.getSetup() == null) {
            //create udp channel
            createUdpChannel(session);
        } else {
            if (rs.isNewConnection()) {
                createTcpChannel(session);
            } else {
                //原路返回
            }
        }
    }

    @Override
    public void addMediaChannels(String ip, int port, int listenPort) {
        List<InetSocketAddress> remotePort = (List<InetSocketAddress>) listenAndRemotePortCache.get(port);
        remotePort.add(new InetSocketAddress(ip, port));
        listenAndRemotePortCache.putIfAbsent(listenPort, remotePort);
    }

    public void createUdpChannel(SipSessionKeeper session) {
        RSnapshot rs = session.getRequestSnapshot();
        ReSnapshot res = session.getResponseSnapshot();

        Integer mediaReceiverPort = rs.getMediaPort().getOutput();
        int mediaReceiverRtcpPort = mediaReceiverPort + 1;
        int mediaSenderRtcpPort = res.getMediaPort().getInput() + 1;
        int mediaSendRtpPort = res.getMediaPort().getInput();
        //rtp server->client
        Channel sToCRtpChannel = bindUdpChannel(
                //listen
                new InetSocketAddress(backwardHostname, rs.getMediaPort().getOutput()),
                //client
                new InetSocketAddress(backwardClientHostname, res.getMediaPort().getOutput()),
                //remote
                new InetSocketAddress(rs.getConnectionHost().getInput(), rs.getMediaPort().getInput()),
                true
        );
        sToCRtpChannel.attr(AttributeKey.valueOf(String.valueOf(rs.getMediaPort().getOutput())));
        //rtcp two way
        //rtcp receiver
        bindUdpChannel(
                //listen
                new InetSocketAddress(backwardHostname, rs.getMediaPort().getOutput() + 1),
                //input
                new InetSocketAddress(backwardClientHostname, res.getMediaPort().getOutput() + 1),
                //remote
                new InetSocketAddress(rs.getConnectionHost().getInput(), rs.getMediaPort().getInput() + 1),
                false
        );
        //rtcp sender
        Channel cToSRtcpChannel = bindUdpChannel(
                //listen
                new InetSocketAddress(forwardHostname, res.getMediaPort().getOutput() + 1),
                //input
                new InetSocketAddress(forwardClientHostname, rs.getMediaPort().getOutput() + 1),
                //remote
                new InetSocketAddress(res.getConnectionHost().getInput(), res.getMediaPort().getInput() + 1),
                false
        );
        cToSRtcpChannel.attr(AttributeKey.valueOf(String.valueOf(res.getMediaPort().getOutput() + 1))).set(rs.getCallId().getOutput());
        listenAndRemotePortCache.putIfAbsent(rs.getMediaPort().getOutput(),
                Lists.newArrayList(new InetSocketAddress(rs.getConnectionHost().getInput(), rs.getMediaPort().getInput())));
        listenAndRemotePortCache.putIfAbsent(res.getMediaPort().getOutput() + 1,
                Lists.newArrayList(new InetSocketAddress(res.getConnectionHost().getInput(), res.getMediaPort().getInput() + 1)));
        listenAndRemotePortCache.putIfAbsent(rs.getMediaPort().getOutput() + 1,
                Lists.newArrayList(new InetSocketAddress(rs.getConnectionHost().getInput(), rs.getMediaPort().getInput() + 1)));
    }


    @SneakyThrows
    private Channel bindUdpChannel(InetSocketAddress listen, InetSocketAddress client, InetSocketAddress remote, Boolean isRtpFilter) {
        UdpProxyHandler udpProxyHandler = new UdpProxyHandler(channelManager);
        //sender
        udpProxyHandler.setClientAddress(client);
        //input address
        udpProxyHandler.setRemoteAddress(remote);
        udpProxyHandler.setRtpOrRtcp(true);
        udpProxyHandler.setCacheManager(cacheManager);
        if (isRtpFilter) {
            udpProxyHandler.setMessageFilters(udpMessageFilters);
        }
        ChannelFuture future = udpServer.bind(listen, udpProxyHandler).syncUninterruptibly();
        return future.channel();
    }


    public void createTcpChannel(SipSessionKeeper session) {
        RSnapshot rs = session.getRequestSnapshot();
        Setup setup = rs.getSetup();
        ReSnapshot res = session.getResponseSnapshot();

        Integer mediaReceiverPort = rs.getMediaPort().getOutput();
        Integer mediaSenderPort = res.getMediaPort().getOutput();

        if (setup == Setup.active) {
            //RTP
            bindTcpChannel(
                    //listen
                    new InetSocketAddress(forwardHostname, mediaSenderPort),
                    //client
                    new InetSocketAddress(forwardClientHostname, mediaReceiverPort),
                    //remote
                    new InetSocketAddress(res.getConnectionHost().getInput(), res.getMediaPort().getInput())
            );
        } else {
            //RTP
            bindTcpChannel(
                    //listen
                    new InetSocketAddress(backwardHostname, mediaReceiverPort),
                    //input
                    new InetSocketAddress(backwardHostname, mediaSenderPort),
                    //remote
                    new InetSocketAddress(rs.getConnectionHost().getInput(), rs.getMediaPort().getInput())
            );
        }
    }

    @Override
    public Address acquire(boolean request) {
        return new Address(request ? backwardHostname : forwardHostname, PortUtils.randomPorts(2)[0]);
    }

    private void bindTcpChannel(InetSocketAddress listen, InetSocketAddress client, InetSocketAddress remote) {
        HexDumpProxyFrontendHandler frontendHandler = new HexDumpProxyFrontendHandler(client, remote);
        frontendHandler.setProxyAddress(proxyAddress);
        DefaultTcpServer tcpServer = createTcpServer(frontendHandler);
        tcpServer.bind(listen).syncUninterruptibly();
    }

    @SneakyThrows
    private DefaultTcpServer createTcpServer(HexDumpProxyFrontendHandler handler) {
        DefaultTcpServer tcpServer = new DefaultTcpServer(channelManager) {
            @Override
            protected void customizeChannelPipeline(ChannelPipeline pipeline) {
                pipeline.addLast(handler);
            }
        };
        tcpServer.setBossGroup(bossGroup);
        tcpServer.setWorkerGroup(workerGroup);
        tcpServer.setIdleSeconds(idleSeconds);
        tcpServer.setAutoRead(false);
        tcpServer.afterPropertiesSet();
        return tcpServer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.udpServer = new DefaultUdpServer(channelManager);
        this.udpServer.setWorkerGroup(workerGroup);
        this.udpServer.setIdleSeconds(idleSeconds);
        this.udpServer.afterPropertiesSet();
        this.listenAndRemotePortCache = cacheManager.getCache("listenandremoteport");
    }

    public void bindProxyServer(InetSocketAddress listen) {
        DefaultTcpServer tcpServer = createTcpProxyServer();
        tcpServer.bind(listen, new SocksPortUnificationServerHandler()).syncUninterruptibly();
    }

    @SneakyThrows
    private DefaultTcpServer createTcpProxyServer() {
        DefaultTcpServer tcpServer = new DefaultTcpServer(channelManager) {
            @Override
            protected void customizeChannelPipeline(ChannelPipeline pipeline) {
                pipeline.addLast(
//                        new LoggingHandler(LogLevel.ERROR),
                        new SocksPortUnificationServerHandler(),
                        SocksServerHandler.INSTANCE);
            }
        };
        tcpServer.setBossGroup(bossGroup);
        tcpServer.setWorkerGroup(workerGroup);
        tcpServer.setIdleSeconds(0);
        tcpServer.setAutoRead(true);
        tcpServer.afterPropertiesSet();
        return tcpServer;
    }

    @Override
    public void destroy() throws Exception {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
