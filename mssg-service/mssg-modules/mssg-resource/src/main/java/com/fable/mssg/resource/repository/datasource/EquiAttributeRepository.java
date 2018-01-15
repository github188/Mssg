package com.fable.mssg.resource.repository.datasource;


import com.fable.mssg.domain.dsmanager.EquipAttributeBean;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 设备属性dao层
 * @author xiejk 2017/11/14
 */
public interface EquiAttributeRepository extends GenericJpaRepository<EquipAttributeBean,String> ,JpaSpecificationExecutor<EquipAttributeBean> {

    /**
     * 根据设备编码查询 设备属性
     * @param sbbm
     * @return
     */
    List<EquipAttributeBean> findAllBySbbm(String sbbm);


}
