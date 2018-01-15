package com.fable.mssg.security.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.catalog.repository.MediaSourceInfoRepository;
import com.fable.mssg.catalog.repository.SecurityPolicyRepository;
import com.fable.mssg.domain.equipment.MediaInfoBean;
import com.fable.mssg.domain.securityconfig.SecurityConfig;
import com.fable.mssg.service.equipment.ConfigPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: yuhl Created on 14:22 2017/11/15 0015
 */
@Exporter(interfaces = ConfigPolicyService.class)
public class ConfigPolicyServiceImpl implements ConfigPolicyService {

    @Autowired
    private MediaSourceInfoRepository mediaSourceInfoRepository;

    @Autowired
    private SecurityPolicyRepository securityPolicyRepository;
    /**
     * 根据deviceId查询媒体源信息
     *
     * @param deviceId
     * @return
     */
    @Override
    public MediaInfoBean getMediaByDeviceId(String deviceId) {
        return mediaSourceInfoRepository.findByDeviceId(deviceId);
    }

    /**
     * 根据ip查询媒体源信息
     *
     * @param ipAddress
     * @return
     */
    @Override
    public MediaInfoBean getMediaByIp(String ipAddress) {
        return mediaSourceInfoRepository.findByIpAddress(ipAddress);
    }

    /**
     * 根据安全配置编码查询安全配置信息
     *
     * @param secCode
     * @return
     */
    @Override
    public SecurityConfig getConfigInfoByCode(String secCode) {
        return securityPolicyRepository.findBySecCode(secCode);
    }

}
