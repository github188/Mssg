package com.fable.framework.proxy.sip.handler;

import com.fable.framework.proxy.sip.session.SipMessageWrapper;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.sip.session.SipSession;
import lombok.Setter;

/**
 * .
 *
 * @author stormning 2017/11/28
 * @since 1.3.0
 */
public class InternalSipMessageHandlerComposite implements InternalSipMessageHandler<SipMessageWrapper> {

    @Setter
    private InternalSipMessageHandler<SipMessageWrapper> requestHandler;

    @Setter
    private InternalSipMessageHandler<SipMessageWrapper> responseHandler;

    public SipRouterInfo handleInternal(SipSession session, SipMessageWrapper message) {
        if (message.isRequest()) {
            return requestHandler.handleInternal(session, message);
        } else {
            return responseHandler.handleInternal(session, message);
        }
    }

    public SipSession getSession(SipMessageWrapper message) {
        if (message.isRequest()) {
            return requestHandler.getSession(message);
        } else {
            return responseHandler.getSession(message);
        }
    }
}
