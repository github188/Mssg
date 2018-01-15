package com.fable.mssg.catalog.handler;

import com.fable.framework.proxy.sip.handler.SipMessageHandler;
import com.fable.framework.proxy.sip.session.SipRouterInfo;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.utils.SipConstant;
import com.fable.mssg.security.header.DateHeader;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipResponse;
import io.pkts.packet.sip.header.ContactHeader;
import io.pkts.packet.sip.header.ExpiresHeader;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

/**
 * @author: yuhl Created on 16:27 2017/9/6 0006
 */
public class MasterRegisterHandler implements SipMessageHandler<SipMessage> {

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    /**
     * 设备注册返回的消息判断
     *
     * @param sipMessage
     * @return
     */
    @Override
    public boolean supports(SipMessage sipMessage) {
        /**
         * 是注册信令，或者是心跳信令
         */
        // 返回的200 OK或者是结果信令
        boolean flag = false;
        Buffer buffer = sipMessage.getRawContent();
        String method = sipMessage.getCSeqHeader().getMethod().toString();
        if (buffer == null) { // 信令内容为空
            flag = false;
        } else if (buffer.toString().contains("Notify") && !"NOTIFY".equals(method)
                && buffer.toString().contains("Keepalive")) { // 保活但非通知信令
            flag = true;
        }
        return sipMessage.getCSeqHeader().getMethod().toString().contains("REGISTER")
                || (sipMessage.isMessage() && flag);
    }

    /**
     * 处理设备注册返回的消息
     * 如果是心跳信令应该返回200 OK
     * 如果是200 OK的注册信令则不处理
     *
     * @param sipMessage
     * @return
     */
    @Override
    public SipRouterInfo handle(SipMessage sipMessage) {
        if (sipMessage.getInitialLine().toString().contains("200 OK")) { // 200 OK不处理
            return null;
        }
        MasterConfigProperties.Master master = masterConfigProperties.getMaster();
        String gatewayHost = sipMessage.getViaHeader().getHost().toString(); // 网关ip
        int gatewayPort = sipMessage.getViaHeader().getPort(); // 网关端口
        InetSocketAddress masterAddress = AddressUtils.from(master.getHost(), master.getPort());
        InetSocketAddress gatewayAddress = AddressUtils.from(gatewayHost, gatewayPort);
        // 返回200 OK
        final SipResponse response = sipMessage.createResponse(SipConstant.OK_CMD);
        /**
         * 注册信令则返回200 OK
         */
        if (sipMessage.isRegister()) {
            ContactHeader contactHeader = sipMessage.getContactHeader();
            response.setHeader(contactHeader);
//            response.getViaHeader().setRPort(7100);
//            response.getViaHeader().setReceived(Buffers.wrap("192.168.30.241"));
            response.getToHeader().setParameter(Buffers.wrap("tag"), response.getViaHeader().getBranch());
            response.setHeader(ExpiresHeader.create(sipMessage.getExpiresHeader().getExpires()));
            DateHeader dateHeader = DateHeader.create(SipConstant.currentDate()
                    + "T" + SipConstant.currentTime());
            response.setHeader(dateHeader);
//            response.getContactHeader().setParameter(Buffers.wrap("expires"), Buffers.wrap("3600"));
            //response.setHeader(SipHeader.frame(Buffers.wrap("Content-Length: 0")));
        }
        /**
         * 心跳信令返回200 OK
         */
        if (sipMessage.isMessage() && sipMessage.getRawContent().toString().contains("Notify")) {
            //response.setHeader(SipHeader.frame(Buffers.wrap("Content-Length: 0")));
        }
//        Channel sipClient = channelProvider.createIfAbsent(masterAddress);
//        SipUtils.send(sipClient, gatewayAddress, response);// 发送200 OK的响应信令
        return new SipRouterInfo(masterAddress, gatewayAddress, response);
    }
}
