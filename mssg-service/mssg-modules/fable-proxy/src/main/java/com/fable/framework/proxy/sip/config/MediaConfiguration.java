package com.fable.framework.proxy.sip.config;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.media.MediaService;
import com.fable.framework.proxy.sip.media.MediaServiceImpl;
import com.fable.framework.proxy.sip.util.AddressUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/14
 * @since 1.3.0
 */
@Configuration
@EnableConfigurationProperties(MediaProperties.class)
@ConditionalOnProperty(name = "fable.proxy.sip-media.forward.hostname")
@AutoConfigureAfter(CommonConfiguration.class)
public class MediaConfiguration implements InitializingBean {

    private MediaInitializer mediaInitializer;

    public MediaConfiguration(@Autowired(required = false) MediaInitializer mediaInitializer) {
        this.mediaInitializer = mediaInitializer;
    }

    @Bean
    public CacheManager cacheManager(){
        return new GuavaCacheManager();
    }

    @Bean
    public MediaService mediaService(MediaProperties mediaProperties, ChannelManager channelManager) {
        MediaServiceImpl mediaService = new MediaServiceImpl();
        MediaProperties.Forward forward = mediaProperties.getForward();
        mediaService.setForwardClientHostname(forward.getClientHostname());
        mediaService.setForwardHostname(forward.getHostname());
        MediaProperties.Backward backward = mediaProperties.getBackward();
        mediaService.setBackwardClientHostname(backward.getClientHostname());
        mediaService.setBackwardHostname(backward.getHostname());
        mediaService.setChannelManager(channelManager);
        mediaService.setProxyAddress(AddressUtils.nullSafeGet(mediaProperties.getProxyAddress()));
        mediaService.setCacheManager(cacheManager());
        mediaInitializer.customize(mediaService);
        if (forward.getProxyHostname() != null) {
            mediaService.bindProxyServer(new InetSocketAddress(forward.getProxyHostname(), forward.getProxyPort()));
        }
        return mediaService;
    }

    @Bean(name = "/com.fable.framework.proxy.sip.media.MediaService")
    public HessianServiceExporter mediaServiceExporter(MediaProperties mediaProperties, ChannelManager channelManager) {
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(mediaService(mediaProperties, channelManager));
        exporter.setServiceInterface(MediaService.class);
        exporter.setDebug(true);
        return exporter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.mediaInitializer == null) {
            this.mediaInitializer = mediaService -> {};
        }
    }
}
