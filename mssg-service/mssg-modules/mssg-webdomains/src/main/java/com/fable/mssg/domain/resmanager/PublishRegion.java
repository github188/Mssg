package com.fable.mssg.domain.resmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;
import javax.persistence.*;

/**
 * description  公司资源发布关系实体类
 * @author xiejk 2017/9/30
 */
@Data
@Entity
@Table(name = "mssg_publish_region")
public class PublishRegion extends AbstractUUIDPersistable {

      @Id
      @GeneratedValue
      private String id;

      //公司id
      @Column(name = "com_id")
      private String comId;

      //资源id
      @Column(name = "res_id")
      private String resId;

      public PublishRegion(String comId, String resId) {
        this.comId = comId;
        this.resId = resId;
      }
}
