package com.fable.mssg.resource.web;

import com.fable.mssg.company.converter.CompanyConverter;
import com.fable.mssg.company.vo.VCompany;
import com.fable.mssg.domain.usermanager.SysMenu;
import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysRoleMenu;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.converter.RoleConverter;
import com.fable.mssg.resource.service.exception.RoleException;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.service.user.RoleService;
import com.fable.mssg.resource.vo.VRole;
import com.fable.mssg.service.user.SysMenuService;
import com.fable.mssg.service.user.SysRoleMenuService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description  用户角色控制器
 *
 * @author xiejk 2017/10/27
 */
@RestController
@RequestMapping("/role")
@Slf4j
@Secured(LoginUtils.ROLE_USER)
@Api(value = "角色管理制器", description = "角色管理制器")
public class RoleController {

    //用户角色接口操作对象
    @Autowired
    RoleService roleService;
    //用户角色装换类操作对象
    @Autowired
    RoleConverter roleConverter;
    @Autowired
    SysMenuService sysMenuService;
    @Autowired
    SysRoleMenuService sysRoleMenuService;
    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyConverter companyConverter;
    @Autowired
    SysUserService sysUserService;


    /**
     * 分页查询用户角色
     *
     * @param size 每页显示个数
     * @param page 当前页数
     * @return datatable
     */
    @ApiOperation(value = "分页角色信息", notes = "分页角色信息")
    @RequestMapping(value = "/findPageRole", method = RequestMethod.GET)
    public DataTable findPageRole(@RequestParam String size, @RequestParam String page) {
        Page<VRole> vlist = roleService.findPageRole(Integer.valueOf(size), Integer.valueOf(page) - 1).map(roleConverter);
        return DataTable.buildDataTable(vlist);
    }

    /**
     * 查询单个的role
     *
     * @param roleId
     * @return Role
     */
    @ApiOperation(value = "查询单个的role", notes = "查询单个的role")
    @RequestMapping(value = "/findOneRole", method = RequestMethod.GET)
    public SysRole findOneRole(@RequestParam String roleId) {
        return roleService.findOneRole(roleId);
    }

    @ApiOperation("查询角色配置菜单")
    @RequestMapping(value = "/findMenuByRole",method = RequestMethod.GET)
    public List<SysRoleMenu> getSysRoleMenu(@RequestParam String roleId){
        return sysRoleMenuService.findByRoleId(roleId);
    }

    /**
     * 修改用户角色
     *
     * @param
     * @return string
     */
    @ApiOperation(value = "修改用户角色", notes = "修改用户角色")
    @RequestMapping(value = "/updateRole", method = RequestMethod.POST)
    public void updateRole(@RequestBody SysRole role) {
        SysRole sysrole = roleService.findOneRole(role.getId());
        if(null==sysrole){
            throw new RoleException(RoleException.ROLE_NOT_FOUND);
        }
        if(sysrole.getRoleName().equals("admin")){
            throw new RoleException(RoleException.UPDATE_ADMIN_ERROR);
        }
        if(roleService.findRoleByRoleName(role.getRoleName(),role.getId())){
            throw new RoleException(RoleException.ROLE_NAME_ALREADY_EXIST);
        }
        if(roleService.findbyRoleCode(role.getRoleCode(),role.getId())){
            throw new RoleException(RoleException.ROLE_CODE_ALREADY_EXIST);
        }
        if (!roleService.updateRole(role)) {
            throw new RoleException(RoleException.UPDATE_ROLE_ERROR);
        }
    }

    /**
     * 删除用户角色  超级管理员不能被删除拥有所有的权限
     * @param roleId 用户角色id
     * @return string
     */
    @ApiOperation(value = "删除用户角色", notes = "删除用户角色")
    @RequestMapping(value = "/delRole", method = RequestMethod.GET)
    public void delRole(@RequestParam String roleId) {
        SysRole role = roleService.findOneRole(roleId);
        if (role.getRoleName().equals("admin")) {
            throw new RoleException(RoleException.DEL_ADMIN_ERROR);
        }
        List<SysUser> sysUsers=sysUserService.findAllUserByRoleId(roleId);
        if(sysUsers.size()!=0){
            throw  new RoleException(RoleException.ROLE_EXIST_USER);
        }
        if (!roleService.delRole(roleId)) {
            throw new RoleException(RoleException.DEL_ERROR);
        }
    }


    /**
     * 新增用户角色
     * @param
     * @param
     * @param
     * @return String string
     */
    @ApiOperation(value = "新增用户角色", notes = "新增用户角色")
    @RequestMapping(value = "/insertRole", method = RequestMethod.POST)
    public void insertRole(@RequestBody SysRole role) {
        if(roleService.findRoleByRoleName(role.getRoleName())){
            throw new RoleException(RoleException.ROLE_NAME_ALREADY_EXIST);
        }
        if(roleService.findbyRoleCode(role.getRoleCode())){
            throw  new RoleException(RoleException.ROLE_CODE_ALREADY_EXIST);
        }
        if (!roleService.updateRole(role)) {
            throw new RoleException(RoleException.INSERT_ERROR);
        }
    }

    /**
     * 根据角色类型查询全部的菜单
     * @param roleId
     * @return
     */
    @ApiOperation(value = "根据角色类型查询全部的菜单", notes = "根据角色类型查询全部的菜单")
    @RequestMapping(value = "/findAllMenu", method = RequestMethod.GET)
    public Map findAllMenu(@RequestParam String roleId) {
        SysRole role = roleService.findOneRole(roleId);
        if (role != null) {
            List<SysMenu> list;
            Map map = new HashMap();
            map.put("roleName", role.getRoleName());
            list = sysMenuService.findAllByMenuType(role.getRoleType());
            List<SysRoleMenu> srm = sysRoleMenuService.findByRoleId(role.getId());
            List<String> menuid = new ArrayList<>();
            for (SysRoleMenu s : srm) {
                menuid.add(s.getMenuId());//返回已经有权限的menuid
            }
            map.put("list", list);
            map.put("menuid", menuid);
            return map;
        } else {
            throw new RoleException(RoleException.ROLE_NOT_FOUND);
        }
    }


    /**
     * 配置菜单
     *
     * @param roleId
     * @param menuId
     * @return
     */
    @ApiOperation(value = "配置菜单", notes = "配置菜单")
    @RequestMapping(value = "/confingMenu", method = RequestMethod.GET)
    public void confingMenu(@RequestParam String roleId, @RequestParam String[] menuId) {
        List<SysRoleMenu> list = new ArrayList<>();
        SysRole role = roleService.findOneRole(roleId);
      /*  if (role.getRoleName().equals("admin")) {
            throw new RoleException(RoleException.ROLE_NAME_ERROR, "admin不允许配置");
        }*/
        //删除之前关系
        sysRoleMenuService.deleteByRoleId(roleId);
        for (String mid : menuId) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(mid);
            list.add(sysRoleMenu);
        }
        for (SysRoleMenu s : list) {
            if (!sysRoleMenuService.insertSysRoleService(s)) {
                throw new RoleException(RoleException.CONFIG_MENU_ERROR);
            }
        }
    }

    @ApiOperation(value = "查询全部角色", notes = "查询全部角色")
    @RequestMapping(value = "/findAllRole", method = RequestMethod.GET)
    public List<SysRole> findAllRole() {
        return  roleService.findAllRole();
    }

    @ApiOperation(value = "查询全部公司", notes = "查询全部公司")
    @RequestMapping(value = "/findAllCompany", method = RequestMethod.GET)
    public List<VCompany> findAllCompany() {
        return companyConverter.convert(companyService.findAll(0));
    }

}









