package com.fable.mssg.catalog.service;

import com.fable.mssg.domain.equipment.EquipAttribute;
import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.domain.equipment.MediaInfoBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author : yuhl 2017-09-01
 */
public interface EquipmentCatalogService {

    /**
     * 保存目录信息
     * @param bean
     * @return
     */
    EquipmentCatalogBean saveCatalogInfo(EquipmentCatalogBean bean);

    /**
     * 查询所有目录信息
     * @return
     */
    List<EquipmentCatalogBean> findAll();

    /**
     * 查询所有媒体源信息
     * @return
     */
    List<MediaInfoBean> findAllMediaInfo();

    /**
     * 根据deviceCode查询目录信息
     * @param deviceCode
     * @return
     */
    EquipmentCatalogBean getCatalogInfoByDeviceCode(String deviceCode);

    /**
     * 根据id查询设备目录信息
     * @param id
     * @return
     */
    EquipmentCatalogBean getCatalogInfoById(String id);

    /**
     * 根据deviceCode更新设备目录信息
     * @param bean
     * @return
     */
    void updateCatalogInfoByDeviceCode(EquipmentCatalogBean bean);

    /**
     * 根据deviceCode删除设备目录
     * @param deviceCode
     * @return
     */
    int deleteCatalogByDeviceCode(String deviceCode);

    /**
     * 更新设备目录状态
     * @param status
     * @param deviceId
     * @return
     */
    int updateCatalogStatus(String status, String deviceId);

    /**
     * 根据媒体平台id查询设备
     * @param mediaIds
     * @param eqLevels
     *@param positions @return
     */

    List<EquipmentCatalogBean> findByFilter(String[] mediaIds, Integer[] eqLevels, Integer[] positions, String eqName);

    /**
     * 导入摄像设备信息
     * @param inputStream
     */
    int[] importEquipInfo(InputStream inputStream);

    /**
     * 导出摄像设备信息
     * @return
     */

    HSSFWorkbook exportEquipInfo();

    Map findAllPageByCondition(int size, int page, String dsName,
                               String jkdwlx, String locationType, String mediaDeviceId);

    void updateEquipAttribute(EquipAttribute equipAttribute);

    EquipmentCatalogBean findOne(String oid);

    EquipAttribute findOneEquipBydsCode(String deviceId);

    EquipmentCatalogBean findByDeviceId(String deviceId);
}
