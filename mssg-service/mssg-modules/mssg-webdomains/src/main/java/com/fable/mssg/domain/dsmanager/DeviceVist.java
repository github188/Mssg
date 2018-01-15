package com.fable.mssg.domain.dsmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.OnLineLog;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Table(name = "mssg_device_vist")
@Entity
@Data
public class DeviceVist extends AbstractUUIDPersistable {

  //设备名称
  @Column(name = "device_name",length = 50)
  private String deviceName;
  //设备码
  @Column(name = "device_code",length = 20)
  private String deviceCode;
  //操作类型
  @Column(name = "op_type")
  private int op_type;
  //访问时间
  @Column(name = "VIST_TIME")
  private java.sql.Timestamp vistTime;
  //访问时长
  @Column(name = "VIST_LONG")
  private Long vistLong;

  @Column(name = "ONLINE_LOG_ID")
  private  String onlineLogId;

  @Column(name = "USER_ID")
  private String userId;
  

}

