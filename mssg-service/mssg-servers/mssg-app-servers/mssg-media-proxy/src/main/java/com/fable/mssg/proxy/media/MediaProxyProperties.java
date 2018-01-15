package com.fable.mssg.proxy.media;

import com.fable.framework.core.config.Address;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * .
 *
 * @author stormning 2017/9/5
 * @since 1.3.0
 */
@ConfigurationProperties(prefix = "com.fable.mssg.proxy.media")
@Data
public class MediaProxyProperties {
    private boolean master = false;

    private Address remoteServer;

    private boolean rtpFilter = false;
}
