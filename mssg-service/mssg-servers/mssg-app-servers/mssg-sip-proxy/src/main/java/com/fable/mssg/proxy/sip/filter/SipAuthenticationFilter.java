package com.fable.mssg.proxy.sip.filter;

import com.fable.framework.proxy.MessageFilter;
import com.fable.framework.proxy.session.Session;
import com.fable.framework.proxy.sip.session.SessionAttributeHolder;
import com.fable.framework.proxy.sip.session.SipSession;
import com.fable.framework.proxy.sip.session.SipSessionKeeper;
import com.fable.framework.proxy.sip.session.SipSessionManager;
import com.fable.framework.proxy.sip.util.SipUtils;
import com.fable.framework.proxy.sip.util.SnapshotUtils;
import com.fable.mssg.bean.AuthInfo;
import com.fable.mssg.proxy.sip.util.AuthUtil;
import com.fable.mssg.service.datasource.DataSourceAuthService;
import com.fable.mssg.service.equipment.SecurityService;
import io.netty.channel.ChannelHandlerContext;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.address.SipURI;
import io.sipstack.netty.codec.sip.SipMessageEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * .
 *
 * @author stormning 2017/11/27
 * @since 1.3.0
 */
@Slf4j
public class SipAuthenticationFilter implements MessageFilter<SipMessageEvent>, ApplicationContextAware {

    @Setter
    private SecurityService securityService;

    @Setter
    private DataSourceAuthService dataSourceAuthService;

    @Setter
    private boolean isMaster;

    private SipSessionManager sessionManager;

    @Setter
    private boolean isSslEnabled;

    public static String AUTH_INFO_KEY = "AUTH_INFO";

    @Override
    public boolean doFilter(ChannelHandlerContext ctx, SipMessageEvent event) {
        SipMessage message = event.getMessage();
        if(!securityService.checkEquipment(
                SipUtils.getSipURI(message.getFromHeader()).getUser().toString(),"0001")){
            event.getConnection().send(AuthUtil.getSipResponse(message));
            log.info("设备认证未通过！！！");
            return false;
        }
        if (!securityService.digitalDigest(message, "0005")) {
            event.getConnection().send(AuthUtil.getSipResponse(message));
            log.info("客户端校验未通过！！！");
            return false;
        }
        if (!message.toBuffer().toString().contains("fablesoft")) {//自己的客户端不做格式校验
            if (!securityService.checkSignalFormat(
                    SipUtils.getSipURI(message.getFromHeader()).getUser().toString(),
                    message.getMethod().toString(),
                    "0003"
            )) {
                event.getConnection().send(AuthUtil.getSipResponse(message));
                log.info("信令格式校验未通过！！！");
                return false;
            }
        }

        String callId = SnapshotUtils.getCallId(message);
        SipSession session = sessionManager.getSession(callId);
        AuthInfo authInfo;
        if (session == null) {
            SipURI toSipURI = SipUtils.getSipURI(message.getToHeader());
            //deviceId and toHost
            String deviceId = toSipURI.getUser().toString();
            authInfo = AuthUtil.getAuthInfo(message, deviceId, isMaster, dataSourceAuthService);
            SessionAttributeHolder.getSessionAttributes().put(AUTH_INFO_KEY, authInfo);
        } else {
            authInfo = (AuthInfo) session.getAttribute(AUTH_INFO_KEY);
        }

        if (!authInfo.isAuth()) {
            event.getConnection().send(AuthUtil.getSipResponse(message));
            log.info("用户没有权限进行该操作！！！");
            return false;
        }

        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.sessionManager = applicationContext.getBean(SipSessionManager.class);
    }
}
