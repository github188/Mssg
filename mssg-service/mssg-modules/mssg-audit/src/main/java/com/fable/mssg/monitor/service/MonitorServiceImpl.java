package com.fable.mssg.monitor.service;

import com.fable.framework.core.config.Address;
import com.fable.framework.core.support.remoting.Exporter;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.domain.businesslog.BusinessLog;
import com.fable.mssg.businesslog.repository.BusinessLogRepository;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.monitor.vo.*;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.service.resource.ResourceService;
import com.fable.mssg.service.slave.SlaveMonitorService;
import com.fable.mssg.service.slave.SlaveNetworkService;
import com.fable.mssg.service.user.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description
 * @Author wangmeng 2017/10/25
 */
@Service
@Slf4j
@Exporter(interfaces = SlaveMonitorService.class)
public class MonitorServiceImpl implements MonitorService, SlaveMonitorService {

    @Autowired
    ResSubscribeService resSubscribeService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    BusinessLogRepository businessLogRepository;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    CompanyService companyService;

    @Value("${com.fable.mssg.proxy.sip.remoteServer}")
    Address slaveAddress;

    @Value("${master.in.netInterface.name}")
    String netInterfaceName;

    @Autowired
    ServiceRegistry serviceRegistry;

    SlaveNetworkService slaveNetworkService;

    SimpleDateFormat simpleDateFormat;

    @PostConstruct
    public void init() {

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            slaveNetworkService = serviceRegistry.lookup(slaveAddress.getHost(), slaveAddress.getPort(), true, SlaveNetworkService.class);
        } catch (Exception e) {
            log.error("访问slave失败", e);
        }
    }


    @Override
    public SystemSurvey getSystemSurvey() {

        SystemSurvey systemSurvey = new SystemSurvey();
        systemSurvey.setResource(resourceService.countPublish());
        systemSurvey.setSubscribe(resSubscribeService.count());
        BusinessLog businessLog = new BusinessLog();
        businessLog.setOperateType(BusinessLog.REALTIME);
        systemSurvey.setRealtime(businessLogRepository.count(Example.of(businessLog)));
        businessLog.setOperateType(BusinessLog.HISTORY);
        systemSurvey.setHistory(businessLogRepository.count(Example.of(businessLog)));
        SysUser sysUser = new SysUser();
        sysUser.setDeleteFlag(0); //未被删除
        Company company = new Company();
        company.setComType(2);
        sysUser.setComId(company);
        systemSurvey.setTotalUser(sysUserService.count(sysUser));

        sysUser.setLoginState(1);//且在线
        systemSurvey.setLoginUser(sysUserService.count(sysUser));
        systemSurvey.setSharedUnit(companyService.countShared());
        systemSurvey.setOnlineUnit(companyService.countOnline());

        return systemSurvey;
    }

    @Override
    public Traffic getTraffic() {
        Sigar sigar = new Sigar();
        Traffic traffic = new Traffic();
        try {
            long start = System.currentTimeMillis();
            long inStartRx = sigar.getNetInterfaceStat(netInterfaceName).getRxBytes();
            long outStartRx = slaveNetworkService.getRxBytes();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("", e);
            }
            long inEndRx = sigar.getNetInterfaceStat(netInterfaceName).getRxBytes();
            long outEndRx = slaveNetworkService.getRxBytes();
            long end = System.currentTimeMillis();
            long inSpeed = (inEndRx - inStartRx) * 1000 / (end - start);
            long outSpeed = (outEndRx - outStartRx) * 1000 / (end - start);
            log.debug("inSpeed:" + inSpeed + "B/s outSpeed:" + outSpeed + "B/s");
            traffic.setIn(inSpeed);
            traffic.setOut(outSpeed);
        } catch (SigarException e) {
            log.error("获取网卡信息失败", e);
        }

        return traffic;
    }

    @Override
    public List<TopSubCompany> getTopSubCompany(Integer days) {

        Calendar calendar = getCalendar(days);
        Timestamp approvalTime = new Timestamp(calendar.getTimeInMillis());
        List comIdCounts = companyService.findTopSubscribeCompany(approvalTime);
        List<TopSubCompany> topSubCompanies = new ArrayList<>(comIdCounts.size());
        for (Object obj : comIdCounts) {
            Object[] objs = (Object[]) obj;
            topSubCompanies.add(TopSubCompany.builder()
                    .companyId((String) objs[0])
                    .companyName((String) objs[1])
                    .code((String) objs[2])
                    .subCount(objs[3].toString())
                    .build());

        }
        return topSubCompanies;
    }


    @Override
    public List<TopResSurvey> getTopResource(Integer days, String type, Integer size) {
        Calendar calendar = getCalendar(days);
        List result;
        if (TYPE_SUBMIT.equals(type)) {
            result = resourceService.findTopSubRes(new Timestamp(calendar.getTimeInMillis()), size);
        } else if (TYPE_VIEW.equals(type)) {
            result = resourceService.findPopRes(new Timestamp(calendar.getTimeInMillis()), size);
        } else {
            throw new RuntimeException("类型错误");
        }
        List<TopResSurvey> topResSurveys = new ArrayList<>(result.size());
        for (Object obj : result) {
            Object[] objects = (Object[]) obj;
            topResSurveys.add(TopResSurvey.builder()
                    .resourceId((String) objects[0])
                    .resourceName((String) objects[1])
                    .subscribe(objects[2].toString())
                    .dsQty(objects[3].toString())
                    .realtime(objects[4].toString())
                    .history(objects[5].toString())
                    .createTime(simpleDateFormat.format(objects[6]))
                    .build());

        }
        return topResSurveys;
    }

    @Override
    public List<TopViewUser> getTopViewUser(Integer days, Integer size) {
        Calendar calendar = getCalendar(days);
        List result = sysUserService.findTopViewUser(new Timestamp(calendar.getTimeInMillis()), size);
        List<TopViewUser> topViewUsers = new ArrayList<>(result.size());
        for (Object obj : result) {
            Object[] objects = (Object[]) obj;
            Integer realtime = Integer.parseInt(objects[2].toString());
            Integer history = Integer.parseInt(objects[3].toString());
            topViewUsers.add(TopViewUser.builder()
                    .username((String) objects[0])
                    .companyName((String) objects[1])
                    .realtime(realtime)
                    .history(history)
                    .preview(realtime+history)
                    .build());
        }


        return topViewUsers;
    }

    /**
     * slave 端获得系统概况
     *
     * @return
     */
    @Override
    public Map getSysSurvey(String comId) {

        String resource = resourceService.countPublish() + "";
        ResSubscribe resSubscribe = new ResSubscribe();
        Company company = new Company(comId);
        resSubscribe.setComId(company);
        String subscribe = resSubscribeService.count(resSubscribe) + "";
        BusinessLog businessLog = new BusinessLog();
        businessLog.setOperateType(BusinessLog.REALTIME);
        businessLog.setCompanyId(company);
        String realtime = businessLogRepository.count(Example.of(businessLog)) + "";
        businessLog.setOperateType(BusinessLog.HISTORY);
        String history = businessLogRepository.count(Example.of(businessLog)) + "";

        Map sysSurvey = new HashMap();
        sysSurvey.put("resource", resource);
        sysSurvey.put("subscribe", subscribe);
        sysSurvey.put("realtime", realtime);
        sysSurvey.put("history", history);
        return sysSurvey;
    }

    @Override
    public List<Map> getPopResource(Integer days, Integer size, String comId) {
        Calendar calendar = getCalendar(days);
        List result = resourceService.findPopResByComId(new Timestamp(calendar.getTimeInMillis()), size, comId);
        List<Map> topResSurveys = new ArrayList<>(result.size());
        for (Object obj : result) {
            Object[] objects = (Object[]) obj;
            Map map = new HashMap(objects.length);
            map.put("resourceId", objects[0]);
            map.put("resourceName", objects[1]);
            map.put("submit", objects[2]);
            map.put("dsQty", objects[3]);
            map.put("realtime", objects[4]);
            map.put("history", objects[5]);
            map.put("createTime", simpleDateFormat.format(objects[6]));
            topResSurveys.add(map);

        }

        return topResSurveys;
    }

    @Override
    public List<Map> getTopViewUser(Integer days, Integer size, String comId) {
        Calendar calendar = getCalendar(days);
        List result = sysUserService.findTopViewUserByComId(new Timestamp(calendar.getTimeInMillis()), comId, size);
        List<Map> topViewUsers = new ArrayList<>(result.size());
        for (Object obj : result) {
            Object[] objects = (Object[]) obj;
            Map map = new HashMap(objects.length);
            map.put("username", objects[0]);
            map.put("companyName", objects[1]);
            map.put("realtime", objects[2]);
            map.put("history", objects[3]);
            map.put("viewDevice", objects[4]);
            topViewUsers.add(map);
        }

        return topViewUsers;
    }

    private Calendar getCalendar(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -(days - 1));
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}
