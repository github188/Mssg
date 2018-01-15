package com.fable.mssg.service.equipment;

import com.fable.mssg.domain.equipment.MediaInfoBean;
import com.fable.mssg.domain.securityconfig.SecurityConfig;

/**
 * @author: yuhl Created on 13:58 2017/11/15 0015
 */
public interface ConfigPolicyService {

    /**
     * 根据deviceId查询媒体源信息
     * @param deviceId
     * @return
     */
    MediaInfoBean getMediaByDeviceId(String deviceId);

    /**
     * 根据ip查询媒体源信息
     * @param ipAddress
     * @return
     */
    MediaInfoBean getMediaByIp(String ipAddress);

    /**
     * 根据安全配置编码查询安全配置信息
     * @param secCode
     * @return
     */
    SecurityConfig getConfigInfoByCode(String secCode);

}
