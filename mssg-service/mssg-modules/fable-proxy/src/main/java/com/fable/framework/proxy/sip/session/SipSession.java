package com.fable.framework.proxy.sip.session;

import com.fable.framework.proxy.session.Session;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * .
 *
 * @author stormning 2017/12/7
 * @since 1.3.0
 */
@Data
@ToString
public class SipSession implements Session<SipSessionKeeper>, Serializable {

    private SipSessionKeeper raw;

    private String id;

    private boolean request = false;

    SipSession(SipSessionKeeper raw, boolean request) {
        this.raw = raw;
        //use input id as sessionId, need mapping or generate a new id???
        this.id = raw.getRequestSnapshot().getCallId().getInput();
        this.request = request;
    }

    @Override
    public void setAttribute(Object key, Object value) {
        raw.getAttributes().put(key, value);
    }

    @Override
    public Object getAttribute(Object key) {
        return raw.getAttributes().get(key);
    }
}
