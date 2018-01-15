package com.fable.mssg.resource.service.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description  角色异常类
 * @Author xiejk 2017/11/20
 */
public class RoleException extends RestApiException {

    public static final String ROLE_NOT_FOUND ="1401";  //角色不存在
    public static final String UPDATE_ROLE_ERROR ="1402";  //更新角色异常
    public static final String DEL_ADMIN_ERROR ="1403";  //超级管理员不能被删除
    public static final String DEL_ERROR ="1404";  //删除角色异常
    public static final String INSERT_ERROR ="1405";  //新增角色异常
    public static final String CONFIG_MENU_ERROR ="1406";  //配置菜单异常
    public static final String ROLE_EXIST_USER ="1408";  //角色下存在用户
    public static final String UPDATE_ADMIN_ERROR ="1409";  //admin不能被修改
    public static final String ROLE_NAME_ALREADY_EXIST ="1410";  //角色名称已经存在
    public static final String ROLE_CODE_ALREADY_EXIST ="1411";  //角色编码已经存在




    String code;

    public RoleException(String code) {
        this.code = code;
    }

    @Override
    public String getErrorCode() {
        return this.code;
    }

}
