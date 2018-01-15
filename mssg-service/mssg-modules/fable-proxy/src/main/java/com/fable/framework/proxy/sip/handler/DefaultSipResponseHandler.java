package com.fable.framework.proxy.sip.handler;

import com.fable.framework.proxy.session.Session;
import com.fable.framework.proxy.sip.AbstractSipProxyConfig;
import com.fable.framework.proxy.sip.Address;
import com.fable.framework.proxy.sip.session.*;
import com.fable.framework.proxy.sip.util.SnapshotUtils;
import io.pkts.packet.sip.SipResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/8
 * @since 1.3.0
 */
@Slf4j
public class DefaultSipResponseHandler extends AbstractSipProxyConfig implements InternalSipMessageHandler<SipMessageWrapper> {


    public DefaultSipResponseHandler(InetSocketAddress clientAddress, InetSocketAddress remoteAddress, InetSocketAddress contactAddress, SipSessionManager sessionManager) {
        super(clientAddress, remoteAddress, contactAddress, sessionManager, null);
    }

    @Override
    public SipRouterInfo handleInternal(SipSession session, SipMessageWrapper message) {
        ReSnapshot rs = session.getRaw().getResponseSnapshot();
        InetSocketAddress contactAddress = getContactAddress();
//        if (message.isSuccess() && message.hasContent() && message.getContentTypeHeader().isSDP()) {
            rs = getSessionManager().updateResponseSnapshot(session, new SipMessageContext(
                    message,
                    getClientAddress(),
                    getRemoteAddress(),
                    contactAddress,
                    null
            ));
//        }
        //fixed ugly
        //TODO optimize
        rs.setContactAddress(MappedProp.output(new Address(contactAddress.getAddress().getHostAddress(), contactAddress.getPort())));
        return new SipRouterInfo(getClientAddress(), rs.getToAddress(), SnapshotUtils.createResponseOutput(message, rs));
    }

    @Override
    public SipSession getSession(SipMessageWrapper message) {
        log.info("Handling sip response ...");
        return message.getSession();
    }
}
