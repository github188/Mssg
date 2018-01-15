package com.fable.mssg.proxy.media;

import com.fable.framework.core.config.Address;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.framework.proxy.MessageFilter;
import com.fable.framework.proxy.sip.config.MediaConfiguration;
import com.fable.framework.proxy.sip.config.MediaInitializer;
import com.fable.mssg.proxy.media.filter.RtpFilter;
import com.fable.mssg.service.equipment.SecurityService;
import com.fable.mssg.utils.HttpsURLConnectionUtilX;
import com.google.common.collect.Lists;
import io.netty.channel.socket.DatagramPacket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MediaProxyProperties.class)
@AutoConfigureAfter(MediaConfiguration.class)
public class MediaConfig {

    @Value("${serverself.ssl.enabled}")
    private boolean isSSL;

    private final MediaProxyProperties proxyProperties;

    public MediaConfig(MediaProxyProperties proxyProperties) {
        this.proxyProperties = proxyProperties;
    }

    @Bean
    public MediaInitializer mediaInitializer(SecurityService securityService) {
        return mediaService -> {
            if (isMaster() && proxyProperties.isRtpFilter()) {
                mediaService.setUdpMessageFilters(Lists.newArrayList(rtpFilter(securityService)));
            }

        };
    }

    private boolean isMaster() {
        return proxyProperties.isMaster();
    }

    private MessageFilter<DatagramPacket> rtpFilter(SecurityService securityService) {
        return new RtpFilter(securityService);
    }

    @Bean
    @SneakyThrows
    public SecurityService securityService(ServiceRegistry serviceRegistry) {
        Address remoteServer = proxyProperties.getRemoteServer();
        return serviceRegistry.lookup(remoteServer.getHost(), remoteServer.getPort(), isSSL, SecurityService.class);
    }

    @Bean
    @ConditionalOnProperty(value = "serverself.ssl.enabled", havingValue = "true")
    public HttpsUrlConnectionBean HttpsUrlConnectionBean() {
        HttpsUrlConnectionBean bean = new HttpsUrlConnectionBean();
        bean.enableHttpsClient();
        return bean;
    }

    public static class HttpsUrlConnectionBean {
        public void enableHttpsClient() {
            try {
                HttpsURLConnectionUtilX.initHttpsURLConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
