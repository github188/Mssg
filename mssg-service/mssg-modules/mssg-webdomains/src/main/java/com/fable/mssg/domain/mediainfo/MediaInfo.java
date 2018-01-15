package com.fable.mssg.domain.mediainfo;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.dsmanager.Originalds;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * description  媒体平台实体类
 * @author xiejk 2017/9/30
 * @version   [版本号, YYYY-MM-DD]
 */

@Data
@Entity
@Table(name="mssg_media_info")
public class MediaInfo  extends AbstractUUIDPersistable {

    //平台名称
    @Column(name="MEDIA_NAME")
    private String mediaName;

    //ip地址
    @Column(name="IP_ADDRESS")
    private String ipAddress;

    //会话端口
    @Column(name = "SESSION_PORT")
    private Integer sessionPort;

    //所属厂商
    @Column(name = "MANU_NAME")
    private String manuName;

    //备注
    @Column(name = "REMARK")
    private String remark;

    //心跳时间
    @Column(name = "HEART_TIME")
    private Long heartTime;

    //登录密码
    @Column(name = "PASSWORD")
    private String password;

    //设备号  对应originalds media_device_id
    @Column(name = "DEVICE_ID")
    private  String deviceId;


    /*//设备号 一对多配置
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn (name = "media_device_id",referencedColumnName = "DEVICE_ID")
    private List<Originalds> deviceIds=new ArrayList<>();*/

    //域名
    @Column(name="AREA_NAME")
    private String areaName;

    //鉴权
    @Column(name = "AUTH")
    private  Integer auth;
    //信令格式
    @Column(name = "SINGAL_FORMAT")
    private String singalFormat;
    //realm
    @Column(name = "REALM")
    private  String realm;
    //媒体格式
    @Column(name = "MEDIA_FORMAT")
    private String mediaFormat;
    //媒体平台类型
    @Column(name = "MEDIA_TYPE")
    private Long mediaType;
    //所属模块
    @Column(name = "FOR_MODULE")
    private  Integer forModule;
    /*//创建人
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne*/
    @Column(name = "create_user")
    private String  createUser;
    //创建时间
    @Column(name="create_time")
    private Timestamp createTime;
    //更改人
    /*@NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne*/
    @Column(name="update_user")
    private String updateUser;

    //更改时间
    @Column(name = "update_time")
    private Timestamp updateTime;
    //单位id
    @Column(name = "COM_ID")
    private  String comId;

    @Column(name = "GB_VERSION")
    private String gbVersion;

    public MediaInfo(){}

    public MediaInfo(String mediaDeviceId) {
        super.setId(mediaDeviceId);
    }
}
