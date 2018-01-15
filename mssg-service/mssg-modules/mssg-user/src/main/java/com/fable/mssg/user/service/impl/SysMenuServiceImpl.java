package com.fable.mssg.user.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.domain.usermanager.SysMenu;
import com.fable.mssg.user.repository.SysMenuRepository;
import com.fable.mssg.service.user.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * description 系统菜单接口实现类
 * @author xiejk 2017/11/11
 */

@Service
@Exporter(interfaces = SysMenuService.class)
public class SysMenuServiceImpl implements SysMenuService{

    @Autowired
    SysMenuRepository sysMenuRepository;

    @Override
    public List<SysMenu> findAllByMenuType(int menuType) {
        List<SysMenu> list= sysMenuRepository.findByMenuType(menuType);
        return list;
    }

    @Override
    public List<SysMenu> findAll() {
        List<SysMenu> list = sysMenuRepository.findAll();
        return list;
    }

    @Override
    public List<SysMenu> querySysMenuByUserId(String roleId, int menuType) {
        return sysMenuRepository.querySysMenuByUserId(roleId,menuType);
    }
}
