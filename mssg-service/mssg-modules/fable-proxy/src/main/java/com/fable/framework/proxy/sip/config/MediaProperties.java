package com.fable.framework.proxy.sip.config;

import com.fable.framework.core.config.Address;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * .
 *
 * @author stormning 2017/11/14
 * @since 1.3.0
 */
@ConfigurationProperties(prefix = "fable.proxy.sip-media")
@Data
public class MediaProperties {

    private Forward forward;

    private Backward backward;

    private Address proxyAddress;

    @Data
    public static class Forward {
        private String hostname;
        private String clientHostname;
        private String proxyHostname;
        private int proxyPort;
    }

    @Data
    public static class Backward {
        private String hostname;
        private String clientHostname;
    }
}