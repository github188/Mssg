package com.fable.mssg.catalog.sipheader;

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

/**
 * @author: yuhl Created on 9:35 2017/9/5 0005
 * 设备目录查询信令头
 */
@Data
public class QueryHeader {

    public String gatewayHost; // 网关地址

    public int gatewayPort; // 网关ip

    public String targetUser; // 网关设备名

    public String clientHost; // master地址

    public int clientPort; // master端口

    public String clientUser; // master设备名

    public QueryHeader(String gatewayHost, int gatewayPort, String targetUser,
                       String clientHost, int clientPort, String clientUser) {
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
        this.targetUser = targetUser;
        this.clientHost = clientHost;
        this.clientPort = clientPort;
        this.clientUser = clientUser;
    }

    /**
     * 生成目录查询的SIP信令头信息
     * @return
     */
    public SipMessage generateQueryHeader() throws JAXBException {

        final FromHeader fromHeader = FromHeader.with().user(clientUser).host(clientHost).port(clientPort)
                .parameter("tag", FromHeader.generateTag().toString()).build();
        final ViaHeader viaHeader = ViaHeader.with().host(clientHost).port(clientPort)
                .branch(ViaHeader.generateBranch()).transportUDP().build();
        final ToHeader toHeader = ToHeader.with().user(targetUser).host(gatewayHost).port(gatewayPort).build();
        final SipRequest request = SipRequest.request(Buffers.wrap("MESSAGE"),
                "sip:" + targetUser + "@" + gatewayHost + ":" + gatewayPort)
                .from(fromHeader).via(viaHeader).to(toHeader).build();
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
