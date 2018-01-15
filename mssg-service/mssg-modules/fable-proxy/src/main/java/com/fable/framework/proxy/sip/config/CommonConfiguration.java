package com.fable.framework.proxy.sip.config;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.ChannelManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * .
 *
 * @author stormning 2017/11/28
 * @since 1.3.0
 */
@Configuration
public class CommonConfiguration {

    @Bean
    public ChannelManager channelManager(){
        return new ChannelManagerImpl();
    }
}
