package com.fable.mssg.user.service.impl;


import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.service.user.RoleService;
import com.fable.mssg.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户角色接口实现类
 * @author xiejk  2017/10/27
 */
@Exporter(interfaces = RoleService.class)
@Service
public class RoleServiceImpl  implements  RoleService{

    //用户角色dao层操作对象
    @Autowired
    RoleRepository roleRepository;

    /**
     * 分页查询全部的用户角色
     * @param size 每页显示个数
     * @param page 当前页数
     * @return Page
     */
    @Override
    public Page<SysRole> findPageRole(int size, int page) {
        return  roleRepository.findAll(new PageRequest(page,size));
    }

    /**
     * 查询单个的role
     * @param roleId
     * @return Role
     */
    @Override
    public SysRole findOneRole(String roleId) {
        return roleRepository.findOne(roleId);
    }

    /**
     * 修改用户角色
     * @param role  用于角色对象
     * @return String
     */
    @Override
    @Transactional
    public boolean updateRole(SysRole role) {
        boolean flag=false;
        try {
            roleRepository.save(role);
            flag= true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  flag;
    }

    /**
     * 删除用户角色
     * @param roleId  用户角色id
     * @return string
     */
    @Override
    @Transactional
    public boolean delRole(String roleId) {
        try {
            roleRepository.delete(roleId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 查询共享端的角色信息
     * @return
     */
    @Override
    public List<SysRole> findAllByRoleType(int roletype) {
        return roleRepository.findAllByRoleType(roletype);
    }

    @Override
    public List<SysRole> findAllByRoleType(int roletype, String roleName) {
        return roleRepository.findAllByRoleType(roletype,roleName);
    }

    //查询全部的角色
    @Override
    public List<SysRole> findAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public List<SysRole> findAllRole(String roleName) {
        return roleRepository.findAllByRoleName(roleName);
    }

    @Override
    public boolean findbyRoleCode(String roleCode) {
       List list=roleRepository.findByRoleCode(roleCode);
        if(list.size()>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean findbyRoleCode(String roleCode, String id) {
        List<SysRole> list = roleRepository.findByRoleCode(roleCode, id);
        if(list.size()>0){
            return true;
        }else{
            return false;
        }
    }


    @Override
    public boolean findRoleByRoleName(String roleName) {
        List list=roleRepository.findAllByRoleName(roleName);
        if(list.size()>0){
            return true;
        }else{
            return false;
        }
    }

    //名称不重复 不是自己
    @Override
    public boolean findRoleByRoleName(String roleName, String id) {
        List<SysRole> list=roleRepository.findAllByRoleName(roleName,id);
        if(list.size()>0){
            return true;
        }else{
            return false;
        }
    }
}
