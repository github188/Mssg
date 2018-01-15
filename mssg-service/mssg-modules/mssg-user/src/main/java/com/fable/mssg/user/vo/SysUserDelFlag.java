package com.fable.mssg.user.vo;

import lombok.Data;

/**
  * 用户删除标志工具类
  * author xiejk 2017/10/26
 */
@Data
public class SysUserDelFlag {
     //未删除
     public final static int NOTDELETED=0;
     //已删除
     public final static int DELETED=1;


}
