package com.fable.mssg.vo.resource;

import com.fable.mssg.vo.datasource.VDataSource;
import com.fable.mssg.vo.orginalds.VEquipmentCatalogBean;
import lombok.Data;

import java.util.List;
@Data
public class PublishDataSourceInfo {
    String rid;
    List<VEquipmentCatalogBean> equipmentCatalogBeans;
    Integer realControl;
    Integer record;
    Integer realSnap;
    Integer histSnap;
    Integer download;
    String realDays;
    String histDays;

}
