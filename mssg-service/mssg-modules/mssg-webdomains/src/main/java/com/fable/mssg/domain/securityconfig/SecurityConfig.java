package com.fable.mssg.domain.securityconfig;


import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * description  安全配置实体类
 * @author xiejk 2017/11/11
 */
@Entity
@Table(name = "mssg_security_config")
@Data
public class SecurityConfig extends AbstractUUIDPersistable {

  @Column(name = "SEC_NAME")
  private String secName;
  @Column(name = "SEC_CODE")
  private String secCode;
  @Column(name = "STATUS")
  private Long status;
  @Column(name = "SEC_POLICY")
  private Long secPolicy;
  @Column(name = "SEC_DESCRIPTION")
  private String secDescription;
  @Column(name = "CREATE_USER")
  private String createUser;
  @Column(name = "CREATE_TIME")
  private java.sql.Timestamp createTime;
  @Column(name = "UPDATE_USER")
  private String updateUser;
  @Column(name = "UPDATE_TIME")
  private java.sql.Timestamp updateTime;


}
