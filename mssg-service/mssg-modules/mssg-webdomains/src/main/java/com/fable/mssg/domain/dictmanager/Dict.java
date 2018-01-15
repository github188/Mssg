package com.fable.mssg.domain.dictmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * 字典实体类
 * description
 * @author xiejk 2017/11/06
 */
@Data
@Entity
@Table(name = "mssg_dict")
public class Dict  extends AbstractUUIDPersistable {


  //字典分类编码
  @Column(name = "dict_code")
  private Long dictCode;
  //字典分类名称
  @Column(name = "dict_name")
  private String dictName;
  //字典分类描述
  @Column(name = "dict_remark")
  private String dictRemark;

  @Column(name = "create_user")
  private String createUser;
  @Column(name = "create_time")
  private java.sql.Timestamp createTime;

  public Dict(){}

}
