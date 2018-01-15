package com.fable.mssg.domain.usermanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.*;

/**
 * @description 用户角色实体类
 * @author xiejk 2017/9/20
 */
@Data
@Table(name = "mssg_role")
@Entity
public class SysRole extends AbstractUUIDPersistable {


  //角色名称
  @Column(name = "ROLE_NAME")
  private String roleName;

  //角色别名
  @Column(name = "ROLE_CODE")
  private String roleCode;

  //角色描述
  @Column(name = "ROLE_DECRIPTION")
  private String roleDecription;

  //角色类型
  @Column(name="ROlE_TYPE")
  private  int roleType;

  public SysRole() {
  }

  public SysRole(String id) {
    super.setId(id);
  }

}
