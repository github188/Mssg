package com.fable.mssg.vo;

import lombok.Data;

/**
 * description    注册审批视图类
 * @author xiejk 2017/11/12
 */


@Data
public class VRegisterApprList {

    String id;//审批id
    String loginName;//注册用户名
    String userName;//姓名
    String companyName;//单位
    String apprStatus;//审批状态
    String position;//职位
    String CareID;//身份证
    String pwd;//密码
    String telphone;//办公电话
    String cellphonenumber;//手机电话
    String companyAddress;//单位地址
    String linkMan;//联系人
    String linkManposition;//联系人职位
    String companytelphone;//单位联系电话
    String comLevel;


}
