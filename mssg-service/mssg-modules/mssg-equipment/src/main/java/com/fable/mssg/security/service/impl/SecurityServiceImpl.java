package com.fable.mssg.security.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.framework.proxy.ChannelManager;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.util.AddressUtils;
import com.fable.mssg.catalog.config.MasterConfigProperties;
import com.fable.mssg.catalog.utils.*;
import com.fable.mssg.domain.equipment.MediaInfoBean;
import com.fable.mssg.domain.securityconfig.SecurityConfig;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.service.equipment.ConfigPolicyService;
import com.fable.mssg.service.equipment.SecurityService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.utils.MD5Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramPacket;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipResponse;
import io.pkts.packet.sip.header.CallIdHeader;
import io.pkts.packet.sip.header.FromHeader;
import io.pkts.packet.sip.header.SipHeader;
import io.pkts.packet.sip.header.ToHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

/**
 * @author: yuhl Created on 14:39 2017/11/15 0015
 */
@Exporter(interfaces = SecurityService.class)
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    public ConfigPolicyService configPolicyService;

    @Autowired
    public MasterConfigProperties masterConfigProperties;

    @Autowired
    ServiceRegistry registry;

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    public SysUserService sysUserService;

    /**
     * 信令格式校验
     *
     * @param deviceId
     * @param method
     * @param secCode
     * @return
     */
    @Override
    public boolean checkSignalFormat(String deviceId, String method, String secCode) {
        SecurityConfig configInfo = null; // 安全配置信息
        MediaInfoBean mediaInfo = null; // 媒体源信息
        try {
            configInfo = configPolicyService.getConfigInfoByCode(secCode);
            mediaInfo = configPolicyService.getMediaByDeviceId(deviceId);
        } catch (Exception e) {
            log.error("Query security config or media info error {}", e);
            return false;
        }
        /**
         * 信令格式和媒体源认证，不存在信令的安全配置或者不存在对应的媒体源，认证不通过
         * 存在信令的安全配置并且存在对应的媒体源，则对信令格式进行验证
         */
        if (null != configInfo && configInfo.getStatus() == 1) { // 开启认证
            if (null == mediaInfo || null == mediaInfo.getId()|| "".equals(mediaInfo.getId())) { // 无媒体源，不过
                return false;
            } else { // 存在媒体源，校验信令类型
                String signalFormat = mediaInfo.getSingalFormat(); // 信令格式
                if (signalFormat.contains(method)) { // 允许的信令类型
                    return true;
                } else { // 信令类型不允许
                    return false;
                }
            }
        } else { // 不开启认证，全过
            return true;
        }
    }

    /**
     * 媒体流格式校验
     *
     * @param ipAddress
     * @return
     */
    @Override
    public String getMediaFormat(String ipAddress) {
        MediaInfoBean mediaInfo = configPolicyService.getMediaByIp(ipAddress); // 根据IP查询媒体源信息
        if (null == mediaInfo) { // 不存在对应的媒体源
            return null;
        } else { // 媒体源校验
            return mediaInfo.getMediaFormat();
        }
    }

    /**
     * 客户端数字摘要认证
     *
     * @param sipMessage
     * @return
     */
    @Override
    public boolean digitalDigest(SipMessage sipMessage, String secCode) {
        SecurityConfig configInfo = null;
        if ("BYE".equals(sipMessage.getMethod().toString())) {
            return true;
        }
        try {
            configInfo = configPolicyService.getConfigInfoByCode(secCode);
        } catch (Exception e) {
            log.error("Query security config error {}", e);
            return false;
        }
        if (null == configInfo || configInfo.getStatus() == 0) { // 数字摘要认证未开启，不校验
            return true;
        }
        MasterConfigProperties.Authorize authorize = masterConfigProperties.getAuthorize();
        MasterConfigProperties.Slave slave = masterConfigProperties.getSlave();
        long duration = authorize.getDuration(); // 允许的时间差值
        String clientHost = sipMessage.getViaHeader().getHost().toString(); // 客户端ip
        int clientPort = sipMessage.getViaHeader().getPort(); // 客户端端口
        InetSocketAddress slaveAddress = AddressUtils.from(slave.getHost(), slave.getPort());
        InetSocketAddress clientAddress = AddressUtils.from(clientHost, clientPort);
        String current = SipConstant.current(); // 获取当前时间
        final FromHeader fromHeader = sipMessage.getFromHeader(); // from头域
        final ToHeader toHeader = sipMessage.getToHeader(); // to头域
        final SipHeader date = sipMessage.getHeader("Date"); // date头域
        final SipHeader note = sipMessage.getHeader("Note");
        final CallIdHeader callIdHeader = sipMessage.getCallIDHeader(); // callId头域
        String time = date.getValue().toString(); // 发送SIP信令的时间

        long minus = SipConstant.minusCommonTime(current, time);
        if (minus > duration) { // 时间差不在允许范围内，数字摘要认证失败
            return false;
        }
        /**
         * 数字摘要认证过程
         */
        String fromValue = fromHeader.getAddress().getURI().toString(); // from头域值
        log.info("FromHeader Value ：{}", fromValue);
        String toValue = toHeader.getAddress().getURI().toString(); // to头域值
        log.info("ToHeader Value ：{}", toValue);
        String callId = callIdHeader.getCallId().toString(); // callId
        log.info("CallId Value ：{}", callId);
        String nonce = SipConstant.getMiddleStr(note.getValue().toString()); // nonce内容
        log.info("Nonce Value ：{}", nonce);
        log.info("Time Value : {}", time);
        String rawContent = sipMessage.getRawContent() == null ? "" : sipMessage.getRawContent().toString();
        log.info("Content Value : {}", rawContent.toString());
        String md5Value = MD5Utils.getMD5Value(fromValue + toValue + callId + time
                + "seed" + rawContent).toLowerCase();
        log.info("MD5 Value ：{}", md5Value);
        String base64Value = Base64Utils.encodeByBase64(md5Value); // 加密发送SIP信令内容
        log.info("Base64 Value : {}", base64Value);
        ChannelFuture future = channelManager.selectUdpChannel(null, slaveAddress, null);

        if (nonce.equals(base64Value)) { // 验证通过,发送返回结果信息
//            DateHeader dateHeader = DateHeader.create(current);
//            SipRequest request = sipMessage.toRequest();
//            String responseMd5 = Base64Utils.encodeByBase64(MD5Utils.getMD5Value(fromValue + toValue + callId +
//                    current + "seed" + rawContent).toLowerCase());
//            request.setHeader(dateHeader);
//            request.setHeader(SipHeader.frame(Buffers.wrap("Note: " + "Digest nonce=\"" + responseMd5
//                    + "\" algorithm=MD5")));
//            SipUtils.send(future, clientAddress, request);// 发送响应信令
            return true;
        } else { // 数字摘要认证不通过,发送401
            SipResponse response = sipMessage.createResponse(401);
            SipUtils.send(future, clientAddress, response);
            return false;
        }
    }

    /**
     * 加密处理
     *
     * @param packet
     * @param secCode
     * @return
     */
    @Override
    public byte[] isEncryption(DefaultAddressedEnvelope packet, String secCode) {
        MasterConfigProperties.Encryption encryption = masterConfigProperties.getEncryption();
        SecurityConfig configInfo = null;
        try {
            configInfo = configPolicyService.getConfigInfoByCode(secCode);
        } catch (Exception e) {
            log.error("Query security config error {}", e);
            return null;
        }
        byte[] bytes = new byte[((ByteBuf)packet.content()).readableBytes()];
        ((ByteBuf)packet.content()).getBytes(0, bytes);
        if (null == configInfo || configInfo.getStatus() == 0) { // 加密未开启
            return bytes;
        } else {
            long secPolicy = configInfo.getSecPolicy(); // 加密策略-3:DES,4:3DES,5:AES
            if (secPolicy == 3) { // DES
                bytes = DESUtils.encodeByDES(bytes, encryption.getDesKey());
            } else if (secPolicy == 4) { // 3DES
                bytes = TripleDESUtils.encodeByTripleDES(bytes, encryption.getTriDesKey());
            } else if (secPolicy == 5) { // AES
                bytes = AESUtils.encodeByAES(bytes, encryption.getAesKey());
            }
            return bytes;
        }
    }

    /**
     * 解密处理
     *
     * @param packet
     * @param secCode
     * @return
     */
    @Override
    public byte[] isDecode(DatagramPacket packet, String secCode) {
        MasterConfigProperties.Encryption encryption = masterConfigProperties.getEncryption();
        SecurityConfig configInfo = null;
        try {
            configInfo = configPolicyService.getConfigInfoByCode(secCode);
        } catch (Exception e) {
            log.error("Query security config error {}", e);
            return null;
        }
        byte[] bytes = new byte[packet.content().readableBytes()];
        packet.content().getBytes(0, bytes);
        if (null == configInfo || configInfo.getStatus() == 0) { // 加密未开启
            return bytes;
        } else {
            long secPolicy = configInfo.getSecPolicy(); // 加密策略-3:DES,4:3DES,5:AES
            if (secPolicy == 3) { // DES
                bytes = DESUtils.decodeByDES(bytes, encryption.getDesKey());
            } else if (secPolicy == 4) { // 3DES
                bytes = TripleDESUtils.decodeByTriple(bytes, encryption.getTriDesKey());
            } else if (secPolicy == 5) { // AES
                bytes = AESUtils.decodeByAES(bytes, encryption.getAesKey());
            }
            return bytes;
        }
    }

    @Override
    public Boolean isCheckRtpFormat(String secCode) {
        SecurityConfig configInfo = configPolicyService.getConfigInfoByCode(secCode);
        if (null == configInfo || configInfo.getStatus() == 0) { // 不校验媒体流格式
            return false;
        }
        return true;
    }

    /**
     * 设备认证，不在媒体源范围，认证不通过
     *
     * @param deviceId
     * @param secCode
     * @return
     */
    @Override
    public boolean checkEquipment(String deviceId, String secCode) {
        MediaInfoBean mediaInfo = null; // 媒体源信息
        SecurityConfig securityConfig = null; // 安全配置信息
        SysUser sysUser = null; // 用户信息
        try {
            mediaInfo = configPolicyService.getMediaByDeviceId(deviceId);
            securityConfig = configPolicyService.getConfigInfoByCode(secCode);
            sysUser = sysUserService.findBySipId(deviceId);
        } catch (Exception e) {
            log.error("Query media info error {}", e);
            return false;
        }
        /**
         * 存在媒体源，则设备认证通过，不存在则认证失败
         */
        if (null != securityConfig && securityConfig.getStatus() == 1) { // 开启认证
            if ((mediaInfo == null || "".equals(mediaInfo.getId()) || mediaInfo.getId() == null)
                    && (sysUser == null || sysUser.getId() == null || "".equals(sysUser.getId()))) {
                return false;
            } else { // 存在配置和媒体源信息
                return true;
            }
        } else { // 不开启认证则通过
            return true;
        }
    }

    /**
     * 数据完整性验证
     * 根据信息的md5值进行完整性验证
     * @param sipMessage
     * @param md5Value
     * @return
     */
    @Override
    public boolean checkDataIntegrity(SipMessage sipMessage, String md5Value) {
        String integrity = MD5Utils.getMD5Value(sipMessage.toBuffer().toString());
        if (md5Value.equals(integrity)) { // 数据完整
            return true;
        } else {
            return false;
        }
    }

}
