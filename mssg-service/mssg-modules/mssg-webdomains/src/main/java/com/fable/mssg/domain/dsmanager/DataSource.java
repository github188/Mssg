package com.fable.mssg.domain.dsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.fable.mssg.vo.FlatDir;
import lombok.Data;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * .
 *
 * @author stormning 2017/8/21
 * @since 1.3.0
 */

@Entity
@Table(name = "mssg_datasource")
@Data
public class DataSource implements FlatDir, Serializable {

    @Id
    @Column(name = "id", length = 20)
    String id;

    //数据源名称
    @Column(name = "DS_NAME", length = 100)
    String dsName; //数据源名称,

    @Column(name = "DS_CODE", length = 20)
    String dsCode;  //数据源ID   标准定义，长度不超过20

    @Column(name = "DS_TYPE", length = 1)
    Integer dsType; //数据源类型  1:行政区划目录  2:系统目录 3:业务分组目录 4:虚拟目录 5:设备目录 6:自定义目录

    @Column(name = "MANU_NAME", length = 100)
    String manuName; //厂商名称 标准定义，目录类型为系统目录和设备目录时填写。

    @Column(name = "MODEL", length = 100)
    String model; //平台或设备型号  标准定义，目录类型为系统目录和设备目录时填写。

    @Column(name = "OWNER", length = 100)
    String owner; //归属  标准定义，目录类型为系统或设备目录时填写。

    @Column(name = "CIVIL_CODE", length = 20)
    String civilCode; //关联代码 标准定义，目录类型为系统或设备目录时填写。用于跨级关系的关联。

    @Column(name = "BLOCK", length = 20)
    String block; //组织机构码  标准定义，为设备目录时填写。

    @Column(name = "ADDRESS", length = 200)
    String address; //安装地址 标准定义，目录类型为系统或设备目录时填写。

    @Column(name = "PARENTAL", length = 1)
    Integer parental; //父级标识  标准定义，为设备目录时填写，表示该设备是否一个父设备。

    @Column(name = "PARENTID", length = 100)
    String standardParentId; //上级目录ID 标准定义，为设备目录时可填多个，之间用/分割。比如同时拥有父设备目录ID和虚拟目录ID

    @Column(name = "REGISTER_WAY", length = 1)
    Integer registerWay; //注册路径 标准定义

    @Column(name = "SECRECY", length = 1)
    Integer secrecy;  //是否保密 标准定义，系统或设备目录填写。

    @Column(name = "STATUS", length = 10)
    String status; //状态  标准定义，系统或设备目录填写，取值ON或OFF

    @Column(name = "BUS_GROUP_ID", length = 20)
    String busGroupId; //业务分组ID 标准定义，为虚拟目录时填写。

    @Column(name = "LOGIN_PWD", length = 50)
    String loginPwd; //登录密码 标准定义，系统或设备目录时填写。

    @Column(name = "PARENT_ID", length = 36)
    String parentId; //父ID  非标准定义，用于直接区分目录间的父子关系，对应主键ID。

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MEDIA_ID", referencedColumnName = "ID")
    MediaInfo mediaId;

    @Column(name = "DS_LEVEL", length = 11)
    Integer dsLevel;

    @Column(name = "LNG")
    Double lng;

    @Column(name = "LAT")
    Double lat;

    @Column(name = "LOCATION_TYPE", length = 11)
    Integer locationType;

    @Column(name = "IP_ADDRESS", length = 50)
    String ipAddress;

    @Column(name = "EQUIP_TYPE", length = 11)
    Integer equipType;

    @Column(name = "CREATE_USER", length = 36)
    String createUser; //创建用户

    @Column(name = "CREATE_TIME")
    Date createTime; //创建时间

    @Column(name = "UPDATE_USER", length = 36)
    String updateUser; //更新用户

    @Column(name = "UPDATE_TIME")
    Date updateTime; //更新时间

    @Column(name = "RES_ID")
    String rsId;

    public DataSource() {
    }

    public DataSource(String dsId) {
        this.setId(dsId);
    }

    @Transient
    List<FlatDir> children;

    @Override
    public String thisId() {
        return dsCode;
    }

    @Override
    public String parentId() {
        return standardParentId;
    }

    @Override
    public List<FlatDir> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }
}
