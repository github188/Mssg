package com.fable.mssg.proxy.media.filter;

import com.fable.framework.proxy.MessageFilter;
import com.fable.mssg.catalog.utils.SipConstant;
import com.fable.mssg.service.equipment.SecurityService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
@Slf4j
public class RtpFilter implements MessageFilter<DatagramPacket> {
    private int i = 0;

    private SecurityService securityService;

    public RtpFilter(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public boolean doFilter(ChannelHandlerContext ctx, DatagramPacket msg) {
        if (msg != null && securityService.isCheckRtpFormat("0004")) {
            String mediaFormat = securityService.getMediaFormat(msg.sender().getAddress().getHostAddress());
            String mediaType;
            if (i != 0) {
                byte[] bytes = new byte[msg.content().readableBytes()];
                msg.content().getBytes(0, bytes);
                // 获取媒体流格式-RTP流第二个字节的后七位标识流媒体格式:98-H264 96-PS 97-MPEG4
                String hexStr = SipConstant.bytesToHexString(bytes); // byte[]转为十六进制
                // 截取十六进制的第三和第四位转为十进制
                String hexResult = hexStr.substring(2, 4);
                // 转为十进制
                int format = SipConstant.binaryToDecimal(SipConstant.hexString2binaryString(hexResult));
                if (format == 98) { // H.264封装
                    mediaType = "H.264";
                } else if (format == 97) { // MPEG4封装
                    mediaType = "MPEG4";
                } else if (format == 99) { // SVAC封装
                    mediaType = "SVAC";
                } else { // PS封装还要具体区分视频流类型
                    mediaType = "PS";
                }
                if (mediaFormat != null && mediaFormat.contains(mediaType)) { // 媒体流格式校验通过
                    //log.info("媒体格式通过安全验证！！！");
                    return true;
                } else { // 媒体流格式校验不通过
                    log.info("媒体格式未通过安全验证！！！");
                    return false;
                }
            }
        }
        i++;
        return true;
    }
}
