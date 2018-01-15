package com.fable.mssg.service.equipment;

import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramPacket;
import io.pkts.packet.sip.SipMessage;

/**
 * @author: yuhl Created on 14:31 2017/11/15 0015
 */
public interface SecurityService {

    /**
     * 信令格式校验
     *
     * @param deviceId
     * @param method
     * @param secCode
     * @return
     */
    boolean checkSignalFormat(String deviceId, String method, String secCode);

    /**
     * 媒体流格式校验
     *
     * @param ipAddress
     * @return
     */
    String getMediaFormat(String ipAddress);

    /**
     * 数字摘要认证
     *
     * @param sipMessage
     * @param secCode
     * @return
     */
    boolean digitalDigest(SipMessage sipMessage, String secCode);

    /**
     * 加密处理
     *
     * @param packet
     * @param secCode
     * @return
     */
    byte[] isEncryption(DefaultAddressedEnvelope packet, String secCode);

    /**
     * 解密处理
     *
     * @param packet
     * @param secCode
     * @return
     */
    byte[] isDecode(DatagramPacket packet, String secCode);

    /**
     * 媒体格式验证开关
     * @param secCode
     * @return
     */
    Boolean isCheckRtpFormat(String secCode);

    /**
     * 设备认证，不在媒体源范围，认证不通过
     * @param deviceId
     * @param secCode
     * @return
     */
    boolean checkEquipment(String deviceId, String secCode);

    /**
     * 数据完整性验证
     * 根据信息的md5值进行完整性验证
     * @param sipMessage
     * @param md5Value
     * @return
     */
    boolean checkDataIntegrity(SipMessage sipMessage, String md5Value);
}
