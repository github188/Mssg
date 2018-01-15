package com.fable.mssg.service.user;

import com.fable.mssg.domain.usermanager.SysMenu;

import java.util.List;

/**
 * description  系统目录接口
 * @author xiejk 2017/11/11
 */
public interface SysMenuService {


    /**
     * 根据菜单类型查找
     * @return
     */
    List<SysMenu> findAllByMenuType(int menuType);

    List<SysMenu> findAll();

    List<SysMenu> querySysMenuByUserId(String roleId,int menuType);

}
