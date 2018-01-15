package com.fable.mssg.bean;

/**
 * @Description
 * @Author wangmeng 2017/10/31
 */

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户实体类
 *
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Data
@Builder
public class SysUserBean {
    private String id;
    private String loginName;//登录名称
    private String userName;//用户名称
    private String password;//密码
    private String salt;//md5加密
    private String roleId;//角色id
    private int state;//用户状态
    private String idCard;//用户身份证号
    private String telphone;//电话
    private String email;//电子邮箱
    private String description;//描述
    private Integer deleteFlag;//是否被删除
    private Integer loginState;//登录状态
    private String cellPhoneNumber;//手机号码
    private String createUser;//创建人
    private Timestamp createTime;//创建时间
    private String updateUser;//更新人
    private Timestamp updateTime;//更新时间
    private String comId;  //公司id


}
