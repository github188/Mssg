package com.fable.mssg.resource.repository.onlinelog;


import com.fable.mssg.domain.dsmanager.DeviceVist;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 设备访问记录dao层
 * @author  xiejk 2017/10/26
 */
public interface DeviceVistRepository extends GenericJpaRepository<DeviceVist,String> ,JpaSpecificationExecutor<DeviceVist> {

    //根据onlineLogid 查询
     List<DeviceVist> findByOnlineLogId(String onlineLogId);

}
