package com.fable.mssg.service.onlinelog;


import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.dsmanager.DeviceVist;
import com.fable.mssg.vo.VDeviceVist;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

/**
 * description  设备访问明细接口
 *
 * @author xiejk 2017/10/26
 */
public interface DeviceVisitService {

    //新建设备访问记录  根据id 查询设备名称   根据userid  查询 在线记录id
    DeviceVist addDeviceVist(String dsId, int opType, String userId);


    //更新访问时长
    DeviceVist updateVistLong(String id, Long vistLong);

    DeviceVist updateVistLong(DeviceVist deviceVist);

    //根据onlineLogId 进行分页访问
    Page<DeviceVist> findPageByOnlineLogid(String onlineLogId,Integer page,Integer size);

    List<DeviceVist> findAllByOnlineLogId(String onlineLogId);


}
