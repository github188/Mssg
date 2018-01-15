package com.fable.framework.proxy.sip.util;

import com.fable.framework.proxy.sip.Address;
import com.fable.framework.proxy.sip.session.MessageSnapshot;
import com.fable.framework.proxy.sip.session.RSnapshot;
import com.fable.framework.proxy.sip.session.ReSnapshot;
import com.fable.framework.proxy.sip.session.SipMessageWrapper;
import com.fable.framework.proxy.util.NotifyXml;
import com.fable.framework.proxy.util.QueryXml;
import com.fable.framework.proxy.util.RecordItemXml;
import com.fable.framework.proxy.util.ResponseXml;
import com.fable.mssg.catalog.xml.query.RealControlXml;
import gov.nist.javax.sdp.fields.URIField;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipRequest;
import io.pkts.packet.sip.SipResponse;
import io.pkts.packet.sip.XmlContent;
import io.pkts.packet.sip.address.SipURI;
import io.pkts.packet.sip.header.*;
import io.pkts.packet.sip.impl.SipRequestLine;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import javax.sdp.Media;
import javax.sdp.SessionDescription;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * .
 *
 * @author stormning 2017/11/9
 * @since 1.3.0
 */
public class SnapshotUtils {


    public static String getCallId(SipMessage message) {
        return message.getCallIDHeader().getCallId().toString();
    }

    public static SipMessage createResponseOutput(SipMessage message, ReSnapshot rs) {
        return createCommonOutput(message, rs);
    }

    public static SipMessage createRequestOutput(SipMessageWrapper message, RSnapshot snapshot) {
        SipMessage raw = message.getRaw();
        SipMessage reqOut = createCommonOutput(raw, snapshot);
        //change request uri
        if (raw instanceof SipRequest) {
            SipRequest request = (SipRequest) reqOut;
            SipURI uri = (SipURI) request.getRequestUri();
            InetSocketAddress toAddress = snapshot.getToAddress();
            uri.setPort(toAddress.getPort());
            uri.setHost(Buffers.wrap(toAddress.getAddress().getHostAddress()));
            reqOut.setInitialLine(new SipRequestLine(message.getMethod(), uri));
        }
        return reqOut;
    }

    @SneakyThrows
    private static SipMessage createCommonOutput(SipMessage message, MessageSnapshot ms) {
        //CallId
        message.setHeader(SipUtils.createCallIdHeader(ms.getCallId().getOutput()));
        //Contact
        ContactHeader contactHeader = message.getContactHeader();
        String contactDeviceId = "";
        if (contactHeader != null) {
            contactDeviceId = ms.getContactDeviceId().getOutput();
            Address contact = ms.getContactAddress().getOutput();
            message.setHeader(SipUtils.replaceContactHeader(contactHeader, contact.getHost(), contact.getPort(), contactDeviceId));
        }
        //Via 域/ip+port
        ViaHeader viaHeader = message.getViaHeader();
        if (viaHeader != null) {
            if (message.hasContent() && message.getRawContent().toString().toLowerCase().contains("mediastatus")){
                message.setHeader(SipUtils.replaceViaHeader(viaHeader, ms.getViaHost().getOutput(), ms.getToPort().getOutput()));
            } else {
                message.setHeader(SipUtils.replaceViaHeader(viaHeader, ms.getViaHost().getOutput(), ms.getViaPort().getOutput()));
            }
        }
        //From
        FromHeader fromHeader = message.getFromHeader();
        if (fromHeader != null) {
            if (message.hasContent() && message.getRawContent().toString().toLowerCase().contains("mediastatus")){
                message.setHeader(SipUtils.replaceFromHeader(fromHeader, ms.getFromHost().getOutput(), ms.getToPort().getOutput(), ms.getFromDeviceId().getOutput()));
            } else {
                message.setHeader(SipUtils.replaceFromHeader(fromHeader, ms.getFromHost().getOutput(), ms.getFromPort().getOutput(), ms.getFromDeviceId().getOutput()));
            }
        }
        String deviceId = ms.getToDeviceId().getOutput();
        //To
        ToHeader toHeader = message.getToHeader();
        if (toHeader != null) {
            int toPort = ms.getToPort() == null ? 0 : ms.getToPort().getOutput();
            message.setHeader(SipUtils.replaceToHeader(
                    toHeader,
                    deviceId,
                    ms.getToHost().getOutput(),
                    toPort));
        }
        SipHeader subjectHeader = message.getHeader("Subject");
        if (subjectHeader != null) {
            message.setHeader(SipHeader.frame(Buffers.wrap("Subject: " + subjectHeader.getValue().toString().replace(ms.getToDeviceId().getInput(), ms.getToDeviceId().getOutput()).replace(ms.getFromDeviceId().getInput(), ms.getFromDeviceId().getOutput()))));
        }
        //替换o行和c行的IP，以及媒体端口
        if (message.hasContent()) {
            ContentTypeHeader header = message.getContentTypeHeader();
            if (header.isSDP()) {
                SessionDescription sdp = (SessionDescription) message.getContent();
                //o=
                sdp.getOrigin().setAddress(ms.getOriginalHost().getOutput());

                if (StringUtils.isNotBlank(contactDeviceId)) {
                    sdp.getOrigin().setUsername(contactDeviceId);
                }
                //c=
                sdp.getConnection().setAddress(ms.getConnectionHost().getOutput());
                //m= port
                Media firstMedia = SipUtils.getFirstMedia(sdp);
                firstMedia.setMediaPort(ms.getMediaPort().getOutput());
                URIField uriField = (URIField) sdp.getURI();
                if (uriField != null) {
                    String uri = uriField.getURI();
                    uri = uri.replace(ms.getToDeviceId().getInput(), ms.getToDeviceId().getOutput());
                    uriField.setURI(uri);
                }
                //reset payload
                Buffer payload = Buffers.wrap(sdp.toString());
                message.setPayload(payload);
            } else if (message.isMessage()) {
                String rawContent = message.getRawContent().toString().toLowerCase();
                XmlContent xmlContent = (XmlContent) message.getContent();
                StringWriter writer = new StringWriter();
                JAXBContext context;
                Marshaller marshaller;
                if (rawContent.contains("control")) {
                    RealControlXml realControlXml = xmlContent.get(RealControlXml.class);
                    realControlXml.setDeviceId(deviceId);
                    context = JAXBContext.newInstance(RealControlXml.class);
                    marshaller = context.createMarshaller();
                    marshaller.marshal(realControlXml, writer);
                } else if (rawContent.contains("query")) {
                    QueryXml queryXml = xmlContent.get(QueryXml.class);
                    queryXml.setDeviceId(deviceId);
                    queryXml.setRecorderId(deviceId);
                    queryXml.setFilePath(deviceId);
                    context = JAXBContext.newInstance(QueryXml.class);
                    marshaller = context.createMarshaller();
                    marshaller.marshal(queryXml, writer);
                } else if (rawContent.contains("response")) {
                    ResponseXml responseXml = xmlContent.get(ResponseXml.class);
                    responseXml.setDeviceId(deviceId);
                    List<RecordItemXml> list = responseXml.getResponseRecordListXml().getItemXmlList();
                    for (RecordItemXml recordItemXml : list) {
                        recordItemXml.setDeviceId(deviceId);
                        recordItemXml.setFilePath(deviceId);
                        recordItemXml.setRecordedId(deviceId);
                    }
                    responseXml.getResponseRecordListXml().setNum(list.size());
                    context = JAXBContext.newInstance(ResponseXml.class);
                    marshaller = context.createMarshaller();
                    marshaller.marshal(responseXml, writer);
                } else if (rawContent.contains("mediastatus")){
                    NotifyXml notifyXml = xmlContent.get(NotifyXml.class);
                    notifyXml.setDeviceId(deviceId);
                    context = JAXBContext.newInstance(NotifyXml.class);
                    marshaller = context.createMarshaller();
                    marshaller.marshal(notifyXml, writer);
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
}