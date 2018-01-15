package com.fable.framework.proxy.sip.session;

import io.pkts.packet.sip.SipMessage;
import lombok.AllArgsConstructor;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/19
 * @since 1.3.0
 */
@AllArgsConstructor
public class SipMessageContext {
    private SipMessage sipMessage;
    private InetSocketAddress clientAddress;
    private InetSocketAddress remoteAddress;
    private InetSocketAddress contactAddress;
    private InetSocketAddress backServerAddress;
    public SipMessage getSipMessage() {
        return sipMessage;
    }

    public void setSipMessage(SipMessage sipMessage) {
        this.sipMessage = sipMessage;
    }

    public InetSocketAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetSocketAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public InetSocketAddress getContactAddress() {
        return contactAddress == null ? clientAddress : contactAddress;
    }

    public void setContactAddress(InetSocketAddress contactAddress) {
        this.contactAddress = contactAddress;
    }

    public InetSocketAddress getBackServerAddress() {
        return backServerAddress;
    }
}
