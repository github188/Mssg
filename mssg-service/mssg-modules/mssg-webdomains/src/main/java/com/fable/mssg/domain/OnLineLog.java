package com.fable.mssg.domain;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.dsmanager.DeviceVist;
import com.fable.mssg.domain.usermanager.SysUser;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.Id;
import javax.persistence.*;
import java.util.List;

/**
 * 用户访问日志实体类
 * @author xiejk 2017/10/26
 */
@Entity
@Table(name="mssg_online_log")
@Data
public class OnLineLog extends AbstractUUIDPersistable {


  //登录IP
  @Column(name = "LOGIN_IP",length = 50)
  private String loginIp;
  //用户ID
  @NotFound(action = NotFoundAction.IGNORE)
  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private SysUser user;
  //上线时间
  @Column(name = "LOGIN_TIME")
  private java.sql.Timestamp loginTime;
  //下线时间
  @Column(name = "LOGOUT_TIME")
  private java.sql.Timestamp logoutTime;
  //是否强制下线 0:否  1:是
  @Column(name = "IS_T_OFFLINE")
  private Integer istOffline;
  //在线流量  存储单位为字节
  @Column(name = "ONLINE_RATE")
  private Long onlineRate;
  //访问设备数
  @Column(name = "VIST_EQUIP_COUNT")
  private Integer vistEquipCount;

  /*//访问明细
  @OneToMany
  @JoinColumn(name = "ONLINE_LOG_ID",referencedColumnName = "ID")
  private List<DeviceVist> devices;*/

}
