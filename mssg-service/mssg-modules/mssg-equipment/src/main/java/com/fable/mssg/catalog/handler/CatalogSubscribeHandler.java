package com.fable.mssg.catalog.handler;

import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipRequest;

/**
 * @author: yuhl Created on 11:31 2017/9/5 0005
 * 目录订阅逻辑处理
 */
public class CatalogSubscribeHandler implements SipMessageHandler<SipMessage> {

    /**
     * 判定消息类型并决定是否处理
     *
     * @param sipMessage
     * @return
     */
    @Override
    public boolean supports(SipMessage sipMessage) {
        return sipMessage.getCSeqHeader().getMethod().toString().contains("SUBSCRIBE");
    }

    /**
     * 处理原始消息，网关返回200 OK，忽略
     *
     * @param sipMessage
     * @return
     */
    @Override
    public SipRouterInfo handle(SipMessage sipMessage) {
        return null;
    }

}
