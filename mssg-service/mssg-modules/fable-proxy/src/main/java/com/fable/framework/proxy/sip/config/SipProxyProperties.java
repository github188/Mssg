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
@ConfigurationProperties(prefix = "fable.proxy.sip")
@Data
public class SipProxyProperties {
    private UdpForward udpForward;

    private UdpBackward udpBackward;

    private TcpForward tcpForward;

    private TcpBackward tcpBackward;

    private Address mediaServer;

    private int sipSessionExpire = -1;

    @Data
    public static class UdpForward {
        private Address server;
        private Address client;
        private Address gateway;
    }

    @Data
    public static class UdpBackward {
        private Address server;
        private Address client;
        private Address gateway;
    }

    @Data
    public static class TcpForward {
        private Address server;
        private Address client;
        private Address gateway;
    }

    @Data
    public static class TcpBackward {
        private Address server;
        private Address client;
    }

}