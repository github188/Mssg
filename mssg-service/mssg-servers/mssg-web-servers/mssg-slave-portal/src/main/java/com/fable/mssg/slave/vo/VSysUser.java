package com.fable.mssg.slave.vo;

import com.fable.mssg.domain.usermanager.SysUser;
import lombok.Data;

/**
 * @Description
 * @Author wangmeng 2017/11/1
 */
@Data
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
    private String roleId;//用户角色
    private  String position;//职位
    private String description;

    public static VSysUser convert(SysUser bean){
        VSysUser vSysUser = null;
        if(bean!=null){
            vSysUser = new VSysUser();
            vSysUser.setCellPhoneNumber(bean.getCellPhoneNumber());
            vSysUser.setId(bean.getId());
            vSysUser.setIdCard(bean.getIdCard());
            vSysUser.setLoginName(bean.getLoginName());
            vSysUser.setTelphone(bean.getTelphone());
            vSysUser.setUserName(bean.getUserName());
            vSysUser.setCompanyName(bean.getComId().getName());
            vSysUser.setCellPhoneNumber(bean.getCellPhoneNumber());
            vSysUser.setUserRole(bean.getRoleId()==null?"":bean.getRoleId().getRoleName());
            vSysUser.setState(bean.getState());
            vSysUser.setCompanyId(bean.getComId()==null?"":bean.getComId().getId());
            vSysUser.setRoleId(bean.getRoleId()==null?"":bean.getRoleId().getId());
            vSysUser.setPosition(bean.getPosition());//职位
            vSysUser.setDescription(bean.getDescription());
        }
        return vSysUser;
    }
}
