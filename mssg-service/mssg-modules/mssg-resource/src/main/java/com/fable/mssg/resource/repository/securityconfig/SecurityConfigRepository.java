package com.fable.mssg.resource.repository.securityconfig;


import com.fable.mssg.domain.securityconfig.SecurityConfig;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 安全配置dao层
 * @author  xiejk 2017/11/11
 */
public interface SecurityConfigRepository extends GenericJpaRepository<SecurityConfig,String> ,JpaSpecificationExecutor<SecurityConfig> {



}
