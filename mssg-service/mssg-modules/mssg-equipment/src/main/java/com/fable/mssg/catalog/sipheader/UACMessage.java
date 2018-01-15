/*
package com.fable.mssg.catalog.sipheader;

import com.fable.framework.proxy.ChannelProvider;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.handler.UACHandler;
import com.fable.mssg.catalog.xml.subscribe.CatalogSubscribeXml;
import io.netty.channel.Channel;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipRequest;
import io.pkts.packet.sip.header.ContentTypeHeader;
import io.pkts.packet.sip.header.FromHeader;
import io.pkts.packet.sip.header.SipHeader;
import io.pkts.packet.sip.header.ViaHeader;
import io.sipstack.netty.codec.sip.Connection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

*/
/**
 * @author: yuhl Created on 10:26 2017/9/4 0004
 *//*

public class UACMessage {

    private final SimpleSipStack stack;

    @Autowired
    ChannelProvider sipClientProvider;

    private final FromHeader fromHeader = FromHeader.with().user("34030000002000000606")
            .host("192.168.30.174").port(5060).build();

    public UACMessage(final SimpleSipStack stack) {
        this.stack = stack;
    }

    public void send() throws Exception {
        final String host = "192.168.30.241";
        final int port = 7100;
        final Connection connection = this.stack.connect(host, port);
        this.fromHeader.setParameter(Buffers.wrap("tag"), FromHeader.generateTag());
        final ViaHeader viaHeader = ViaHeader.with().host(host).port(port).branch(ViaHeader.generateBranch())
                .transportUDP().build();
        final SipRequest request = SipRequest.request(Buffers.wrap("MESSAGE"),
                "sip:34020000002000000022@192.168.30.241:7100")
                .from(UACMessage.this.fromHeader).via(viaHeader).build();
        request.setHeader(SipHeader.frame(Buffers.wrap(ContentTypeHeader.frame(Buffers.wrap(
                "Application/MANSCDP+xml")).toString())));
        CatalogSubscribeXml xml = new CatalogSubscribeXml();
        xml.setCmdType("Catalog");
        xml.setDeviceId("34020000002000000022");
        xml.setSn(1);

        JAXBContext context = JAXBContext.newInstance(CatalogSubscribeXml.class);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(xml, writer);
        request.setPayload(Buffers.wrap(writer.toString()));
        System.out.println(request.toString());
        Channel sipClient = sipClientProvider.createIfAbsent(AddressUtils.from("192.168.30.174", 5060));
        SipUtils.send(sipClient, AddressUtils.from("192.168.30.241", 7100), request);
        connection.send(request);
    }

    public static void main(String[] args) throws Exception {

        final String host = "192.168.30.241";
        final int port = 7100;
        final String transport = "udp";

        final UACHandler handler = new UACHandler();
        final SimpleSipStack stack = new SimpleSipStack(handler, host, port);
        final UACMessage uac = new UACMessage(stack);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.err.println("ok, sending");
                    uac.send();
                } catch (final Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
        stack.run();
    }

}
*/
