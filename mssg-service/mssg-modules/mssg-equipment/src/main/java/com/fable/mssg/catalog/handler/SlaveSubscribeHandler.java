package com.fable.mssg.catalog.handler;

import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.utils.SipConstant;
import com.fable.mssg.service.resource.ResSubscribeService;
import io.netty.channel.ChannelFuture;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

/**
 * @author: yuhl Created on 11:10 2017/12/1 0001 slave侧平台订阅处理
 */
public class SlaveSubscribeHandler implements SipMessageHandler<SipMessage> {

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    @Autowired
    public ResSubscribeService resSubscribeService;

    @Autowired
    private ChannelManager channelManager;

    /**
     * 处理订阅信令
     *
     * @param sipMessage
     * @return
     */
    @Override
    public boolean supports(SipMessage sipMessage) {
        return sipMessage.getCSeqHeader().getMethod().toString().contains("SUBSCRIBE");
    }

    /**
     * 处理订阅消息，返回200 OK
     *
     * @param sipMessage 原始消息
     * @return
     */
    @Override
    public SipRouterInfo handle(SipMessage sipMessage) {
        MasterConfigProperties.Slave slave = masterConfigProperties.getSlave();
        String gatewayHost = sipMessage.getViaHeader().getHost().toString(); // 网关ip
        int gatewayPort = sipMessage.getViaHeader().getPort(); // 网关端口
        InetSocketAddress slaveAddress = new InetSocketAddress(slave.getHost(), slave.getPort());
        InetSocketAddress gatewayAddress = new InetSocketAddress(gatewayHost, gatewayPort);
        SipResponse response = sipMessage.createResponse(SipConstant.OK_CMD);
        ChannelFuture future = channelManager.selectUdpChannel(null, slaveAddress, null);
        SipUtils.send(future, gatewayAddress, response);
        return null;
    }

}
