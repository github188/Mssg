package com.fable.mssg.resource.service.onlinelog.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.datasource.repository.DataSourceRepository;
import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.dsmanager.DeviceVist;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.repository.onlinelog.DeviceVistRepository;
import com.fable.mssg.resource.repository.onlinelog.OnLineLogRepository;
import com.fable.mssg.service.onlinelog.DeviceVisitService;
import com.fable.mssg.vo.VDeviceVist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

/**
 * description 用户接口实现类
 * @author xiejk 2017/10/26
 */
@Service
@Exporter(interfaces = DeviceVisitService.class)
public class DeviceVisitServiceImpl implements DeviceVisitService {

    @Autowired
    DeviceVistRepository deviceVistRepository;

    @Autowired
    DataSourceRepository dataSourceRepository;
    @Autowired
    OnLineLogRepository onLineLogRepository;

    /**
     *
     * @param dsId  设备id
     * @param opType
     * @param userId
     * @return
     */
    @Override
    public DeviceVist addDeviceVist(String dsId,int opType,String userId) {
        DeviceVist dv = new DeviceVist();
        DataSource ds = dataSourceRepository.findOne(dsId);
        dv.setDeviceName(ds.getDsName());
        dv.setDeviceCode(dsId);
        dv.setOp_type(opType);//操作类型·
        dv.setVistTime(new Timestamp(System.currentTimeMillis()));//设置访问时间
        OnLineLog online = onLineLogRepository.findByUserAndLogoutTime(new SysUser(userId), null);
        dv.setOnlineLogId(online.getId());//设置在线日志id
        dv.setVistLong(0L);//设置访问时长
        dv.setUserId(userId);//设置userid
        return deviceVistRepository.save(dv);
    }


    @Override
    public DeviceVist updateVistLong(String id,Long vistLong) {
        DeviceVist dv = deviceVistRepository.findOne(id);
        dv.setVistLong(vistLong);
        return deviceVistRepository.save(dv);
    }

    @Override
    public DeviceVist updateVistLong(DeviceVist deviceVist) {

        return deviceVistRepository.save(deviceVist);
    }

    //分页查询
    @Override
    public Page<DeviceVist> findPageByOnlineLogid(String onlineLogId,Integer page,Integer size) {
       return  deviceVistRepository.findAll(
               //匿名类使用lambda表达式标识
               (root, query, cb) -> {
                   //加上对应catalogId的条件
                   return cb.equal(root.get("onlineLogId"), onlineLogId);
               }
               , new PageRequest(page - 1, size, new Sort(Sort.Direction.DESC, "vistTime")));
    }


    @Override
    public List<DeviceVist> findAllByOnlineLogId(String onlineLogId) {
        return deviceVistRepository.findByOnlineLogId(onlineLogId);
    }
}
