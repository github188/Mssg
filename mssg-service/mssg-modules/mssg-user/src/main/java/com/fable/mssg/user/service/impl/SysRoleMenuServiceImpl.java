package com.fable.mssg.user.service.impl;

import com.fable.mssg.domain.usermanager.SysRoleMenu;
import com.fable.mssg.user.repository.SysMenuRoleRepository;
import com.fable.mssg.service.user.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 系统角色菜单对应表接口实现类
 * @author  xiejk 2017/11/11
 */
@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {
    @Autowired
    SysMenuRoleRepository sysMenuRoleRepository;



    @Override
    public boolean insertSysRoleService(SysRoleMenu sysRoleMenu) {
        boolean flag;
        try {
            flag= true;
            sysMenuRoleRepository.save(sysRoleMenu);
        } catch (Exception e) {
            e.printStackTrace();
            flag=  false;
        }
        return  flag;
    }


    //根据角色名称查询目录
    @Override
    public List<SysRoleMenu> findByRoleId(String roleId) {
        return sysMenuRoleRepository.findByRoleId(roleId);
    }

    /**
     * 删除对应关系
     * @param roleId
     */
    @Override
    @Transactional
    public void deleteByRoleId(String roleId) {
        sysMenuRoleRepository.deleteByRoleId(roleId);
    }
}
