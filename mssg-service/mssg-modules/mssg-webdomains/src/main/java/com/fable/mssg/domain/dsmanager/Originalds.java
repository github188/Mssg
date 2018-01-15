package com.fable.mssg.domain.dsmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.mediainfo.MediaInfo;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * description   原始数据源实体类
 * @author xiejk 2017/9/30
 * @version   [版本号, YYYY-MM-DD]
 */
@Data
@Entity
@Table(name = "mssg_original_ds")
public class Originalds extends AbstractUUIDPersistable {

    //目录名称
    @Column(name = "ds_name")
    private String dsName;

    //数据源设备号目录ID
    @Column(name = "device_id")
    private String deviceId;

    //目录类型
    @Column(name = "ds_type")
    private int dsType;

    //厂商名称
    @Column(name = "manu_name")
    private String manuName;

    //平台或设备型号
    @Column(name = "model")
    private String model;

    //设备所有者
    @Column(name = "owner")
    private String owner;

    //关联代码
    @Column(name = "civil_code")
    private String civilCode;

    //组织机构码
    @Column(name = "block")
    private String block;

    //安装地址
    @Column(name = "ADDRESS")
    private String address;

    //父级标识
    @Column(name = "parental")
    private Integer parental;

    //上级目录ID
    @Column(name = "parentid")
    private String parentid;

    //注册路径
    @Column(name = "register_way")
    private Integer registerWay;

    //是否保密
    @Column(name = "secrecy")
    private Integer secrecy;

    //状态
    @Column(name = "status")
    private String status;

    //业务分组ID
    @Column(name ="bus_group_id" )
    private String busGroupId;

    //登录密码
    @Column(name = "login_pwd")
    private String loginPwd;

    //父ID
    @Column(name = "parent_id")
    private String pid;

    //平台ID 对应媒体平台表的DEVICE_ID
    //@ManyToOne
    //@JoinColumn(name = "MEDIA_DEVICE_ID",referencedColumnName = "DEVICE_ID")
    @Column(name = "MEDIA_DEVICE_ID")
    private String mediaDeviceId;

    //数据源等级
    @Column(name = "DS_LEVEL")
    private  Long  dslevel;

    //经度
    @Column(name = "LNG")
    private  Double  lng;
    //纬度
    @Column(name = "LAT")
    private  Double  lat;
    //位置类型
    @Column(name = "LOCATION_TYPE")
    private  Long  locationType;
    //IP地址
    @Column(name = "IP_ADDRESS")
    private  String  ipAddress;
    //设备类型
    @Column(name = "EQUIP_TYPE")
    private  Long  equipType;

    //创建者
    @Column(name = "create_user")
    private String createUser;

    //创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    //更新人
    @Column(name = "update_user")
    private String updateUser;

    //更新时间
    @Column(name = "update_time")
    private Timestamp updateTime;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany
    @JoinColumn(name = "device_code",referencedColumnName = "device_id")
    private List<DeviceVist> deviceVistList;


}
