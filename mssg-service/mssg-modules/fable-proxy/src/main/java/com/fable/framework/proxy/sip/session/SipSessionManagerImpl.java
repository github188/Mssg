package com.fable.framework.proxy.sip.session;

import com.fable.framework.proxy.sip.Address;
import com.fable.framework.proxy.sip.DeviceInfo;
import com.fable.framework.proxy.sip.DeviceManager;
import com.fable.framework.proxy.sip.media.MediaService;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.sip.util.SnapshotUtils;
import gov.nist.javax.sdp.MediaDescriptionImpl;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.address.SipURI;
import io.pkts.packet.sip.header.ContactHeader;
import io.pkts.packet.sip.header.ContentTypeHeader;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.sdp.Origin;
import javax.sdp.SessionDescription;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

/**
 * .
 *
 * @author stormning 2017/11/8
 * @since 1.3.0
 */
@Slf4j
public class SipSessionManagerImpl implements SipSessionManager, InitializingBean {

    private static final String SIP_REQ_CACHE = "_sip_req";
    private static final String SIP_RESP_CACHE = "_sip_resp";

    private static final String ATTR_SETUP = "setup";
    private static final String ATTR_CONNECTION = "connection";
    private static final String CONNECTION_NEW = "new";

    private Cache requestCache;

    private Cache responseCache;

    private Cache dsCodeAndMediaportCache;

    private Cache dsCodeCountCache;

    @Setter
    private CacheManager cacheManager;

    @Setter
    private DeviceManager deviceManager;

    @Setter
    private MediaService mediaService;

    @Override
    @SuppressWarnings("unchecked")
    public SipSession getSession(String sessionId) {
        SipSession sipSession = getSessionFromCache(sessionId, requestCache, true);
        return sipSession == null ? getSessionFromCache(sessionId, responseCache, false) : sipSession;
    }

    private SipSession getSessionFromCache(String sessionId, Cache cache, boolean request) {
        Cache.ValueWrapper rw = cache.get(sessionId);
        return rw == null ? null : new SipSession((SipSessionKeeper) rw.get(), request);
    }

    @Override
    @SneakyThrows
    public SipSession newSession(SipMessageContext messageContext) {
        SipMessage message = messageContext.getSipMessage();
        InetSocketAddress clientAddress = messageContext.getClientAddress();
        InetSocketAddress remoteAddress = messageContext.getRemoteAddress();
        InetSocketAddress contactAddress = messageContext.getContactAddress();
        InetSocketAddress backServerAddress = messageContext.getBackServerAddress();
        SnapshotCreator snapshotCreator = new SnapshotCreator(
                message,
                UUID.randomUUID().toString(),
                clientAddress,
                remoteAddress,
                contactAddress,
                null,
                backServerAddress
        );
        MessageSnapshot rs = snapshotCreator.getSnapshot();
        SessionDescription sdp = snapshotCreator.getSdp();

        RSnapshot rSnapshot = new RSnapshot(rs);
        //RTP over tcp
        //a=setup: active/passive
        //a=connection: new
        if (sdp != null) {
            MediaDescriptionImpl md = (MediaDescriptionImpl) sdp.getMediaDescriptions(true).get(0);
            String setup = md.getAttribute(ATTR_SETUP);
            if (setup != null) {
                rSnapshot.setSetup(Setup.valueOf(setup));
                String tcpConnection = md.getAttribute(ATTR_CONNECTION);
                if (StringUtils.equals(tcpConnection, CONNECTION_NEW)) {
                    rSnapshot.setNewConnection(true);
                }
            }
        }

        //create and save session
        SipSessionKeeper builder = new SipSessionKeeper();
        builder.setRequestSnapshot(rSnapshot);
        builder.setResponseSnapshot(new ReSnapshot(rSnapshot));

        SipSession session = new SipSession(builder, true);

        SessionAttributes attributes = SessionAttributeHolder.getSessionAttributes();
        if (attributes != null) {
            //save extra attributes
            for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
                session.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        return save(session);
    }

    public SipSession save(SipSession session) {
        SipSessionKeeper raw = session.getRaw();
        requestCache.put(raw.getRequestSnapshot().getCallId().getInput(), raw);
        responseCache.put(raw.getRequestSnapshot().getCallId().getOutput(), raw);
        log.info("Session saved: {}", session);
        return session;
    }

    @Override
    public ReSnapshot updateResponseSnapshot(SipSession session, SipMessageContext messageContext) {
        SipSessionKeeper raw = session.getRaw();
        RSnapshot rs = raw.getRequestSnapshot();
        SnapshotCreator snapshotCreator = new SnapshotCreator(
                messageContext.getSipMessage(),
                rs.getCallId().getInput(),
                messageContext.getClientAddress(),
                rs.getContactAddress().getInput().get(),
                //response contact = request to
                messageContext.getContactAddress(),
                rs.getFromDeviceId().getInput(),
                null
        );
        MessageSnapshot s = snapshotCreator.getSnapshot();
        ReSnapshot snapshot = new ReSnapshot(s);
        raw.setResponseSnapshot(snapshot);
        save(session);
        if (s.getMediaPort() != null) {
            mediaService.createMediaChannels(raw);
            dsCodeAndMediaportCache.putIfAbsent(rs.getToDeviceId().getInput(), rs.getMediaPort().getOutput());
            String dsCode = rs.getToDeviceId().getInput();
            int count = dsCodeCountCache.get(dsCode) == null ? 1 : ((int) dsCodeCountCache.get(dsCode).get()) + 1;
            dsCodeCountCache.putIfAbsent(rs.getToDeviceId().getInput(), count);
        }
        return snapshot;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.requestCache = cacheManager.getCache(SIP_REQ_CACHE);
        this.responseCache = cacheManager.getCache(SIP_RESP_CACHE);
        this.dsCodeAndMediaportCache = cacheManager.getCache("dscodeandmediaport");
        this.dsCodeCountCache = cacheManager.getCache("count");
    }

    private class SnapshotCreator {
        private String callId;
        private SipMessage message;
        private InetSocketAddress clientAddress;
        private InetSocketAddress remoteAddress;
        private InetSocketAddress contactAddress;
        private MessageSnapshot snapshot;
        private SessionDescription sdp;
        private boolean isRequest;
        private InetSocketAddress backServerAddress;

        public SnapshotCreator(
                SipMessage message,
                String callId,
                InetSocketAddress clientAddress,
                InetSocketAddress remoteAddress,
                InetSocketAddress contactAddress,
                String inputFromDeviceID,
                InetSocketAddress backServerAddress) {
            this.callId = callId;
            this.message = message;
            this.clientAddress = clientAddress;
            this.remoteAddress = remoteAddress;
            this.contactAddress = contactAddress;
            this.isRequest = message.isRequest();
            this.backServerAddress = backServerAddress;
            init(inputFromDeviceID);
        }

        public MessageSnapshot getSnapshot() {
            return snapshot;
        }

        public SessionDescription getSdp() {
            return sdp;
        }

        //      应答中的 From 头域必须和请求中的 From 头域相等。
//      应答中的 Call-ID 头域必须和请求中的 Call-ID 头域相等。
//      应答中的 Cseq 头域必须和请求中的 Cseq 头域相等。
//      应答中的 Via 头域必须和请求中的 Via 头域相等，而且顺序也必须相等。
//      如果请求中包含了 To tag，那么应答中的 To 头域必须和请求中的 To 头域相等。
        @SneakyThrows
        private void init(String inputFromDeviceID) {
            snapshot = new MessageSnapshot();
            String contactHost = contactAddress.getAddress().getHostAddress();
            //callId
            snapshot.setCallId(MappedProp.newProp(SnapshotUtils.getCallId(message), callId));

            InetSocketAddress fromAddress = isRequest ? this.backServerAddress : remoteAddress;
            //Via
            snapshot.setViaHost(MappedProp.newProp(message.getViaHeader().getHost().toString(), fromAddress.getAddress().getHostAddress()));
            snapshot.setViaPort(MappedProp.newProp(message.getViaHeader().getPort(), fromAddress.getPort()));
            //FROM
            SipURI fromSipURI = (SipURI) message.getFromHeader().getAddress().getURI();
            snapshot.setFromHost(
                    MappedProp.newProp(
                            fromSipURI.getHost().toString(),
                            fromAddress.getAddress().getHostAddress()
                    )
            );
            snapshot.setFromPort(
                    MappedProp.newProp(
                            fromSipURI.getPort(),
                            fromAddress.getPort()
                    )
            );
            //Contact
            SipURI contactURI = null;
            ContactHeader contactHeader = message.getContactHeader();
            if (contactHeader != null) {
                contactURI = (SipURI) contactHeader.getAddress().getURI();
            }
            if (contactURI != null) {
                snapshot.setContactAddress(MappedProp.newProp(
                        new Address(contactURI.getHost().toString(), contactURI.getPort()),
                        new Address(contactAddress.getAddress().getHostAddress(), contactAddress.getPort())
                ));
            } else {
                snapshot.setContactAddress(MappedProp.newProp(
                        new Address(fromSipURI.getHost().toString(), fromSipURI.getPort()),
                        new Address(contactAddress.getAddress().getHostAddress(), contactAddress.getPort())
                ));
            }
            //TO
            SipURI toSipURI = (SipURI) message.getToHeader().getAddress().getURI();
            String toDeviceId = toSipURI.getUser().toString();
            String toHost = toSipURI.getHost().toString();
            int toPort = toSipURI.getPort();

            //response to == request to
            InetSocketAddress sipToAddress = isRequest ? contactAddress : this.clientAddress;

            snapshot.setToHost(MappedProp.newProp(toHost, sipToAddress.getAddress().getHostAddress()));
            snapshot.setToPort(MappedProp.newProp(toPort, sipToAddress.getPort()));
            String fromDeviceId = fromSipURI.getUser().toString();
            if (remoteAddress == null) {
                DeviceInfo deviceInfo = deviceManager.getDeviceInfo(toDeviceId);
                remoteAddress = deviceInfo.getAddress();
                snapshot.setToDeviceId(MappedProp.newProp(toDeviceId, deviceInfo.getDeviceId()));
                snapshot.setFromDeviceId(MappedProp.newProp(fromDeviceId, deviceInfo.getSipId()));
                if (contactURI != null) {
                    snapshot.setContactDeviceId(MappedProp.newProp(contactURI.getUser().toString(), deviceInfo.getSipId()));
                }
            } else {
                snapshot.setToDeviceId(MappedProp.same(toDeviceId));
                if (StringUtils.isNoneBlank(fromDeviceId)) {
                    if (isRequest) {
                        snapshot.setFromDeviceId(MappedProp.same(fromDeviceId));
                    } else {
                        snapshot.setFromDeviceId(MappedProp.newProp(fromDeviceId, inputFromDeviceID));
                    }
                }
                if (contactURI != null) {
                    snapshot.setContactDeviceId(MappedProp.same(contactURI.getUser().toString()));
                }
            }
            snapshot.setToAddress(remoteAddress);
            if (message.hasContent()) {
                ContentTypeHeader contentTypeHeader = message.getContentTypeHeader();
                if (contentTypeHeader.isSDP()) {
                    //sdp
                    sdp = (SessionDescription) message.getContent();
                    //o=
                    Origin origin = sdp.getOrigin();
                    snapshot.setOriginalHost(MappedProp.newProp(origin.getAddress(), contactHost));
                    //m=
                    int mediaPort = SipUtils.getFirstMedia(sdp).getMediaPort();
                    Address mediaOutput = mediaService.acquire(isRequest);
                    snapshot.setMediaPort(MappedProp.newProp(mediaPort, mediaOutput.getPort()));
                    //c=
                    snapshot.setConnectionHost(MappedProp.newProp(sdp.getConnection().getAddress(), mediaOutput.getHost()));
                    //fromDeviceId=
                    snapshot.setSessionName(MappedProp.same(sdp.getSessionName().getValue()));
                }
            }
        }
    }
}