package com.fable.framework.proxy.sip.handler;

import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.sip.session.SipSession;
import io.pkts.packet.sip.SipMessage;

/**
 * .
 *
 * @author stormning 2017/11/29
 * @since 1.3.0
 */
public interface InternalSipMessageHandler<T extends SipMessage> {

    SipRouterInfo handleInternal(SipSession session, T message);

    SipSession getSession(T message);
}
