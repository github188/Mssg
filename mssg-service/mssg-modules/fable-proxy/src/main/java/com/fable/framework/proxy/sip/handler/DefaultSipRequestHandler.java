package com.fable.framework.proxy.sip.handler;

import com.fable.framework.proxy.session.Session;
import com.fable.framework.proxy.sip.AbstractSipProxyConfig;
import com.fable.framework.proxy.sip.session.*;
import com.fable.framework.proxy.sip.util.SnapshotUtils;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/8
 * @since 1.3.0
 */
@Slf4j
public class DefaultSipRequestHandler extends AbstractSipProxyConfig implements InternalSipMessageHandler<SipMessageWrapper> {


    public DefaultSipRequestHandler(InetSocketAddress clientAddress, InetSocketAddress remoteAddress, InetSocketAddress contactAddress, SipSessionManager sessionManager, InetSocketAddress backServerAddress) {
        super(clientAddress, remoteAddress, contactAddress, sessionManager, backServerAddress);
    }


    @Override
    public SipSession getSession(SipMessageWrapper message) {
        log.info("Handling sip request ...");
        SipSession session = message.getSession();
        if (session == null) {
            //获取,为什么要获取
            //以下回路型需要记录
            //callId  请求替换内容（新生成的）、响应回路
            //contact 请求替换内容、响应回路
            //media 请求替换内容（媒体服务协商的）、媒体响应回路
            //connection 请求替换内容（固定的）、媒体响应回路
            //toDeviceId,toDomain  请求内容替换（接口获取的）、请求目标
            //如果有网关则请求目标为网关，且不替换TO，否则根据toDeviceId获取下级媒体设备信息（deviceId、host、port）
            //因为to需要计算，为了下一次不计算，也放入requestSnapshot的output中
            //from 请求替换内容
            //original 请求替换内容
            //via 请求内容替换
            //如果有网关则请求目标为网关，且不替换TO，否则根据toDeviceId获取下级媒体设备信息（deviceId、host、port）
            //保存回路所需字段
            session = getSessionManager().newSession(new SipMessageContext(message, getClientAddress(), getRemoteAddress(), getContactAddress(), getBackServerAddress()));
        }
        return session;
    }

    @Override
    public SipRouterInfo handleInternal(SipSession session, SipMessageWrapper message) {
        RSnapshot snapshot = session.getRaw().getRequestSnapshot();
        return new SipRouterInfo(getClientAddress(), snapshot.getToAddress(), SnapshotUtils.createRequestOutput(message, snapshot));
    }
}