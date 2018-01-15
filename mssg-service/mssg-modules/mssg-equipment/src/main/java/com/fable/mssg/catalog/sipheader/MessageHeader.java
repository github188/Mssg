package com.fable.mssg.catalog.sipheader;

import com.fable.mssg.domain.mediainfo.MediaInfo;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipRequest;
import io.pkts.packet.sip.header.*;

/**
 * @author: yuhl Created on 19:45 2017/11/14 0014
 */
public class MessageHeader {

    public String slaveHost; // slave地址

    public int slavePort; // slave端口

    public String slaveId; // slave设备名

    public MessageHeader(String host, int port, String sipId) {
        this.slaveHost = host;
        this.slavePort = port;
        this.slaveId = sipId;
    }

    /**
     * 生成响应头域信息
     *
     * @param mediaInfo
     * @param content
     * @return
     */
    public SipMessage generateMessageHeader(MediaInfo mediaInfo, String content) {
//        String slaveHost = slave.getHost(); // slave的IP
//        int slavePort = slave.getPort(); // slave信令端口
//        String slaveId = slave.getSlaveId(); // slave设备id
//        String platformId = SipUtils.getSipURI(sipMessage.getFromHeader()).getUser().toString(); // 平台sipId
//        int platformPort = SipUtils.getSipURI(sipMessage.getFromHeader()).getPort(); // 平台端口
//        String platformHost = SipUtils.getSipURI(sipMessage.getFromHeader()).getHost().toString(); // 平台ip
        String mediaId = mediaInfo.getDeviceId(); // 媒体源id
        int sessionPort = mediaInfo.getSessionPort(); // 媒体源端口
        String mediaHost = mediaInfo.getIpAddress(); // 媒体源ip
        final FromHeader fromHeader = FromHeader.with().user(slaveId).host(slaveHost).port(slavePort)
                .parameter("tag", FromHeader.generateTag().toString()).build();
        final ViaHeader viaHeader = ViaHeader.with().host(slaveHost).port(slavePort)
                .branch(ViaHeader.generateBranch()).transportUDP().build();
        final ToHeader toHeader = ToHeader.with().user(mediaId).host(mediaHost).port(sessionPort).build();
        final SipRequest request = SipRequest.request(Buffers.wrap("MESSAGE"),
                "sip:" + mediaId + "@" + mediaHost + ":" + sessionPort)
                .from(fromHeader).via(viaHeader).to(toHeader).build();
        request.setHeader(SipHeader.frame(Buffers.wrap(ContentTypeHeader.frame(Buffers.wrap(
                "Application/MANSCDP+xml")).toString())));
        request.setPayload(Buffers.wrap(content));
        return request;
    }

}
