package com.fable.mssg.proxy.sip.listener;

import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.framework.proxy.session.Session;
import com.fable.framework.proxy.sip.SipEvent;
import com.fable.framework.proxy.sip.session.SipSession;
import com.fable.framework.proxy.sip.session.SipSessionKeeper;
import com.fable.mssg.bean.AuthInfo;
import com.fable.mssg.bean.businesslog.BusinessLogBean;
import com.fable.mssg.domain.dsmanager.DeviceVist;
import com.fable.mssg.proxy.sip.config.SipProxyProperties;
import com.fable.mssg.proxy.sip.filter.SipAuthenticationFilter;
import com.fable.mssg.service.businesslog.BusinessLogService;
import com.fable.mssg.service.onlinelog.DeviceVisitService;
import com.fable.mssg.service.onlinelog.OnLineLogService;
import io.pkts.packet.sip.SipMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RecordInviteLog {
    @Autowired
    private SipProxyProperties proxyProperties;

    @Autowired
    private ServiceRegistry sr;

    private static Map<String, DeviceVist> deviceLogMap = new ConcurrentHashMap<String, DeviceVist>();

    private static List<String> inviteLogList = new ArrayList<>();

    @Value("${serverself.ssl.enabled}")
    private boolean isSSL;

    @EventListener
    @Async
    public void receiveSipMessageEvent(SipEvent sipEvent) {
        boolean master = proxyProperties.isMaster(); // 1-master 0-slave
        if (!master) { // slave记录日志
            SipSession session = sipEvent.getSipSession();
            SipSessionKeeper sessionObject = session.getRaw();
            AuthInfo authInfo = (AuthInfo) session.getAttribute(SipAuthenticationFilter.AUTH_INFO_KEY);
            SipMessage sipMessage = sipEvent.getSipMessage();
            BusinessLogBean businessLog = new BusinessLogBean();
            businessLog.setCompanyId(authInfo.getComId());
            businessLog.setDeviceId(sessionObject.getRequestSnapshot().getToDeviceId().getInput());
            businessLog.setStartTime(new Timestamp(System.currentTimeMillis()));
            businessLog.setVisitUser(authInfo.getUserId());
            String remoteServerIp = proxyProperties.getRemoteServer().getHost();
            int remoteServerPort = proxyProperties.getRemoteServer().getPort();
            String cSeq = sipMessage.getCSeqHeader().getMethod().toString().toLowerCase();
            try {
                String inputCallId = sessionObject.getRequestSnapshot().getCallId().getInput();
                if (cSeq.contains("invite")) {
                    if (!inviteLogList.contains(inputCallId) && authInfo.getPlayType() != 3) {
                        businessLog.setOperateType(authInfo.getPlayType());
                        //业务审计日志
                        sr.lookup(remoteServerIp, remoteServerPort, isSSL, BusinessLogService.class).save(businessLog);
                        //用户在线访问日志
                        sr.lookup(remoteServerIp, remoteServerPort, isSSL, OnLineLogService.class).addvistEquipCount(authInfo.getUserId());
                        //设备访问日志
                        DeviceVist deviceVist = sr.lookup(remoteServerIp, remoteServerPort, isSSL, DeviceVisitService.class).addDeviceVist(
                                sessionObject.getRequestSnapshot().getToDeviceId().getInput(), authInfo.getPlayType(), authInfo.getUserId());
                        deviceLogMap.put(inputCallId, deviceVist);
                        inviteLogList.add(inputCallId);
                    }
                }
                if (cSeq.contains("bye")) {
                    DeviceVist deviceVist = deviceLogMap.get(inputCallId);
                    if (deviceVist != null) {
                        Timestamp timestamp = deviceVist.getVistTime();
                        long visitTime = (System.currentTimeMillis() - timestamp.getTime()) / 1000;
                        sr.lookup(remoteServerIp, remoteServerPort, isSSL, DeviceVisitService.class).updateVistLong(deviceVist.getId(), visitTime);
                        deviceLogMap.remove(inputCallId);
                        inviteLogList.remove(inputCallId);
                    }
                }
                if (cSeq.contains("message")) {
                    if (authInfo.getPlayType() != 0 && authInfo.getPlayType() != 2) {
                        businessLog.setOperateType(authInfo.getPlayType());
                        businessLog = sr.lookup(remoteServerIp, remoteServerPort, isSSL, BusinessLogService.class).save(businessLog);
                    }
                }
            } catch (Exception e) {
                log.error("Record Business Log Error：" + e.getMessage());
            }
        }
    }
}
