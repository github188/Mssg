package com.fable.framework.proxy.sip.handler;


import com.fable.framework.proxy.sip.session.SipRouterInfo;
import io.pkts.packet.sip.SipMessage;

/**
 * .
 *
 * @author stormning 2017/8/30
 * @since 1.3.0
 */
public interface SipMessageHandler<T extends SipMessage> {
    /**
     * 这个消息处理不处理
     *
     * @param sipMessage
     * @return
     */
    boolean supports(T sipMessage);

    /**
     * 处理原始消息
     *
     * @param rawMessage 原始消息
     * @return 如果返回false则不会继续后续操作，默认为true
     */
    SipRouterInfo handle(T rawMessage);
}
