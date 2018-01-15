package com.fable.mssg.businesslog.service;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.domain.businesslog.BusinessLog;
import com.fable.mssg.businesslog.repository.BusinessLogRepository;

import com.fable.mssg.bean.businesslog.BusinessLogBean;
import com.fable.mssg.businesslog.vo.VBusinessLog;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.service.businesslog.BusinessLogService;
import com.fable.mssg.service.datasource.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.*;

import java.sql.Timestamp;

@Service
@Exporter(interfaces = BusinessLogService.class)
public class BusinessLogServiceImpl implements BusinessLogService {

    @Autowired
    private BusinessLogRepository businessLogRepository;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public Page<BusinessLog> findByTypeAndPage(int page, int size, int type) {
        Page resultPage = businessLogRepository.findAll((root, query, cb) -> {
            Path<Integer> visitType = root.get("visitType");
            Predicate predicate = cb.equal(visitType, type);
            return predicate;
        }, new PageRequest(page, size, new Sort(Sort.Direction.DESC, "startTime")));
        return resultPage;
    }

    @Override
    public List<BusinessLog> findAllRealTime() {

        return businessLogRepository.findAll((root, query, cb) -> cb.equal(root.get("visitType"), 1),
                new Sort(Sort.Direction.DESC, "startTime"));
    }

    @Override
    public BusinessLogBean save(BusinessLogBean logbean) {

        //sip模块无法使用实体类的问题
        BusinessLog log = new BusinessLog();
        log.setVisitType(logbean.getVisitType());
        log.setCompanyId(new Company(logbean.getCompanyId()));
        DataSource dataSource = dataSourceService.findById(logbean.getDeviceId());
        if(dataSource!=null){
            log.setDeviceId(dataSource);
            log.setDsName(dataSource.getDsName());
        }
        log.setVisitUser(new SysUser(logbean.getVisitUser()));
        log.setSegmentTime(logbean.getSegmentTime());
        log.setStartTime(logbean.getStartTime());
        log.setOperateType(logbean.getOperateType());

        log = businessLogRepository.save(log);
        logbean.setId(log.getId());
        return logbean;
    }

    @Override
    public void save(BusinessLog businessLog) {
        businessLogRepository.save(businessLog);
    }

    @Override
    public BusinessLog findRealTimeByUserAndDevice(String user, String deviceId) {

        BusinessLog businessLog = new BusinessLog();
        businessLog.setVisitUser(new SysUser(user));
        DataSource dataSource = new DataSource();
        dataSource.setId(deviceId);
        businessLog.setDeviceId(dataSource);
        businessLog.setVisitType(1);
        return businessLogRepository.findOne(Example.of(businessLog));

    }

    @Override
    public Page<BusinessLog> findByConditions(int page, int size, Timestamp startTime, Timestamp endTime, String unit) {
        return businessLogRepository.findAll((root, query, cb) -> {
            //Predicate predicate = cb.equal(root.get("visitType"), 2);
            Predicate predicate = cb.and();
            if (null != startTime) {
                predicate = cb.and(predicate, cb.greaterThan(root.get("startTime"), startTime));

            }
            if (null != endTime) {
                predicate = cb.and(predicate, cb.lessThan(root.get("startTime"), endTime));
            }
            if (null != unit) {
                predicate = cb.and(predicate, cb.and(predicate,
                        cb.like(root.get("companyId").get("name"), "%" + unit + "%")));
            }

            return predicate;
        }, new PageRequest(page - 1, size, new Sort(Sort.Direction.DESC, "startTime")));

    }

    @Override
    public void setToHistory(String id) {
        BusinessLog log = businessLogRepository.findById(id);
        log.setVisitType(2);
        businessLogRepository.save(log);
    }

    /**
     * 实时查询量大 需要用到查询
     * @return
     */
/**
 @Override public List<VBusinessLog> findRealTime() {

 List result = businessLogRepository.find(BusinessLog.REALTIME);
 List<VBusinessLog> vBusinessLogs = new ArrayList<>();
 for (Object obj : result) {
 Object[] objs = (Object[]) obj;
 VBusinessLog vBusinessLog = VBusinessLog.builder()
 .companyID((String) objs[1])
 .deviceID((String) objs[0])
 .visitUser((String)objs[5])
 .segmentTime(objs[4] == null ? null : new BigDecimal((BigInteger) objs[4]))
 .startTime(objs[3] == null ? "UnKnow Time" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Timestamp) objs[3]))
 .build();
 switch ((int)objs[2]){
 case 1:
 vBusinessLog.setOperateType("实时视频");
 break;
 case 2:
 vBusinessLog.setOperateType("历史视频");
 break;
 case 3:
 vBusinessLog.setOperateType("实时控制");
 break;

 }
 vBusinessLogs.add(vBusinessLog);
 }
 return vBusinessLogs;
 }
 */

}
