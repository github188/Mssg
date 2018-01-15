package com.fable.mssg.resource.web;

import com.fable.framework.core.config.Address;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.dsmanager.DeviceVist;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.converter.OnLineLogConverter;
import com.fable.mssg.resource.converter.dsConvert.DeviceVisitConverter;
import com.fable.mssg.resource.service.exception.OnlineLogException;
import com.fable.mssg.resource.vo.VOnLineLog;
import com.fable.mssg.service.login.ForceUserOffine;
import com.fable.mssg.service.login.LoginService;
import com.fable.mssg.service.onlinelog.DeviceVisitService;
import com.fable.mssg.service.onlinelog.OnLineLogService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import com.fable.mssg.vo.VDeviceVist;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

/**
 * description  用户访问记录控制器
 *
 * @author xiejk 2017/10/26
 */

@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/onLineLog")
@Slf4j
@Api(value = "用户审计控制器", description = "用户审计控制器")
public class OnlineLogController {

    //用户访问记录接口操作对象
    @Autowired
    private OnLineLogService onLineLogService;
    //用户访问记录转换类操作对象
    @Autowired
    private OnLineLogConverter onLineLogConverter;
    @Autowired
    private DeviceVisitConverter deviceVisitConverter;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    DeviceVisitService deviceVisitService;

    @Autowired
    ServiceRegistry registry;

    @Value("${com.fable.mssg.proxy.sip.remoteServer}")
    Address address;


    /**
     * 条件查询用户访问记录表  强制转换时间  格式为yyyy-MM-dd HH:mm:ss  用户都为共享端
     *
     * @param userName    用户名称
     * @param companyName 公司名称
     * @param loginTime   登录时间
     * @param logoutTime  下线时间
     * @return List<OnLineLog>
     */
    @ApiOperation(value = "条件查询用户访问记录表（共享端）", notes = "条件查询用户访问记录表（共享端）")
    @RequestMapping(value = "/findAllOnLineLogByCondition", method = RequestMethod.GET)
    public DataTable<VOnLineLog> findAllOnLineLogByCondition(String userName, String companyName,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size,
                                                             String loginTime, String logoutTime) {

        //转换时间
        Timestamp t1 = null;
        if (loginTime != null && !loginTime.equals("")) {
            t1 = Timestamp.valueOf(loginTime);
        }
        Timestamp t2 = null;
        if (logoutTime != null && !logoutTime.equals("")) {
            t2 = Timestamp.valueOf(logoutTime);
        }
        Page<OnLineLog> olist = onLineLogService.findAllOnLineLogByCondition(userName, companyName, page, size, t1, t2);
        return DataTable.buildDataTable(onLineLogConverter.convert(olist));
    }

   /* *//**
     * 停用用户   现在不做了
     * @param userId
     * @return message
     *//*
    @ApiOperation(value = "停用用户",notes = "停用用户")
    @RequestMapping(value = "/forceUserDisabled",method = RequestMethod.GET)
    public String  forceUserDisabled(@RequestParam String userId){
        String message;
        if (onLineLogService.forceUserDisable(userId)){
            message="停用成功！";
        }else{
            message="停用失败";
        }
        return message;
    }*/


    /**
     * 根据访问记录查询访问明细
     *
     * @param onLineId 访问记录id
     * @return List<VDeviceVist>
     */
    @ApiOperation(value = "在线明细", notes = "在线明细")
    @RequestMapping(value = "findAllDeviceVistByonLineId", method = RequestMethod.GET)
    public DataTable<VDeviceVist> findAllDeviceVistByonLineId(@RequestParam String onLineId,
                                                              @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        OnLineLog online = onLineLogService.findOneOnline(onLineId);
        if (online == null) {
            throw new OnlineLogException(OnlineLogException.ONLINE_NOT_FOUND);
        }
        //分页查询devicevisit
        Page<DeviceVist> list = deviceVisitService.findPageByOnlineLogid(onLineId, page, size);
        return DataTable.buildDataTable(deviceVisitConverter.convert(list));
    }


    @ApiOperation(value = "强制用户下线", notes = "强制用户下线")
    @RequestMapping(value = "/forceUserOffline", method = RequestMethod.GET)
    public void forceUserOffline(@RequestParam String userId, String id){
        SysUser user = sysUserService.findOneUserByUserId(userId);
        user.setLoginState(0);//未登录状态
        OnLineLog onLineLog = onLineLogService.findOneOnline(id);
        onLineLog.setIstOffline(1);//强制下载
        onLineLog.setLogoutTime(new Timestamp(System.currentTimeMillis()));//下线时间
        try {
            registry.lookup(address.getHost(), address.getPort(), true,ForceUserOffine.class).forceUserOffline(user);
            //计算访问明细表的时间并更新
            List<DeviceVist> dlist=deviceVisitService.findAllByOnlineLogId(id);
            if(dlist.size()!=0){
                for(DeviceVist d:dlist){
                    d.setVistLong((new Timestamp(System.currentTimeMillis()).getTime()-d.getVistTime().getTime())/1000);//设置访问时长
                   deviceVisitService.updateVistLong(d);//保存
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("链接slave模块失败");
        }
        sysUserService.updateSysUser(user);//保存
        onLineLogService.addOnlineLog(onLineLog);//保存
    }

}









