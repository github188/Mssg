package com.fable.framework.proxy.sip.handler;

import com.fable.framework.proxy.sip.session.SipMessageWrapper;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.sip.session.SipSession;

/**
 * .
 *
 * @author stormning 2017/11/29
 * @since 1.3.0
 */
public class DefaultSipMessageHandler extends AbstractSipMessageHandler<SipMessageWrapper> {

    private InternalSipMessageHandlerComposite messageHandler;

    public DefaultSipMessageHandler(InternalSipMessageHandlerComposite messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public boolean supports(SipMessageWrapper sipMessage) {
        return true;
    }

    @Override
    protected SipRouterInfo handleInternal(SipSession session, SipMessageWrapper message) {
        return messageHandler.handleInternal(session, message);
    }

    @Override
    protected SipSession getSession(SipMessageWrapper rawMessage) {
        return messageHandler.getSession(rawMessage);
    }
}
