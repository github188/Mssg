package com.fable.mssg.resource.vo;

import lombok.Data;

/**
  * 资源状态工具类
  * author xiejk 2017/9/30
 */
@Data
public class ResourceStatus {

     //待提交
     public final static int TO_SUBMIT =1;

     //待审核
     public final static int CHECK_PENDING =2;

     //待发布
     public final static int TO_PUBLISH =3;

     //已拒绝
     public final static int REFUSED=4;

     //已发布
     public final static int PUBLISHED=5;

     //已撤销
     public final static int REVOKED=6;

}
