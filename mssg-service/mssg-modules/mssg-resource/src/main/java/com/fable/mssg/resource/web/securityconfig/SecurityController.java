package com.fable.mssg.resource.web.securityconfig;

import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.securityconfig.SecurityConfig;
import com.fable.mssg.resource.service.securityconfig.SecurityConfigService;
import com.fable.mssg.utils.login.LoginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description  安全配置控制器
 * @author xiejk 2017/11/11
 */
@Api(value = "安全配置控制器",description = "安全配置控制器")
@RestController
@RequestMapping("/ser")
@Slf4j
@Secured(LoginUtils.ROLE_USER)
public class SecurityController {

    @Autowired
    SecurityConfigService securityConfigService;

    /**
     * 查询所有安全配置
     * @return
     */
    @ApiOperation(value = "查询所有安全配置",notes = "查询所有安全配置")
    @RequestMapping(value = "/findAll" ,method = RequestMethod.GET)
    public Map findAll(){
        List<SecurityConfig> list = securityConfigService.findAll();
        Map map=new HashMap<>();
        for (SecurityConfig s:list){
            String codekey=s.getSecCode();
            map.put(codekey,s);
        }
        return map;
    }

    /**
     * 更改安全配置
     * @param
     * @return
     */
    @ApiOperation(value = "更改安全配置",notes = "更改安全配置")
    @RequestMapping(value = "/updateSecurityConfig" ,method = RequestMethod.POST)
    public void updateSecurityConfig(HttpServletRequest request, @RequestBody SecurityConfig[] securityConfigs){
        for (SecurityConfig  sec : securityConfigs){
            SecurityConfig s=securityConfigService.findOneSecurity(sec.getId());//获得配置
            s.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
            s.setUpdateUser(loginUserInfo.getSysUser().getId());//更新人
            s.setUpdateTime(new Timestamp(System.currentTimeMillis()));//更新时间
            s.setStatus(sec.getStatus());//状态
            s.setSecPolicy(sec.getSecPolicy());//格式
            securityConfigService.updateSecurity(s);
        }
    }

}









