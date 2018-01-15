package com.fable.mssg.service.businesslog;

import com.fable.mssg.bean.businesslog.BusinessLogBean;
import com.fable.mssg.domain.businesslog.BusinessLog;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

public interface BusinessLogService {

    Page<BusinessLog> findByTypeAndPage(int page, int size, int type);

    List<BusinessLog> findAllRealTime();

    BusinessLogBean save(BusinessLogBean log);

    void save(BusinessLog businessLog);

    //根据用户和访问设备查询实时记录
    BusinessLog findRealTimeByUserAndDevice(String userId,String deviceId);

    Page<BusinessLog> findByConditions(int page, int size, Timestamp startTime, Timestamp endTime, String unit);

    void setToHistory(String id);
}
