package com.fable.mssg.catalog.repository;

import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: yuhl Created on 11:34 2017/10/27 0027
 */
public interface MediaSourceRepository extends GenericJpaRepository<EquipmentCatalogBean, String> {

    /**
     * 根据媒体源设备id删除所有对应目录
     * @param mediaDeviceId 媒体源设备id
     * @return
     */
    @Modifying
    @Transactional
    @Query("delete from EquipmentCatalogBean where mediaDeviceId = ?1")
    int deleteCatalogByMediaDeviceId(@Param("mediaDeviceId") String mediaDeviceId);
}
