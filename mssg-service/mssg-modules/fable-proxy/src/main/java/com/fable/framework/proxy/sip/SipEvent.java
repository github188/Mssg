package com.fable.framework.proxy.sip;

import com.fable.framework.proxy.session.Session;
import com.fable.framework.proxy.sip.session.SipSession;
import com.fable.framework.proxy.sip.session.SipSessionKeeper;
import io.pkts.packet.sip.SipMessage;
import lombok.Data;

import java.io.Serializable;

@Data
public class SipEvent implements Serializable {

    private SipSession sipSession;

    private SipMessage sipMessage;

    public SipEvent(SipSession sipSession, SipMessage sipMessage) {
        this.sipSession = sipSession;
        this.sipMessage = sipMessage;
    }
}