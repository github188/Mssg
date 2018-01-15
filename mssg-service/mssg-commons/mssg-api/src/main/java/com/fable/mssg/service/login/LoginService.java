package com.fable.mssg.service.login;

import com.fable.mssg.bean.info.SessionInfo;
import com.fable.mssg.domain.usermanager.SysMenu;
import com.fable.mssg.domain.usermanager.SysUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author: yuhl Created on 17:05 2017/11/1 0001
 */
public interface LoginService {

    /**
     * 用户登录
     * @param loginName
     * @param password
     */
    SessionInfo login(HttpServletRequest request,String loginName, String password, SysUser sysUser);

    /**
     * slave远程登录
     * @param ip
     * @param loginName
     * @param password
     * @param sysUser
     * @return
     */
    /*SessionInfo login(String ip,String loginName, String password, SysUser sysUser);*/

 /*   *//**
     * 用户强制下线
     * @param sysUser
     *//*
    void forceLogout(Map<String, HttpSession> sessionMap, SysUser sysUser);*/

    /**
     * 根据userId查询菜单列表
     * @param userId
     * @return
     */
    List<SysMenu> queryMenuByUserId(String userId,int menuType);

}
