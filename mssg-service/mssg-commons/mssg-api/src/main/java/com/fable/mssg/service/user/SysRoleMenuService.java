package com.fable.mssg.service.user;

import com.fable.mssg.domain.usermanager.SysRoleMenu;

import java.util.List;

/**
 * 系统角色菜单对应表接口
 * @author  xiejk 2017/11/11
 */
public interface SysRoleMenuService {


    boolean insertSysRoleService(SysRoleMenu sysRoleMenu);

    List<SysRoleMenu> findByRoleId(String roleId);

    //删除菜单角色配置信息
    void deleteByRoleId(String roleId);






}
