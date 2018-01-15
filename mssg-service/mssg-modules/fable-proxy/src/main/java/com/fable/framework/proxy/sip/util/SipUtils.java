package com.fable.framework.proxy.sip.util;

import com.fable.framework.proxy.util.QueryXml;
import com.fable.framework.proxy.util.RecordItemXml;
import com.fable.framework.proxy.util.ResponseXml;
import com.fable.mssg.catalog.xml.query.RealControlXml;
import gov.nist.javax.sdp.fields.URIField;
import gov.nist.javax.sdp.parser.ParserFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.DatagramPacket;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.XmlContent;
import io.pkts.packet.sip.address.SipURI;
import io.pkts.packet.sip.header.*;
import io.pkts.packet.sip.header.impl.AddressParametersHeaderImpl;
import io.pkts.packet.sip.header.impl.CallIdHeaderImpl;
import io.pkts.packet.sip.impl.SipParser;
import io.pkts.sdp.SdpException;
import io.pkts.sdp.UnknownParser;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SessionDescription;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * .
 *
 * @author stormning 2017/9/2
 * @since 1.3.0
 */
public class SipUtils {

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static Media getFirstMedia(SessionDescription sdp) {
        final Vector<MediaDescription> mds = sdp.getMediaDescriptions(false);
        if (mds != null) {
            return mds.iterator().next().getMedia();
        }
        throw new SdpException("No media line found or media line error");
    }

    public static FromHeader replaceFromHeader(FromHeader header, String host, int port, String newSipId) {
        FromHeader.Builder builder = FromHeader.with();
        if (StringUtils.isNotBlank(newSipId)) {
            builder.user(newSipId);
        }
        if (header.getTag() != null) {
            builder.parameter(Buffers.wrap("tag"), header.getTag());
        }
        return builder.host(host).port(port).build();
    }

    public static FromHeader replaceSipFromHeader(FromHeader header, String newSipId) {
        return FromHeader.with().user(newSipId).parameter(Buffers.wrap("tag"), header.getTag()).host(newSipId.substring(0, 10)).build();
    }

    public static CallIdHeader createCallIdHeader(String callId) {
        return new CallIdHeaderImpl(false, Buffers.wrap(callId));
    }

    public static ViaHeader replaceViaHeader(ViaHeader header, String host, int port) {
        return ViaHeader.with().host(host).port(port).transport(header.getTransport()).branch(header.getBranch()).build();
    }

    public static ToHeader replaceToHeader(ToHeader toHeader, String deviceId, String host, int port) {
        SipURI sipURI = getSipURI(toHeader);
        AddressParametersHeader.Builder<ToHeader> toHeaderBuilder = ToHeader.with().user(deviceId).host(host);
        if (sipURI.getPort() > 0) {
            toHeaderBuilder.port(port);
        }
        if (toHeader.getTag() != null) {
            toHeaderBuilder.parameter(AddressParametersHeaderImpl.TAG, toHeader.getTag());
        }
        return toHeaderBuilder.build();
    }

    @SneakyThrows
    public static ContactHeader replaceContactHeader(ContactHeader header, String host, int port, String user) {
        SipURI uri = getSipURI(header);
        AddressParametersHeader.Builder<ContactHeader> headerBuilder
                = ContactHeader.with().host(host).port(port);
        SipURI sipURI = (SipURI) header.getAddress().getURI();
        Buffer transportParam = sipURI.getTransportParam();
        if (transportParam != null) {
            headerBuilder.parameter(SipParser.TRANSPORT, transportParam);
        }
        headerBuilder.user(StringUtils.isBlank(user) ? uri.getUser() : Buffers.wrap(user));
        return headerBuilder.build();
    }

    public static SipURI getSipURI(AddressParametersHeader header) {
        if (header == null) {
            return null;
        } else {
            return (SipURI) header.getAddress().getURI();
        }
    }

    @SneakyThrows
    public static SipMessage replace(SipMessage message, String callId, String fromHost, int fromPort,
                                     String originalDsCode, String toHost, int toPort, int mediaPort, String originalToHost, int originalToPort
            , String contactIp, int contactPort, String connectionHost, String newDsCode, String originalSipId, String newSipId) {
        //替换消息头
        //源
        //CallId
        message.setHeader(createCallIdHeader(callId));
        //Contact ip+port
        ContactHeader contactHeader = message.getContactHeader();
        if (contactHeader != null) {
            message.setHeader(replaceContactHeader(contactHeader, contactIp, contactPort, newSipId));
        }
        //Via 域/ip+port
        ViaHeader viaHeader = message.getViaHeader();
        if (viaHeader != null) {
            message.setHeader(replaceViaHeader(viaHeader, fromHost, fromPort));
        }
        //From 域
//        if(message.isMessage()){
//        	FromHeader fromHeader = message.getFromHeader();
//            if (fromHeader != null) {
//                message.setHeader(replaceFromHeader(fromHeader, fromHost,fromPort));
//            }
//        }
        String address = null;
        FromHeader fromHeader = message.getFromHeader();
        if (fromHeader != null) {
            address = fromHeader.getAddress().toString();
        }
        if (address != null && (address.length() - address.replace(":", "").length()) > 1) {
            message.setHeader(replaceFromHeader(fromHeader, fromHost, fromPort, newSipId));
        } else {
            message.setHeader(replaceSipFromHeader(fromHeader, newSipId));
        }
        //目标
        //to 域
        ToHeader toHeader = message.getToHeader();
        if (toHeader != null) {
            message.setHeader(replaceToHeader(toHeader, newDsCode, originalToHost, originalToPort));
        }
        SipHeader subjectHeader = message.getHeader("Subject");
        if (subjectHeader != null) {
            message.setHeader(SipHeader.frame(Buffers.wrap("Subject: " + subjectHeader.getValue().toString().replace(originalDsCode, newDsCode).replace(originalSipId, newSipId))));
        }
        //替换o行和c行的IP，以及媒体端口
        if (message.hasContent()) {
            ContentTypeHeader header = message.getContentTypeHeader();
            if (header.isSDP()) {
                SessionDescription sdp = (SessionDescription) message.getContent();
                //o=
                sdp.getOrigin().setAddress(fromHost);
                sdp.getOrigin().setUsername(newSipId);
                //c=
                sdp.getConnection().setAddress(connectionHost);
                //m= port
                Media firstMedia = getFirstMedia(sdp);
                firstMedia.setMediaPort(mediaPort);
                URIField uriField = (URIField) sdp.getURI();
                if (uriField != null) {
                    String uri = uriField.getURI();
                    uri = uri.replace(originalDsCode, newDsCode);
                    uriField.setURI(uri);
                }
                //reset payload
                Buffer payload = Buffers.wrap(sdp.toString());
                message.setPayload(payload);
                //message.setHeader(SipHeader.frame(Buffers.wrap("Content-Length:   " + payload.getArray().length)));
            } else if (message.isMessage()) {
                String rawContent = message.getRawContent().toString().toLowerCase();
                XmlContent xmlContent = (XmlContent) message.getContent();
                StringWriter writer = new StringWriter();
                JAXBContext context;
                Marshaller marshaller;
                if (rawContent.contains("control")) {
                    RealControlXml realControlXml = xmlContent.get(RealControlXml.class);
                    realControlXml.setDeviceId(newDsCode);
                    context = JAXBContext.newInstance(RealControlXml.class);
                    marshaller = context.createMarshaller();
                    marshaller.marshal(realControlXml, writer);
                } else if (rawContent.contains("query")) {
                    QueryXml queryXml = xmlContent.get(QueryXml.class);
                    queryXml.setDeviceId(newDsCode);
                    queryXml.setRecorderId(newDsCode);
                    queryXml.setFilePath(newDsCode);
                    context = JAXBContext.newInstance(QueryXml.class);
                    marshaller = context.createMarshaller();
                    marshaller.marshal(queryXml, writer);
                } else if (rawContent.contains("response")) {
                    ResponseXml responseXml = xmlContent.get(ResponseXml.class);
                    responseXml.setDeviceId(newDsCode);
                    List<RecordItemXml> list = responseXml.getResponseRecordListXml().getItemXmlList();
                    for (RecordItemXml recordItemXml : list) {
                        recordItemXml.setDeviceId(newDsCode);
                        recordItemXml.setFilePath(newDsCode);
                        recordItemXml.setRecordedId(newDsCode);
                    }
                    responseXml.getResponseRecordListXml().setNum(list.size());
                    context = JAXBContext.newInstance(ResponseXml.class);
                    marshaller = context.createMarshaller();
                    marshaller.marshal(responseXml, writer);
                } else {
                    context = JAXBContext.newInstance(RealControlXml.class);
                    marshaller = context.createMarshaller();
                }
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                message.setPayload(Buffers.wrap(writer.toString()));
            }
        }
        return message;
    }

    @SneakyThrows
    public static void fixParserFactory() {
        String[] unknownFields = new String[]{"d", "f", "g", "h", "j", "l", "n", "q", "w", "x", "y"};
        Field parserTableField = ReflectionUtils.findField(ParserFactory.class, "parserTable");
        ReflectionUtils.makeAccessible(parserTableField);
        Hashtable parserTable = (Hashtable) parserTableField.get(null);
        for (String f : unknownFields) {
            parserTable.put(f, UnknownParser.class);
        }
    }

    public static byte[] encode(io.pkts.packet.sip.SipMessage msg) {
        Buffer buffer = msg.toBuffer();
        buffer.write(SipParser.CR);
        buffer.write(SipParser.LF);
        return buffer.getArray();
    }


    /**
     * support udp only
     *
     * @param sender
     * @param target
     * @param message
     */
    public static void send(Channel sender, InetSocketAddress target, io.pkts.packet.sip.SipMessage message) {
        sender.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(encode(message)), target));
    }

    public static void send(ChannelFuture sender, InetSocketAddress target, io.pkts.packet.sip.SipMessage message) {
        if (sender.isDone()) {
            send(sender.channel(), target, message);
        } else {
            sender.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    send(sender.channel(), target, message);
                }
            });
        }
    }

}
