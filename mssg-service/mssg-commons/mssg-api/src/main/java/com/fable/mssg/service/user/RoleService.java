package com.fable.mssg.service.user;
import com.fable.mssg.domain.usermanager.SysRole;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * description  用户角色接口
 * @author xiejk 2017/10/26
 */
public interface RoleService {

    /**
     * 分页查询全部的用户角色
     * @param size 每页显示个数
     * @param page 当前页数
     * @return page
     */
    Page<SysRole> findPageRole(int size, int page);

    //查询单个用户
    SysRole findOneRole(String roleId);

    /**
     * 修改用户  新增用户角色
     * @param role  用于角色对象
     * @return  string
     */
    boolean updateRole(SysRole role);

    /**
     * 删除用户角色
     * @param roleId  用户角色id
     * @return string
     */
    boolean delRole(String roleId);

    List<SysRole> findAllByRoleType(int roletype);

    List<SysRole> findAllByRoleType(int roletype,String roleName);

    List<SysRole> findAllRole();

    List<SysRole> findAllRole(String roleName);

    //根据角色编码判断是否存在
    boolean findbyRoleCode(String roleCode);

    boolean findbyRoleCode(String roleCode,String id);

    //根据角色名称判断是否存在
    boolean findRoleByRoleName(String roleName);

    boolean findRoleByRoleName(String roleName,String id);
}
