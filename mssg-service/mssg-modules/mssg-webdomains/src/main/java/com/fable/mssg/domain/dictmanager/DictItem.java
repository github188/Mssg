package com.fable.mssg.domain.dictmanager;


import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "mssg_dict_item")
public class DictItem implements Serializable{

  @Id
  @Column(name = "dict_item_code")
  private Long dictItemCode;

  @Column(name="dict_code")
  private Long dictCode;

  @Column(name = "dict_item_name")
  private String dictItemName;
  @Column(name = "dict_item_remark")
  private String dictItemRemark;
  @Column(name = "create_user")
  private String createUser;
  @Column(name = "create_time")
  private java.sql.Timestamp createTime;
  @Column(name = "update_user")
  private String updateUser;
  @Column(name = "update_time")
  private java.sql.Timestamp updateTime;
  @Column(name = "DICT_PID")
  private String dictPid;

  public  DictItem(){}

  public  DictItem(Long dictItemCode){
    this.dictItemCode = dictItemCode;
  }
}
