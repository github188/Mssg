package com.fable.mssg.service.user.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description  用户异常类
 * @Author xiejk 2017/11/20
 */
public class SysUserException extends RestApiException {

    public static final String USER_NOT_FOUND ="2001";  //用户不存在
    public static final String USER_LOGINNAME_ALREADY_EXIST ="2002";  //用户名存在
    public static final String USER_IDCARD_EXIST ="2003";  //用户身份证存在
    public static final String USER_INSERT_ERROR ="2004";  //新增异常
    public static final String ADMIN_CAN_NOT_DELETE ="2005";  //超级管理员不能被删除
    public static final String ADMIN_CAN_NOT_DISABLED ="2008";  //超级管理员不能被停用
    public static final String USER_DELETE_ERROR ="2006";  //用户删除异常
    public static final String UPDATE_USER_ERROR ="2007";  //用户更新异常
    public static final String DISABLE_USER_ERROR ="2009";  //停用用户异常
    public static final String USER_ENABLED ="2010";  //用户已经启用
    public static final String USER_ENABLE_ERROR ="2011";  //启用用户异常
    public static final String USER_RESET_ERROR ="2012";  //重置密码异常
    public static final String USER_ALREADY_DELECTED ="2013";  //用户已经删除
    public static final String USER_ALREADY_DISABLED ="2014";  //用户已经停用
    public static final String SLAVE_CAN_NOT_LOGIN ="2015";  //共享端用户不能登录
    public static final String MASTER_CAN_NOT_LOGIN ="2021";  //管理端用户不能登录
    public static final String USER_PWD_ERROR ="2016";
    public static final String USER_LOGINNAME_NOT_EXIST ="2017";
    public static final String USER_PASSWORD_ERROR ="2018";
    public static final String USER_ALREADY_LOGIN ="2019";
    public static final String USER_CAN_NOT_DELETE_SELF="2020";
    public static final String USER_OLD_PASSWORD_ERROR="2022";//用户原密码不正确
    public static final String USER_ISTOFFINE="2023";//被前置下线，30分钟之内不能登录
    public static final String USER_IS_APPROVING="2024";//用户在审核中
    public static final String USER_APPROVE_IS_REFUSED="2025";//用户审核被拒绝

    String code;

    public SysUserException(String code) {
        this.code = code;
    }

    @Override
    public String getErrorCode() {
        return this.code;
    }

}
