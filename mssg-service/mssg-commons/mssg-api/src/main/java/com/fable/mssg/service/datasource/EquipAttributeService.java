package com.fable.mssg.service.datasource;

import com.fable.mssg.domain.dsmanager.EquipAttributeBean;

import java.io.InputStream;
import java.util.List;

/**
 * description 设备属性表接口
 * @author xiejk 2017/11/14
 */
public interface EquipAttributeService {

    List<EquipAttributeBean> findOneEquiBysbbm(String sbbm);


    EquipAttributeBean updateEquiAttribute(EquipAttributeBean equipAttributeBean);

    /**
     * 导入设备属性
     * @param inputStream
     * @return
     */
    String leadIn(InputStream inputStream);




}
