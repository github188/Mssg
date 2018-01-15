package com.fable.mssg.catalog.repository;

import com.fable.mssg.domain.equipment.MediaInfoBean;
import com.slyak.spring.jpa.GenericJpaRepository;

/**
 * @author : yuhl 2017-09-01
 */
public interface MediaSourceInfoRepository extends GenericJpaRepository<MediaInfoBean, String> {

    /**
     * 根据媒体源id查询媒体源信息
     * @param deviceId
     * @return
     */
    MediaInfoBean findByDeviceId(String deviceId);

    /**
     * 根据ip查询媒体源信息
     * @param ipAddress
     * @return
     */
    MediaInfoBean findByIpAddress(String ipAddress);
}
