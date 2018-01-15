package com.fable.mssg.catalog.repository;

import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : yuhl 2017-09-01
 */
public interface EquipmentCatalogRepository extends GenericJpaRepository<EquipmentCatalogBean, String>, JpaSpecificationExecutor<EquipmentCatalogBean> {

    /**
     * 根据deviceCode删除设备目录
     *
     * @param deviceCode
     * @return
     */
    @Modifying
    @Transactional
    @Query("delete from EquipmentCatalogBean where deviceId = ?1")
    int deleteCatalogByDeviceCode(@Param("deviceId") String deviceCode);

    /**
     * 根据deviceCode查询设备目录
     *
     * @param deviceCode
     * @return
     */
    @Query("select bean from EquipmentCatalogBean bean where bean.deviceId = ?1")
    EquipmentCatalogBean queryByDeviceCode(@Param("deviceId") String deviceCode);

    /**
     * 更新设备目录状态
     *
     * @param status
     * @param deviceId
     * @return
     */
    @Modifying
    @Transactional
    @Query("update EquipmentCatalogBean bean set bean.status = ?1 where bean.deviceId = ?2")
    int updateCatalogStatus(@Param("status") String status, @Param("deviceId") String deviceId);

    @Query(nativeQuery = true,
            value = "SELECT  * " +
                    "FROM MSSG_ORIGINAL_DS " +
                    "WHERE FIND_IN_SET(DEVICE_ID,queryParent(?1))")
    List<EquipmentCatalogBean> findParent(String deviceId);

}
