package com.fable.framework.proxy.sip.handler;

import com.fable.framework.proxy.sip.session.SipRouterInfo;
import io.pkts.packet.sip.SipRequest;

/**
 * @author: yuhl Created on 10:52 2017/11/16 0016
 */
public class SipSignalHandler implements SipMessageHandler<SipRequest> {

    /**
     * 这个消息处理不处理
     * Register信令不处理
     *
     * @param sipMessage
     * @return
     */
    @Override
    public boolean supports(SipRequest sipMessage) {
        return !sipMessage.isRegister();
    }

    @Override
    public SipRouterInfo handle(SipRequest rawMessage) {
        return null;
    }
}
