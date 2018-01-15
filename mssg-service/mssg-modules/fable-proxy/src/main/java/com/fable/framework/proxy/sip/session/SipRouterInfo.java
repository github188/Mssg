package com.fable.framework.proxy.sip.session;

import com.fable.framework.proxy.RouterInfo;
import io.pkts.packet.sip.SipMessage;
import lombok.Data;
import lombok.ToString;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * .
 *
 * @author stormning 2017/9/4
 * @since 1.3.0
 */
@Data
@ToString
public class SipRouterInfo implements RouterInfo<SipMessage> {

    public InetSocketAddress localAddress;

    public InetSocketAddress remoteAddress;

    public SipMessage message;

    public List<SipMessage> batch;

    public SipRouterInfo(InetSocketAddress remoteAddress, SipMessage message) {
        this.remoteAddress = remoteAddress;
        this.message = message;
    }

    public SipRouterInfo(InetSocketAddress localAddress, InetSocketAddress remoteAddress, SipMessage message) {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.message = message;
    }

    public SipRouterInfo(InetSocketAddress localAddress, InetSocketAddress remoteAddress, List<SipMessage> batch) {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.batch = batch;
    }
}
