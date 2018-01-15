package com.fable.framework.proxy.sip.session;

import org.springframework.core.NamedThreadLocal;

/**
 * .
 *
 * @author stormning 2017/11/28
 * @since 1.3.0
 */
public class SessionAttributeHolder {
    private static final ThreadLocal<SessionAttributes> sessionAttributeHolder =
            new NamedThreadLocal<SessionAttributes>("Sip session attributes");

    public static void resetSessionAttributes() {
        sessionAttributeHolder.remove();
    }

    public static SessionAttributes getSessionAttributes() {
        SessionAttributes sessionAttributes = sessionAttributeHolder.get();
        if (sessionAttributes == null) {
            sessionAttributes = new SessionAttributes();
            setSessionAttributes(sessionAttributes);
        }
        return sessionAttributes;
    }

    public static void setSessionAttributes(SessionAttributes attributes) {
        if (attributes == null) {
            resetSessionAttributes();
        } else {
            sessionAttributeHolder.set(attributes);
        }
    }
}
