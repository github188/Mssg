package com.fable.framework.proxy.sip.config;

import com.fable.framework.proxy.sip.server.SipUdpMessageServer;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
public interface ProxyInitializer {

    void customizeForwardServer(SipUdpMessageServer sipForwardServer);

    void customizeBackwardServer(SipUdpMessageServer sipBackwardServer);
}
