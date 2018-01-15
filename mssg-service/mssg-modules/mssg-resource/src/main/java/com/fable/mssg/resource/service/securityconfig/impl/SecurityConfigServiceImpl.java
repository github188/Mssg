package com.fable.mssg.resource.service.securityconfig.impl;


import com.fable.mssg.domain.securityconfig.SecurityConfig;
import com.fable.mssg.resource.repository.securityconfig.SecurityConfigRepository;
import com.fable.mssg.resource.service.securityconfig.SecurityConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityConfigServiceImpl implements SecurityConfigService {

    @Autowired
    SecurityConfigRepository securityConfigRepository;
    @Override
    public List<SecurityConfig> findAll() {
        return  securityConfigRepository.findAll();
    }

    @Override
    public SecurityConfig findOneSecurity(String id) {
        return securityConfigRepository.findOne(id);
    }

    @Override
    public void updateSecurity(SecurityConfig securityConfig) {
        securityConfigRepository.save(securityConfig);
    }
}
