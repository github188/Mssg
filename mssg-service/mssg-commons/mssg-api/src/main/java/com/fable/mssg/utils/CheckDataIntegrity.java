package com.fable.mssg.utils;

import io.netty.channel.socket.DatagramPacket;
import io.pkts.packet.sip.SipMessage;

/**
 * @author: yuhl Created on 13:31 2017/11/17 0017 校验数据完整性
 */
public class CheckDataIntegrity {

    /**
     * 通过MD5校验SIP信令数据完整性
     * @param sipMessage SIP信令内容
     * @param code 约定的规则
     * @return
     */
    public static boolean checkSipIntegrity(SipMessage sipMessage, String code) {
        String md5Value = MD5Utils.getMD5Value(sipMessage.toBuffer().toString() + code); // 加密
        String sipContent = "";
        if (md5Value.equals(sipContent)) { // 验证通过
            return true;
        } else {
            return false;
        }
    }

    /**
     * 校验RTP媒体流的完整性
     * @param data RTP流信息
     * @param code 约定规则
     * @return
     */
    public static boolean checkRTPIntegrity(DatagramPacket data, String code) {
        String md5Value = MD5Utils.getMD5Value(new String(data.content().array()) + code);
        String rtpContent = "";
        if (md5Value.equals(rtpContent)) {
            return true;
        } else {
            return false;
        }
    }

}
