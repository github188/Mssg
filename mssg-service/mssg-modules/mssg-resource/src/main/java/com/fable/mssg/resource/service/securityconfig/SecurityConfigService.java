package com.fable.mssg.resource.service.securityconfig;


import com.fable.mssg.domain.securityconfig.SecurityConfig;

import java.util.List;

/**
 * description  安全配置接口
 * @author xiejk 2017/9/30
 */
public interface SecurityConfigService {

    List<SecurityConfig> findAll();

    SecurityConfig findOneSecurity(String id);

    void updateSecurity(SecurityConfig securityConfig);

}
