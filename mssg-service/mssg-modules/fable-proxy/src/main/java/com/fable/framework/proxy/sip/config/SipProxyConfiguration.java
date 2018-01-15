package com.fable.framework.proxy.sip.config;

import com.fable.framework.core.config.Address;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.framework.core.support.remoting.impl.ServiceRegistryAutoConfiguration;
import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.DefaultDeviceManager;
import com.fable.framework.proxy.sip.DeviceManager;
import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.handler.SipProxyHandlerAdapter;
import com.fable.framework.proxy.sip.media.MediaService;
import com.fable.framework.proxy.sip.server.SipUdpMessageServer;
import com.fable.framework.proxy.sip.session.SipSessionManager;
import com.fable.framework.proxy.sip.session.SipSessionManagerImpl;
import com.google.common.collect.Lists;
import io.pkts.packet.sip.SipMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.fable.framework.proxy.sip.util.AddressUtils.nullSafeGet;

/**
 * .
 *
 * @author stormning 2017/11/14
 * @since 1.3.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SipProxyProperties.class)
@ConditionalOnProperty(name = "fable.proxy.sip.udp-forward.server")
@AutoConfigureAfter({ServiceRegistryAutoConfiguration.class, CacheAutoConfiguration.class, CommonConfiguration.class})
public class SipProxyConfiguration implements InitializingBean {

    private final ChannelManager channelManager;
    private final CacheManager cacheManager;
    private SipProxyProperties proxyProperties;
    private ProxyInitializer proxyInitializer;
    private ServiceRegistry serviceRegistry;

    public SipProxyConfiguration(
            @Autowired(required = false) ProxyInitializer proxyInitializer,
            SipProxyProperties proxyProperties,
            ChannelManager channelManager,
            ServiceRegistry serviceRegistry,
            CacheManager cacheManager
    ) {
        this.proxyInitializer = proxyInitializer;
        this.proxyProperties = proxyProperties;
        this.channelManager = channelManager;
        this.serviceRegistry = serviceRegistry;
        this.cacheManager = cacheManager;
    }

    @Bean
    public SipUdpMessageServer sipUdpForwardServer(
            @Qualifier("sipMessageHandlers") List<SipMessageHandler<SipMessage>> messageHandlers,
            SipSessionManager sessionManager
    ) throws Exception {
        SipProxyProperties.UdpForward forward = proxyProperties.getUdpForward();
        SipUdpMessageServer sipForwardServer = new SipUdpMessageServer(channelManager);
        sipForwardServer.setProxyHandlerAdapter(forwardProxyHandlerAdapter(messageHandlers, sessionManager));

        proxyInitializer.customizeForwardServer(sipForwardServer);

        sipForwardServer.afterPropertiesSet();
        sipForwardServer.bind(forward.getServer().getInetSocketAddress());
        log.info("forwardserver绑定");
        return sipForwardServer;
    }

    @Bean
    public SipUdpMessageServer sipUdpBackwardServer(
            @Qualifier("sipMessageHandlers") List<SipMessageHandler<SipMessage>> messageHandlers,
            SipSessionManager sessionManager
    ) throws Exception {
        SipProxyProperties.UdpBackward backward = proxyProperties.getUdpBackward();
        SipUdpMessageServer sipBackwardServer = new SipUdpMessageServer(channelManager);
        sipBackwardServer.setProxyHandlerAdapter(backwardProxyHandlerAdapter(messageHandlers, sessionManager));


        proxyInitializer.customizeBackwardServer(sipBackwardServer);

        sipBackwardServer.afterPropertiesSet();
        sipBackwardServer.bind(backward.getServer().getInetSocketAddress());
        log.info("backwardserver绑定");
        return sipBackwardServer;
    }

    @Bean
    public SipProxyHandlerAdapter forwardProxyHandlerAdapter(
            @Qualifier("sipMessageHandlers") List<SipMessageHandler<SipMessage>> messageHandlers,
            SipSessionManager sessionManager
    ) {
        SipProxyHandlerAdapter adapter = buildHandlerAdapter(messageHandlers, sessionManager);
        SipProxyProperties.UdpForward udpForward = proxyProperties.getUdpForward();
        adapter.setClientAddress(nullSafeGet(udpForward.getClient()));
        adapter.setRemoteAddress(nullSafeGet(udpForward.getGateway()));
        adapter.setBackServerAddress(nullSafeGet(proxyProperties.getUdpBackward().getServer()));
        adapter.setContactAddress(nullSafeGet(proxyProperties.getUdpBackward().getServer()));

        return adapter;
    }

    @Bean
    public SipProxyHandlerAdapter backwardProxyHandlerAdapter(
            @Qualifier("sipMessageHandlers") List<SipMessageHandler<SipMessage>> messageHandlers,
            SipSessionManager sessionManager
    ) {
        SipProxyHandlerAdapter adapter = buildHandlerAdapter(messageHandlers, sessionManager);
        SipProxyProperties.UdpBackward backward = proxyProperties.getUdpBackward();
        adapter.setClientAddress(nullSafeGet(backward.getClient()));
        adapter.setRemoteAddress(nullSafeGet(backward.getGateway()));
        adapter.setContactAddress(nullSafeGet(proxyProperties.getUdpForward().getServer()));

        return adapter;
    }

    private SipProxyHandlerAdapter buildHandlerAdapter(
            @Qualifier("sipMessageHandlers") List<SipMessageHandler<SipMessage>> messageHandlers,
            SipSessionManager sessionManager
    ) {
        SipProxyHandlerAdapter handlerAdapter = new SipProxyHandlerAdapter(channelManager, sessionManager);
        handlerAdapter.setSipMessageHandlers(messageHandlers);
        return handlerAdapter;
    }

    @Bean
    @ConditionalOnMissingBean(name = "sipMessageHandlers")
    public List<SipMessageHandler<SipMessage>> sipMessageHandlers() {
        return Lists.newArrayList();
    }

    @Bean
    public SipSessionManager sipSessionManager(DeviceManager deviceManager) {
        SipSessionManagerImpl sessionManager = new SipSessionManagerImpl();
        sessionManager.setCacheManager(cacheManager);
        sessionManager.setMediaService(mediaService());
        sessionManager.setDeviceManager(deviceManager);
        return sessionManager;
    }

    @Bean
    @ConditionalOnMissingBean(DeviceManager.class)
    public DeviceManager deviceManager() {
        return new DefaultDeviceManager();
    }


    @Bean
    @SneakyThrows
    public MediaService mediaService() {
        Address mediaServer = proxyProperties.getMediaServer();
        return serviceRegistry.lookup(mediaServer.getHost(), mediaServer.getPort(), false, MediaService.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.proxyInitializer == null) {
            this.proxyInitializer = new ProxyInitializer() {
                @Override
                public void customizeForwardServer(SipUdpMessageServer sipForwardServer) {

                }

                @Override
                public void customizeBackwardServer(SipUdpMessageServer sipBackwardServer) {

                }
            };
        }
    }
}