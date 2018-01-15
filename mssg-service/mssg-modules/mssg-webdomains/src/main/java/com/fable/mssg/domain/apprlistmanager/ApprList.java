package com.fable.mssg.domain.apprlistmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "mssg_appr_list")
public class ApprList extends AbstractUUIDPersistable {
  //审批项ID
  @Column(name = "appr_id")
  private String apprId;

  //审批状态
  @Column(name = "appr_status")
  private Integer apprStatus;

  //审批意见
  @Column(name = "appr_msg")
  private String apprMsg;

  //审批类型  1:目录审批  2:订阅审批  3:注册审批
  @Column(name = "appr_type")
  private Long apprType;

  //创建人
  @Column(name = "create_user")
  private String createUser;

  //创建时间
  @Column(name = "create_time")
  private Timestamp createTime;

  public ApprList(){}


}
