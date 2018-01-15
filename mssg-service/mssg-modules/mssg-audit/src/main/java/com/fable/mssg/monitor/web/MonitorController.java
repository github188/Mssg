package com.fable.mssg.monitor.web;

import com.fable.mssg.monitor.service.MonitorService;
import com.fable.mssg.monitor.vo.*;
import com.fable.mssg.utils.login.LoginUtils;
import org.hyperic.sigar.Sigar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/10/25
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @RequestMapping(value = "/resSurvey", method = RequestMethod.GET)
    public SystemSurvey getResourceSurvey() {
        return monitorService.getSystemSurvey();
    }

    @RequestMapping(value = "/topSubUnit", method = RequestMethod.GET)
    public List<TopSubCompany> getTopSubCompany(@RequestParam Integer days) {
        return monitorService.getTopSubCompany(days);

    }

    /**
     * master的最热资源查询
     * @param days
     * @return
     */
    @RequestMapping(value = "/popResource", method = RequestMethod.GET)
    public List<TopResSurvey> getTopSubmitRes(@RequestParam Integer days) {
        return monitorService.getTopResource(days, MonitorService.TYPE_SUBMIT,5);
    }

    /**
     * 观看最多次数的资源(NOT_USED)
     * @param days
     * @return
     */
    @RequestMapping(value = "/topViewResource" , method = RequestMethod.GET)
    public List<TopResSurvey> getPopResource(@RequestParam Integer days) {
        return monitorService.getTopResource(days, MonitorService.TYPE_VIEW,5);
    }

    @RequestMapping(value = "/topViewUser" ,method = RequestMethod.GET)
    public List<TopViewUser> getTopViewUser(@RequestParam Integer days){

        return monitorService.getTopViewUser(days,5);
    }

    @RequestMapping(value = "/getTraffic",method = RequestMethod.GET)
    public Traffic getTraffic(){
        return monitorService.getTraffic();
    }

}
