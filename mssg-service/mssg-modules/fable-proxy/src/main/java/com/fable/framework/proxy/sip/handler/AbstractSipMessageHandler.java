package com.fable.framework.proxy.sip.handler;

import com.fable.framework.proxy.sip.SipEvent;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.sip.session.SipSession;
import io.pkts.packet.sip.SipMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * .
 *
 * @author stormning 2017/11/14
 * @since 1.3.0
 */
public abstract class AbstractSipMessageHandler<T extends SipMessage> implements SipMessageHandler<T>, ApplicationEventPublisherAware {

    protected ApplicationEventPublisher eventPublisher;

    @Override
    public SipRouterInfo handle(T rawMessage) {
        SipSession session = getSession(rawMessage);
        String content = rawMessage.toBuffer().toString().toLowerCase();
        if (eventPublisher != null && content.contains("200 ok")) {
            eventPublisher.publishEvent(new SipEvent(session, rawMessage));
        }
        return handleInternal(session, rawMessage);
    }

    protected abstract SipRouterInfo handleInternal(SipSession session, T rawMessage);

    protected abstract SipSession getSession(T rawMessage);

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
