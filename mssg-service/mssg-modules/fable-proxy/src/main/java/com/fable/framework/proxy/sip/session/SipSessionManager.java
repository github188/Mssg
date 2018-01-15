package com.fable.framework.proxy.sip.session;


import com.fable.framework.proxy.session.SessionManager;

/**
 * .
 *
 * @author stormning 2017/11/8
 * @since 1.3.0
 */
public interface SipSessionManager extends SessionManager<SipSession, SipSessionKeeper, SipMessageContext> {

    @Override
    SipSession getSession(String sessionId);

    @Override
    SipSession newSession(SipMessageContext message);

    @Override
    SipSession save(SipSession sipSession);

    ReSnapshot updateResponseSnapshot(SipSession session, SipMessageContext messageContext);
}
