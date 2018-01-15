package com.fable.mssg.catalog.converter;

import com.fable.mssg.catalog.domain.EquipmentCatalogBean;
import com.fable.mssg.vo.orginalds.VEquipmentCatalogBean;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * @author : yuhl 2017-09-01
 */
@Service
public class EquipmentCatalogConverter extends RepoBasedConverter<EquipmentCatalogBean, VEquipmentCatalogBean, String> {
    @Override
    protected VEquipmentCatalogBean internalConvert(EquipmentCatalogBean bean) {
        return VEquipmentCatalogBean.builder().id(bean.getId()).dsName(bean.getCatalogName())
                .deviceId(bean.getDeviceId()).address(bean.getAddress()).block(bean.getBlock())
                .busGroupId(bean.getBusGroupId()).dsType(bean.getCatalogType()).civilCode(bean.getCivilCode())
                .createTime(bean.getCreateTime()).loginPwd(bean.getLoginPwd()).createUser(bean.getCreateUser())
                .manuName(bean.getManuName()).model(bean.getModel()).owner(bean.getOwner())
                .parent_id(bean.getParent_id()).parental(bean.getParental()).parentId(bean.getParentId())
                .registerWay(bean.getRegisterWay()).secrecy(bean.getSecrecy()).status(bean.getStatus())
                .mediaDeviceId(bean == null ? "" : bean.getMediaDeviceId())
                .lng(bean.getLng() == null ? 0 : bean.getLng()).lat(bean.getLat() == null ? 0 : bean.getLat())
                .equipType(bean.getEquipType()).locationType(bean.getLocationType()).ipAddress(bean.getIpAddress())
                .updateTime(bean.getUpdateTime()).updateUser(bean.getUpdateUser())
                .dsLevel(bean.getDsLevel())
                .build();
    }


}
