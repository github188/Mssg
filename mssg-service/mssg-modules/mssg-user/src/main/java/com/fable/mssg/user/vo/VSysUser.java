package com.fable.mssg.user.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @description 用户信息视图类
 * @author xiejk 2017/9/20
 */
@Data
@Builder
public class VSysUser {

    private String id;
    private String loginName;//登录名称
    private String userName;//用户名称
    private String idCard;//用户身份证号
    private String telphone;//电话
    private String companyName;//单位
    private String cellPhoneNumber;//手机号码
    private String userRole;//用户角色
    private int state;//状态
    private  String companyId;//公司id
    private String roleId;//用户角色id
    private  String position;//职位
    private  String address;//地址
    private String contacts;
    private String composition;
    private String officePhone;
    private String ctelphone;
    private String description;
    private int roleType;//用户角色类型
    private Integer companyLevel;

}
