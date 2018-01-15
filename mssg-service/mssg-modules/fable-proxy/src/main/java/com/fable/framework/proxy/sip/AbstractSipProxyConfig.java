package com.fable.framework.proxy.sip;

import com.fable.framework.proxy.AbstractProxyConfig;
import com.fable.framework.proxy.sip.session.SipSessionManager;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/29
 * @since 1.3.0
 */
@Data
public abstract class AbstractSipProxyConfig extends AbstractProxyConfig {

    private final InetSocketAddress contactAddress;
    private SipSessionManager sessionManager;

    public AbstractSipProxyConfig(InetSocketAddress clientAddress, InetSocketAddress remoteAddress, InetSocketAddress contactAddress, SipSessionManager sessionManager, InetSocketAddress backServerAddress) {
        super(clientAddress, remoteAddress, backServerAddress);
        this.sessionManager = sessionManager;
        this.contactAddress = contactAddress;
    }
}
