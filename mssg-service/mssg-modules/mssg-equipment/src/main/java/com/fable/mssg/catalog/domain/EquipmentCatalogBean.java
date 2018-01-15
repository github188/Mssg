package com.fable.mssg.catalog.domain;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.*;

/**
 * @author : yuhl 2017-09-01
 * 设备目录实体类
 */
@Entity
@Table(name = "MSSG_ORIGINAL_DS")
@Data
public class EquipmentCatalogBean extends AbstractUUIDPersistable {

    public String id; // 主键

    @Column(name = "DS_NAME")
    public String catalogName; // 目录名称

    @Column(name = "DEVICE_ID")
    public String deviceId; // 设备id

    @Column(name = "DS_TYPE")
    public Integer catalogType; // 目录类型

    @Column(name = "MANU_NAME")
    public String manuName; // 厂商名称

    @Column(name = "MODEL")
    public String model; // 平台或设备型号

    @Column(name = "OWNER")
    public String owner; // 设备归属

    @Column(name = "CIVIL_CODE")
    public String civilCode; // 关联区划编码

    @Column(name = "BLOCK")
    public String block; // 组织机构编码

    @Column(name = "ADDRESS")
    public String address; // 设备地址

    @Column(name = "PARENTAL")
    public Integer parental; // 父级标识

    @Column(name = "PARENTID")
    public String parentId; //父级id

    @Column(name = "REGISTER_WAY")
    public Integer registerWay; // 注册路径

    @Column(name = "SECRECY")
    public Integer secrecy; // 是否保密

    @Column(name = "STATUS")
    public String status; // 设备状态

    @Column(name = "BUS_GROUP_ID")
    public String busGroupId; // 设备业务分组id

    @Column(name = "LOGIN_PWD")
    public String loginPwd; // 登陆密码

    @Column(name = "PARENT_ID")
    public String parent_id; // 业务表父级id

    @Column(name = "MEDIA_DEVICE_ID")
    public String mediaDeviceId; // 媒体源设备id

    @Column(name = "DS_LEVEL")
    public Integer dsLevel = 0; // 设备等级,默认为0所有单位可看

    @Column(name = "LNG")
    public Double lng; // 经度

    @Column(name = "LAT")
    public Double lat; // 纬度

    @Column(name = "LOCATION_TYPE")
    public int locationType; // 位置类型

    @Column(name = "IP_ADDRESS")
    public String ipAddress; // IP地址

    @Column(name = "EQUIP_TYPE")
    public int equipType; // 设备类型

    @Column(name = "CREATE_USER")
    public String createUser; // 创建者

    @Column(name = "CREATE_TIME")
    public String createTime; // 创建时间

    @Column(name = "UPDATE_USER")
    public String updateUser; // 更新者

    @Column(name = "UPDATE_TIME")
    public String updateTime; // 更新时间



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EquipmentCatalogBean that = (EquipmentCatalogBean) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
