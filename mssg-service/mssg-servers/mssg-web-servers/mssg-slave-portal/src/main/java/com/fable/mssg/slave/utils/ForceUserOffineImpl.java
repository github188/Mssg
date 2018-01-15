package com.fable.mssg.slave.utils;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.service.login.ForceUserOffine;
import com.fable.mssg.service.user.exception.SysUserException;
import com.fable.mssg.slave.web.SlaveLoginController;
import com.fable.mssg.utils.login.LoginUtils;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Map;

/**
 * 强制用户下线
 */
@Slf4j
@Exporter(interfaces =ForceUserOffine.class )
public class ForceUserOffineImpl implements ForceUserOffine {


    public void forceUserOffline( SysUser user) {
            SlaveLoginController.sessionMap.get(user.getId());//获得sessionmap
            forceLogout(SlaveLoginController.sessionMap,user);  //强制下线
    }


    public void forceLogout(Map<String, HttpSession> sessionMap, SysUser sysUser) {
        HttpSession session = sessionMap.get(sysUser.getId());
        sessionMap.remove(sysUser.getId());
        session.invalidate();
    }


}
