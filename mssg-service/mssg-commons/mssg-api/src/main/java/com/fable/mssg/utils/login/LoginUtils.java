package com.fable.mssg.utils.login;

/**
 * @author: yuhl Created on 14:26 2017/11/2 0002 登录常量类
 */
public class LoginUtils {

    public static final String USER_INFO_NULL = "80001"; // 用户登录信息为空

    public static final String AUTH_CODE_ERROR = "80002"; // 验证码校验失败

    public static final String GET_CERTIFICATE_FAIL = "80003"; // 获取证书失败

    public static final String CERTIFICATE_FORMAT_ERROR = "80004"; // 证书格式错误

    public static final String CERTIFICATE_CONTENT_ERROR = "80005"; // 证书内容错误

    public static final String USER_FORBIDDEN_ERROR = "80006"; // 用户被停用

    public static final String NAME_PASSWORD_ERROR = "80007"; // 用户名或密码错误

    public static final String ROLE_USER = "ROLE_USER"; // 登录用户的角色

    public static final String LOGIN_CACHE_NAME = "loginName"; // 缓存登录用户名

    public static final String CURRENT_USER_KEY = "current_user"; // 当前用户名

    public static final String VALIDATE_CODE = "verCode"; // 图片验证码

    public static final String CURRENT_ONLINE = "currentonline"; // 当前在线用户
}
