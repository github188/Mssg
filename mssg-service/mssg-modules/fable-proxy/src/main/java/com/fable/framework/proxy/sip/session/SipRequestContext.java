package com.fable.framework.proxy.sip.session;

import io.pkts.packet.sip.SipMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/9
 * @since 1.3.0
 */
@AllArgsConstructor
@Getter
public class SipRequestContext {

    private InetSocketAddress localAddress;

    private InetSocketAddress remoteAddress;

    private SipMessage sipMessage;
}
