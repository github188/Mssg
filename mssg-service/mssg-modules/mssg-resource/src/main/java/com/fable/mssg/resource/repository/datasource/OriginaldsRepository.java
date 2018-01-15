package com.fable.mssg.resource.repository.datasource;


import com.fable.mssg.domain.dsmanager.Originalds;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 原始数据源dao层
 * @author xiejk 2017/9/30
 */
public interface OriginaldsRepository extends GenericJpaRepository<Originalds,String> ,JpaSpecificationExecutor<Originalds> {


    List<Originalds> findAllByMediaDeviceId(String mediadeviceid);


}
