package com.fable.mssg.proxy.sip.util;

import com.fable.mssg.bean.AuthInfo;
import com.fable.mssg.catalog.xml.query.RealControlXml;
import com.fable.mssg.service.datasource.DataSourceAuthService;
import gov.nist.javax.sdp.TimeDescriptionImpl;
import gov.nist.javax.sdp.fields.TimeField;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipResponse;
import io.pkts.packet.sip.XmlContent;
import io.pkts.packet.sip.address.impl.SipURIImpl;
import io.pkts.packet.sip.header.impl.SipHeaderImpl;
import lombok.SneakyThrows;

import javax.sdp.SessionDescription;
import java.util.Vector;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
public class AuthUtil {
    //没有权限，构造返回sip信令
    @SneakyThrows
    public static SipResponse getSipResponse(SipMessage sipMessage){
        return sipMessage.createResponse(401);
    }

    //调用接口，查询权限
    @SneakyThrows
    public static AuthInfo getAuthInfo(SipMessage message, String deviceId, boolean isMaster, DataSourceAuthService dataSourceAuthService){
        String contentType = message.getContentTypeHeader() == null ? null:message.getContentTypeHeader().getContentSubType().toString();
        SipHeaderImpl userAgentHeader = (SipHeaderImpl) message.getHeader("User-Agent");
        Boolean thirdMedia = userAgentHeader != null && userAgentHeader.getValue().toString().contains("fablesoft") ? false : true;//第三方平台user-agent不是fablesoft
        SessionDescription sdp;
        String operateType;
        AuthInfo authInfo = null;
        if(!isMaster){
            if(contentType != null){
                String sipId = ((SipURIImpl) message.getFromHeader().getAddress().getURI()).getUser().toString();
                if(contentType.toLowerCase().contains("sdp")){
                    sdp = (SessionDescription) message.getContent();
                    operateType = sdp.getSessionName().getValue().toLowerCase();
                    switch (operateType){
                        case "play"://实时
                            authInfo = dataSourceAuthService.isAuth(sipId,deviceId,1,System.currentTimeMillis(),null,null,thirdMedia);
                            authInfo.setPlayType(1);
                            break;
                        case "playback"://历史
                            Vector vector = sdp.getTimeDescriptions(true);
                            TimeField timeField = (TimeField) ((TimeDescriptionImpl)vector.get(0)).getTime();
                            long startTime = timeField.getStartTime() * 1000;//秒转毫秒
                            long endTime = timeField.getStopTime() * 1000;
                            authInfo = dataSourceAuthService.isAuth(sipId,deviceId,2,null,startTime,endTime,thirdMedia);
                            authInfo.setPlayType(2);
                            break;
                        case "download"://历史下载
                            vector = sdp.getTimeDescriptions(true);
                            timeField = (TimeField)((TimeDescriptionImpl)vector.get(0)).getTime();
                            startTime = timeField.getStartTime() * 1000; //秒转毫秒
                            endTime = timeField.getStopTime() * 1000;
                            authInfo = dataSourceAuthService.isAuth(sipId,deviceId,4,null,startTime,endTime,thirdMedia);
                            authInfo.setPlayType(3);
                            break;
                    }
                }else if(message.getRawContent().toString().toLowerCase().contains("control")){//代表实时控制
                    XmlContent xmlContent = (XmlContent) message.getContent();
                    RealControlXml realControlXml = xmlContent.get(RealControlXml.class);
                    String cmdType = realControlXml.getCmdType();
                    deviceId = realControlXml.getDeviceId();
                    authInfo = dataSourceAuthService.isAuth(sipId,deviceId,3,null,null,null,thirdMedia);
                    authInfo.setPlayType(4);
                }else{
                    authInfo = dataSourceAuthService.isAuth(sipId,deviceId,10,null,null,null,thirdMedia);
                }
            }
        }
        return authInfo;
    }
}
