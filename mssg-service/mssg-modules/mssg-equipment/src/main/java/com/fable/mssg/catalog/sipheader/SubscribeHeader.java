package com.fable.mssg.catalog.sipheader;

import com.fable.mssg.catalog.utils.SipConstant;
import com.fable.mssg.catalog.xml.subscribe.CatalogSubscribeXml;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipRequest;
import io.pkts.packet.sip.header.*;
import lombok.Data;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author: yuhl Created on 9:35 2017/9/5 0005
 * 设备订阅信令消息头
 */
@Data
public class SubscribeHeader {

    public String gatewayHost; // 网关地址

    public int gatewayPort; // 网关ip

    public String targetUser; // 网关设备名

    public String clientHost; // master地址

    public int clientPort; // master端口

    public String clientUser; // master设备名

    public int expires; // 超时时间

    public SubscribeHeader(String gatewayHost, int gatewayPort, String targetUser,
                       String clientHost, int clientPort, String clientUser, int expires) {
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
        this.targetUser = targetUser;
        this.clientHost = clientHost;
        this.clientPort = clientPort;
        this.clientUser = clientUser;
        this.expires = expires;
    }

    /**
     * 生成目录订阅的SIP信令头信息
     * @return
     */
    public SipMessage generateSubscribeHeader() throws JAXBException {

        String sessionInfo = SipConstant.SESSION_MAP.get(targetUser); // 根据网关获取tag和callId信息
        String tag;
        CallIdHeader callIdHeader;
        if (null != sessionInfo && !"".equals(sessionInfo)) { // 刷新订阅
            String[] sessionInfos = sessionInfo.split("#", -1);
            tag = sessionInfos[0];
            callIdHeader = CallIdHeader.frame(Buffers.wrap(sessionInfos[1]));
            SipConstant.CSEQ ++;
        } else { // 初始订阅
            tag = FromHeader.generateTag().toString();
            callIdHeader = CallIdHeader.create();
            SipConstant.SESSION_MAP.put(targetUser, tag + "#" + callIdHeader.getCallId().toString());
            SipConstant.CSEQ ++;
        }
        final CSeqHeader cSeqHeader = CSeqHeader.with().cseq(SipConstant.CSEQ).method("SUBSCRIBE").build();
        final FromHeader fromHeader = FromHeader.with().user(clientUser).host(clientHost).port(clientPort)
                .parameter("tag", tag).build();
        final ViaHeader viaHeader = ViaHeader.with().host(clientHost).port(clientPort)
                .branch(ViaHeader.generateBranch()).transportUDP().build();
        final ToHeader toHeader = ToHeader.with().user(targetUser).host(gatewayHost).port(gatewayPort).build();
        final ExpiresHeader expiresHeader = ExpiresHeader.create(expires);
        final ContactHeader contactHeader = ContactHeader.with().user(clientUser).host(clientHost)
                .port(clientPort).build();
        final SipRequest request = SipRequest.request(Buffers.wrap("SUBSCRIBE"),
                "sip:" + targetUser + "@" + gatewayHost + ":" + gatewayPort)
                .from(fromHeader).via(viaHeader).to(toHeader).contact(contactHeader).build();
        request.setHeader(expiresHeader);
        request.setHeader(callIdHeader);
        request.setHeader(cSeqHeader);
        request.setHeader(SipHeader.frame(Buffers.wrap("Event: Catalog;id=" + new Random().nextInt(10000))));
        request.setHeader(SipHeader.frame(Buffers.wrap(ContentTypeHeader.frame(Buffers.wrap(
                "Application/MANSCDP+xml")).toString())));
        SecureRandom random = new SecureRandom();
        CatalogSubscribeXml xml = new CatalogSubscribeXml();
        xml.setCmdType("Catalog");
        xml.setDeviceId(targetUser);
        xml.setSn(random.nextInt(1000000));
        JAXBContext context = JAXBContext.newInstance(CatalogSubscribeXml.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(xml, writer);
        request.setPayload(Buffers.wrap(writer.toString()));
        //request.setHeader(SipHeader.frame(Buffers.wrap("Content-Length: " + writer.toString().length())));
        return request;
    }

}
