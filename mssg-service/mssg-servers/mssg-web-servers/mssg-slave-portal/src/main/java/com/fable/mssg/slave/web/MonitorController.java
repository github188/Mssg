package com.fable.mssg.slave.web;

import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.service.slave.SlaveMonitorService;
import com.fable.mssg.slave.web.exception.VisitMasterException;
import com.fable.mssg.utils.login.LoginUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author wangmeng 2017/11/3
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/monitor")
@Slf4j
public class MonitorController {

    @Autowired
    private ServiceRegistry registry;

    @Value("${remote.proxy.server.ip}")
    private String remoteIp;

    @Value("${remote.proxy.server.port}")
    private int remotePort;

    private SlaveMonitorService slaveMonitorService;

    @PostConstruct
    public void init() {
        try {
            slaveMonitorService = registry.lookup(remoteIp, remotePort, SlaveMonitorService.class);
        } catch (Exception e) {
            log.error("远程调用失败",e);
        }

    }

    @RequestMapping(value = "/getResSurvey", method = RequestMethod.GET)
    @ApiOperation(value = "系统概况")
    public Map getResourceSurvey(HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        return slaveMonitorService.getSysSurvey(sysUser.getComId().getId());
    }

    @RequestMapping(value = "/getTopViewUser", method = RequestMethod.GET)
    @ApiOperation(value = "查询前N天本单位的观看次数最多的用户")
    public List<Map> getTopViewUser(Integer days, HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        return slaveMonitorService.getTopViewUser(days, 10, sysUser.getComId().getId());
    }

    @RequestMapping(value = "/getPopResource", method = RequestMethod.GET)
    @ApiOperation(value = "查询前N天本单位浏览最多的资源")
    public List<Map> getPopResource(Integer days, HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        return slaveMonitorService.getPopResource(days, 5, sysUser.getComId().getId());

    }


}
