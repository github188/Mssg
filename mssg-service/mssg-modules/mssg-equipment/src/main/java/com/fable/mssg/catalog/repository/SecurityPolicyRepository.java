package com.fable.mssg.catalog.repository;

import com.fable.mssg.domain.securityconfig.SecurityConfig;
import com.slyak.spring.jpa.GenericJpaRepository;

/**
 * @author: yuhl Created on 18:50 2017/11/16 0016
 */
public interface SecurityPolicyRepository extends GenericJpaRepository<SecurityConfig, String> {

    /**
     * 根据安全配置编码查询安全配置信息
     * @param secCode
     * @return
     */
    SecurityConfig findBySecCode(String secCode);
}
