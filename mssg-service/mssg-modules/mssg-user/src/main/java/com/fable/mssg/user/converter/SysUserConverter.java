package com.fable.mssg.user.converter;



import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.user.vo.SysUserStatus;
import com.fable.mssg.user.vo.VSysUser;

import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * 用户视图转换类
 * @author  xiejk 2017/9/30
 */
@Service
public class SysUserConverter extends RepoBasedConverter<SysUser,VSysUser,String> {
    @Override
    protected VSysUser internalConvert(SysUser source) {
        return VSysUser.builder()
                //用户id
                .id(source.getId())
                //手机号码
                .cellPhoneNumber(source.getCellPhoneNumber())
                //公司名称
                .companyName(source.getComId()==null?"":source.getComId().getName())
                //身份证号
                .idCard(source.getIdCard())
                //登录名称
                .loginName(source.getLoginName())
                //角色名称
                .userRole(source.getRoleId()==null?"":source.getRoleId().getRoleName())
                //用户状态
                .state(source.getState())
                //用户名称
                .userName(source.getUserName())
                .position(source.getPosition())//职位
                .roleId(source.getRoleId()==null?"":source.getRoleId().getId())
                .telphone(source.getTelphone())
                .companyId(source.getComId()==null?"":source.getComId().getId())
                .companyName(source.getComId()==null?"":source.getComId().getName())
                .telphone(source.getTelphone())
                .address(source.getComId()==null?"":source.getComId().getAddress())
                .cellPhoneNumber(source.getCellPhoneNumber())
                .composition(source.getComId()==null?"":source.getComId().getPosition())
                .contacts(source.getComId()==null?"":source.getComId().getContacts())
                .ctelphone(source.getComId()==null?"":source.getComId().getTelphone())
                .officePhone(source.getComId()==null?"":source.getComId().getOfficePhone())
                .description(source.getComId()==null?"":source.getComId().getDescription())//描述
                .roleType(source.getRoleId()==null?0:source.getRoleId().getRoleType())//用户角色类型
                .companyLevel(source.getComId()==null?null:source.getComId().getComLevel())
                .build();
    }

}
