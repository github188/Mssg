package com.fable.framework.proxy.sip.session;

import com.fable.framework.proxy.session.Session;
import io.pkts.buffer.Buffer;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipParseException;
import io.pkts.packet.sip.SipRequest;
import io.pkts.packet.sip.SipResponse;
import io.pkts.packet.sip.header.*;
import io.pkts.packet.sip.impl.SipInitialLine;

import java.util.List;

/**
 * .
 *
 * @author stormning 2017/12/7
 * @since 1.3.0
 */
public class SipMessageWrapper implements SipMessage {

    private final SipMessage message;
    private final SipSession session;

    public SipMessageWrapper(SipMessage message, SipSession session) {
        this.message = message;
        this.session = session;
    }

    public SipSession getSession() {
        return session;
    }

    public SipMessage getRaw(){
        return message;
    }

    @Override
    public Buffer getInitialLine() {
        return message.getInitialLine();
    }

    @Override
    public void setInitialLine(SipInitialLine initialLine) {
        message.setInitialLine(initialLine);
    }

    @Override
    public SipRequest toRequest() throws ClassCastException {
        return message.toRequest();
    }

    @Override
    public SipResponse toResponse() throws ClassCastException {
        return message.toResponse();
    }

    @Override
    public SipResponse createResponse(int responseCode) throws SipParseException, ClassCastException {
        return message.createResponse(responseCode);
    }

    @Override
    public boolean isResponse() {
        return message.isResponse();
    }

    @Override
    public boolean isRequest() {
        return session == null || session.isRequest();
    }

    @Override
    public Object getContent() throws SipParseException {
        return message.getContent();
    }

    @Override
    public Buffer getRawContent() {
        return message.getRawContent();
    }

    @Override
    public boolean hasContent() {
        return message.hasContent();
    }

    @Override
    public Buffer getMethod() throws SipParseException {
        return message.getMethod();
    }

    @Override
    public SipHeader getHeader(Buffer headerName) throws SipParseException {
        return message.getHeader(headerName);
    }

    @Override
    public SipHeader getHeader(String headerName) throws SipParseException {
        return message.getHeader(headerName);
    }

    @Override
    public void addHeader(SipHeader header) throws SipParseException {
        message.addHeader(header);
    }

    @Override
    public void addHeaderFirst(SipHeader header) throws SipParseException {
        message.addHeaderFirst(header);
    }

    @Override
    public SipHeader popHeader(Buffer headerNme) throws SipParseException {
        return message.popHeader(headerNme);
    }

    @Override
    public void setHeader(SipHeader header) throws SipParseException {
        message.setHeader(header);
    }

    @Override
    public FromHeader getFromHeader() throws SipParseException {
        return message.getFromHeader();
    }

    @Override
    public ToHeader getToHeader() throws SipParseException {
        return message.getToHeader();
    }

    @Override
    public ViaHeader getViaHeader() throws SipParseException {
        return message.getViaHeader();
    }

    @Override
    public List<ViaHeader> getViaHeaders() throws SipParseException {
        return message.getViaHeaders();
    }

    @Override
    public MaxForwardsHeader getMaxForwards() throws SipParseException {
        return message.getMaxForwards();
    }

    @Override
    public RecordRouteHeader getRecordRouteHeader() throws SipParseException {
        return message.getRecordRouteHeader();
    }

    @Override
    public List<RecordRouteHeader> getRecordRouteHeaders() throws SipParseException {
        return message.getRecordRouteHeaders();
    }

    @Override
    public RouteHeader getRouteHeader() throws SipParseException {
        return message.getRouteHeader();
    }

    @Override
    public List<RouteHeader> getRouteHeaders() throws SipParseException {
        return message.getRouteHeaders();
    }

    @Override
    public ExpiresHeader getExpiresHeader() throws SipParseException {
        return message.getExpiresHeader();
    }

    @Override
    public ContactHeader getContactHeader() throws SipParseException {
        return message.getContactHeader();
    }

    @Override
    public ContentTypeHeader getContentTypeHeader() throws SipParseException {
        return message.getContentTypeHeader();
    }

    @Override
    public CallIdHeader getCallIDHeader() throws SipParseException {
        return message.getCallIDHeader();
    }

    @Override
    public CSeqHeader getCSeqHeader() throws SipParseException {
        return message.getCSeqHeader();
    }

    @Override
    public boolean isInvite() throws SipParseException {
        return message.isInvite();
    }

    @Override
    public boolean isRegister() throws SipParseException {
        return message.isRegister();
    }

    @Override
    public boolean isBye() throws SipParseException {
        return message.isBye();
    }

    @Override
    public boolean isAck() throws SipParseException {
        return message.isAck();
    }

    @Override
    public boolean isOptions() throws SipParseException {
        return message.isOptions();
    }

    @Override
    public boolean isMessage() throws SipParseException {
        return message.isMessage();
    }

    @Override
    public boolean isInfo() throws SipParseException {
        return message.isInfo();
    }

    @Override
    public boolean isCancel() throws SipParseException {
        return message.isCancel();
    }

    @Override
    public boolean isInitial() throws SipParseException {
        return message.isInitial();
    }

    @Override
    public void verify() {
        message.verify();
    }

    @Override
    public Buffer toBuffer() {
        return message.toBuffer();
    }

    @Override
    public SipMessage clone() {
        return message.clone();
    }

    @Override
    public void setPayload(Buffer payload) {
        message.setPayload(payload);
    }
}
