package com.fable.mssg.catalog.sipheader;

import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipRequest;
import io.pkts.packet.sip.header.*;
import lombok.Data;

/**
 * @author: yuhl Created on 18:13 2017/9/6 0006
 */
@Data
public class RegisterHeader {

    public String gatewayHost; // 网关地址

    public int gatewayPort; // 网关ip

    public String targetUser; // 网关设备名

    public String clientHost; // master地址

    public int clientPort; // master端口

    public String clientUser; // master设备名

    public RegisterHeader(String gatewayHost, int gatewayPort, String targetUser,
                          String clientHost, int clientPort, String clientUser) {
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
        this.targetUser = targetUser;
        this.clientHost = clientHost;
        this.clientPort = clientPort;
        this.clientUser = clientUser;
    }

    /**
     * 生成注册信令头部
     * @return
     */
    public SipMessage generateRegisterHeader() {
        final FromHeader fromHeader = FromHeader.with().user(clientUser).host(clientHost).port(clientPort)
                .parameter("tag", FromHeader.generateTag().toString()).build();
        final ViaHeader viaHeader = ViaHeader.with().host(clientHost).port(clientPort)
                .branch(ViaHeader.generateBranch()).transportUDP().build();
        final ToHeader toHeader = ToHeader.with().user(targetUser).host(gatewayHost).port(gatewayPort).build();
        final ContactHeader contact = ContactHeader.with().user(clientUser).host(clientHost).port(clientPort).build();
        final ExpiresHeader expiresHeader = ExpiresHeader.create(3600);
        final SipRequest request = SipRequest.request(Buffers.wrap("REGISTER"),
                "sip:" + targetUser + "@" + gatewayHost + ":" + gatewayPort)
                .from(fromHeader).via(viaHeader).to(toHeader).contact(contact).build();
        request.setHeader(expiresHeader); // 超时时间
        request.setHeader(SipHeader.frame(Buffers.wrap("Allow: PRACK, INVITE, ACK, BYE," +
                " CANCEL, UPDATE, INFO, SUBSCRIBE, NOTIFY, REFER, MESSAGE, OPTIONS")));
        //request.setHeader(SipHeader.frame(Buffers.wrap("Content-Length:  0")));
        return request;
    }
}
