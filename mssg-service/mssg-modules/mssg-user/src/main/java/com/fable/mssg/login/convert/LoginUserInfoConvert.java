package com.fable.mssg.login.convert;

import com.fable.mssg.domain.usermanager.SysMenu;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.user.repository.SysMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author: yuhl Created on 11:22 2017/11/20 0020
 */
@Component
public class LoginUserInfoConvert implements Converter<SysUser, LoginUserInfo> {

    @Autowired
    SysMenuRepository menuRepository;

    @Override
    public LoginUserInfo convert(SysUser sysUser) {
        LoginUserInfo loginUserInfo = new LoginUserInfo();
        loginUserInfo.setId(sysUser.getId());
        loginUserInfo.setLoginName(sysUser.getLoginName());
        loginUserInfo.setSysUser(sysUser);
        loginUserInfo.setRole(sysUser.getRoleId());
        List<SysMenu> menuList = menuRepository.querySysMenuByUserId(sysUser.getId(),sysUser.getRoleId().getRoleType());
        loginUserInfo.setMenuList(menuList);
        return loginUserInfo;
    }
}
