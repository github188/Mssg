package com.fable.mssg.domain.equipment;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author : yuhl 2017-09-01
 * 流媒体源实体类
 */
@Entity
@Table(name = "mssg_media_info")
@Data
public class MediaInfoBean extends AbstractUUIDPersistable {

    public String id; // 主键

    @Column(name = "MEDIA_NAME")
    public String mediaName; // 媒体源名称

    @Column(name = "IP_ADDRESS")
    public String ipAddress; // ip

    @Column(name = "SESSION_PORT")
    public Integer sessionPort; // 端口

    @Column(name = "MANU_NAME")
    public String manuName; // 厂商

    @Column(name = "REMARK")
    public String remark; // 备注

    @Column(name = "HEART_TIME")
    public String heartTime; // 心跳时间

    @Column(name = "PASSWORD")
    public String password; // 密码

    @Column(name = "DEVICE_ID")
    public String deviceId; // 设备id

    @Column(name = "AREA_NAME")
    public String domainName; // 域名

    @Column(name = "AUTH")
    public int auth; // 是否认证

    @Column(name = "SINGAL_FORMAT")
    public String singalFormat; // 信令格式

    @Column(name = "REALM")
    public String realm;

    @Column(name = "MEDIA_FORMAT")
    public String mediaFormat; // 媒体格式

    @Column(name = "MEDIA_TYPE")
    public int mediaType; // 媒体源类型

    @Column(name = "FOR_MODULE")
    public int forModule; // 厂家

    @Column(name = "CREATE_USER")
    public String createUser; // 创建人

    @Column(name = "CREATE_TIME")
    public String createTime; // 创建时间

    @Column(name = "UPDATE_USER")
    public String updateUser; // 更新人

    @Column(name = "UPDATE_TIME")
    public String updateTime; // 更新时间

    @Column(name = "GB_VERSION")
    private String gbVersion; // 网关国标版本

}
