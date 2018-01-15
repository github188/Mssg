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
import io.pkts.packet.sip.header.SipHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

/**
 * @author: yuhl Created on 16:45 2017/9/8 0008
 * 处理客户端注册请求
 */
@Slf4j
public class ClientRegisterHandler implements SipMessageHandler<SipMessage> {

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    /**
     * 客户端注册或保活信令
     *
     * @param sipMessage
     * @return
     */
    @Override
    public boolean supports(SipMessage sipMessage) {
        boolean flag = false;
        Buffer buffer = sipMessage.getRawContent();
        if (buffer == null) {
            flag = false;
        } else if (buffer.toString().contains("Keepalive")) {
            flag = true;
        }
        return sipMessage.isRegister() || flag;
    }

    /**
     * 客户端注册信令的处理
     *
     * @param sipMessage
     * @return
     */
    @Override
    public SipRouterInfo handle(SipMessage sipMessage) {
        MasterConfigProperties.Slave slave = masterConfigProperties.getSlave(); // slave的配置
        MasterConfigProperties.Authorize authorize = masterConfigProperties.getAuthorize(); // 认证配置
        SipResponse response = null;
        String realm = "realm=" + authorize.getRealm(); // realm信息
        String nonce = "nonce=" + authorize.getNonce(); // nonce信息
        String algorithm = "algorithm=MD5";// 加密算法
        String clientHost = sipMessage.getViaHeader().getHost().toString(); // 客户端IP
        int clientPort = sipMessage.getViaHeader().getPort(); // 客户端端口
        InetSocketAddress slaveAddress = AddressUtils.from(slave.getHost(), slave.getPort());
        InetSocketAddress clientAddress = AddressUtils.from(clientHost, clientPort);
        /**
         * 保活信令直接返回200 OK
         */
        io.pkts.buffer.Buffer buffer = sipMessage.getRawContent();
        if (null != buffer && buffer.toString().contains("Keepalive")) { // 保活信令
            return new SipRouterInfo(slaveAddress, clientAddress, sipMessage.createResponse(SipConstant.OK_CMD));
        }

        /**
         * 首先判断是否为第一次注册
         * 如果Authorization信息是则返回401 Unauthorized
         * 如果不是则校验Authorization信息的正确性
         */
        if (!sipMessage.toBuffer().toString().contains("Authorization")) { // 不包含Authorization
            DateHeader dateHeader = DateHeader.create(SipConstant.currentDate() + "T" + SipConstant.millionTime());
            response = sipMessage.createResponse(SipConstant.UNAUTH_CMD);
            response.getViaHeader().setRPort(clientPort);
            response.setHeader(SipHeader.frame(Buffers.wrap("WWW-Authenticate: "
                    + "Digest " + realm + "," + algorithm + "," + nonce))); // WWW-Authenticate内容
            response.setHeader(SipHeader.frame(Buffers.wrap("User-Agent: fablesoft")));
            response.setHeader(dateHeader);
        } else if (sipMessage.toBuffer().toString().contains("Authorization")) { // 校验Authorization信息
//            SipHeader authHeader = sipMessage.getHeader("Authorization"); // 获取认证信息
            String sipContent = sipMessage.toBuffer().toString();
//            String authInfo = authHeader.toString(); // 认证内容信息
            log.info("slave authorization realm {} ", realm);
            log.info("slave authorization nonce {} ", nonce);
            log.info("slave authorization algorithm {} ", algorithm);
            if (sipContent.contains(realm) && sipContent.contains(nonce) && sipContent.contains(algorithm)) { // 通过 200 OK
                response = sipMessage.createResponse(SipConstant.OK_CMD);
                ContactHeader contactHeader = sipMessage.getContactHeader();
                response.setHeader(contactHeader);
                DateHeader dateHeader = DateHeader.create(SipConstant.currentDate()
                        + "T" + SipConstant.currentTime());
                response.setHeader(ExpiresHeader.create(sipMessage.getExpiresHeader().getExpires()));
                response.setHeader(dateHeader);
            } else { // 否则认证失败
                response = sipMessage.createResponse(SipConstant.UNAUTH_CMD);
            }
        }
        // 发送响应信令
        return new SipRouterInfo(slaveAddress, clientAddress, response);
    }
}
