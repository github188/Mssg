package com.fable.mssg.slave.utils;


import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.login.service.impl.LoginServiceImpl;
import com.fable.mssg.service.onlinelog.OnLineLogService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.slave.web.SlaveLoginController;
import com.fable.mssg.utils.login.LoginUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.sql.Timestamp;

/**
 * 用户session监听方法
 */
@WebListener
public class SlaveMssgListener implements HttpSessionListener {

    @Autowired
    SysUserService sysUserService;
    @Autowired
    OnLineLogService onLineLogService;

    //session 建立的方法
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {


    }


    //session消失的方法
    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        LoginUserInfo loginUserInfo=(LoginUserInfo) httpSessionEvent.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        if(loginUserInfo!=null){
            sysUserService.updateLoginState(loginUserInfo.getSysUser().getId());//更新用户状态
            onLineLogService.updateLoginout(new Timestamp(System.currentTimeMillis()),loginUserInfo.getOnLineLog().getId());//保存下线时间
            SlaveLoginController.sessionMap.remove(loginUserInfo.getSysUser().getId());
        }
    }



}
