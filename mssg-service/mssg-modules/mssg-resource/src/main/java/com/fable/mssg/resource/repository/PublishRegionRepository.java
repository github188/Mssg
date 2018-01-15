package com.fable.mssg.resource.repository;

import com.fable.mssg.domain.resmanager.PublishRegion;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 发布关系dao层
 * @author  xiejk 2017/9/30
 */
public interface PublishRegionRepository extends GenericJpaRepository<PublishRegion,String> ,JpaSpecificationExecutor<PublishRegion> {



}
