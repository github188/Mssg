package com.fable.mssg.proxy.sip.config;

import com.fable.framework.core.config.Address;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * .
 *
 * @author stormning 2017/9/5
 * @since 1.3.0
 */
@ConfigurationProperties(prefix = "com.fable.mssg.proxy.sip")
@Data
public class SipProxyProperties {

    private Address remoteServer;

    private boolean master = false;

    //是否启用信令鉴权
    private boolean authEnabled = true;

    //是否启用信令加解密
    private boolean encryptEnabled = true;

    private Address mediaServer;
}
