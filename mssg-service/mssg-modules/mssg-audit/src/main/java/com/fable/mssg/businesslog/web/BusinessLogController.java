package com.fable.mssg.businesslog.web;

import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.businesslog.converter.BizLogConverter;
import com.fable.mssg.domain.businesslog.BusinessLog;
import com.fable.mssg.service.businesslog.BusinessLogService;
import com.fable.mssg.businesslog.vo.VBusinessLog;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.bean.AuthInfo;
import com.fable.mssg.service.datasource.DataSourceAuthService;
import com.fable.mssg.service.login.SessionService;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;

/**
 * @Description
 * @Author wangmeng 2017/09/01
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/businesslog")
public class BusinessLogController {
    //默认分页大小
    private static final String DEF_PAGE_SIZE = "10";
    @Autowired
    private BizLogConverter bizLogConverter;
    @Autowired
    private BusinessLogService businessLogService;
    @Autowired
    private DataSourceAuthService dataSourceAuthService;


    @RequestMapping(value = "/listrealtime",method = RequestMethod.GET)
    @ApiOperation("查询实时")
    public DataTable findRealTime(@RequestParam(defaultValue = "1") String page,
                                  @RequestParam(defaultValue = DEF_PAGE_SIZE) String size) {

//        String sessionId=req.getSession().getId();
//        Timestamp lastVisit=LAST_VISIT_TIME.get(sessionId);
//        List<VBusinessLog> result = bizLogConverter.create(businessLogService.findAllRealTime());

        return DataTable.buildDataTable(businessLogService.findByTypeAndPage(
                Integer.parseInt(size), Integer.parseInt(size), 1));

    }

    @RequestMapping(value = "/listhistory",method = RequestMethod.GET)
    @ApiOperation("查询历史")
    public DataTable findHistory(@RequestParam(defaultValue = DEF_PAGE_SIZE) String size) {
        Page<VBusinessLog> vBusinessLogs = businessLogService.findByTypeAndPage(0, Integer.parseInt(size), VBusinessLog.HISTORY).map(bizLogConverter);

        return DataTable.buildDataTable(vBusinessLogs);


    }

    @RequestMapping(value = "/listbypage",method = RequestMethod.GET)
    @ApiOperation("分页查询")
    public DataTable findByPageAndType(@RequestParam(defaultValue = "1") String page,
                                       @RequestParam(defaultValue = DEF_PAGE_SIZE) String size,
                                       @RequestParam(defaultValue = "2") String type) {
        Page<VBusinessLog> vBusinessLogs = businessLogService.findByTypeAndPage(Integer.parseInt(page),
                Integer.parseInt(size), Integer.parseInt(type)).map(bizLogConverter);

        return DataTable.buildDataTable(vBusinessLogs);
    }


    @RequestMapping(value = "/listhisbycondition",method = RequestMethod.GET)
    @ApiOperation("分页条件查询")
    public Object findHisByTimeAndUnit(@RequestParam(defaultValue = "1") String page,
                                       @RequestParam(defaultValue = DEF_PAGE_SIZE) String size,
                                       String startTime, String endTime, String unit) {
        Timestamp start = null, end = null;
        if (startTime != null && !"".equals(startTime)) {
            try {
                start = Timestamp.valueOf(startTime);
            } catch (Exception e) {
                return "时间参数错误";
            }
        }
        if (endTime != null && !"".equals(endTime)) {
            try {
                end = Timestamp.valueOf(endTime);
            } catch (Exception e) {
                return "时间参数错误";
            }
        }

        Page<VBusinessLog> vBusinessLogs = businessLogService.findByConditions(Integer.parseInt(page),
                Integer.parseInt(size), start, end, unit).map(bizLogConverter);


        return DataTable.buildDataTable(vBusinessLogs);
    }

    @RequestMapping("/operate")
    @ApiOperation(value = "操作")
    public boolean recordBizLog(String type, String dsId, HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();

        AuthInfo authInfo = dataSourceAuthService.isAuth(sysUser.getUserName(), dsId, Integer.parseInt(type), null, null, null, false);
        if (authInfo.isAuth()) {
            BusinessLog businessLog = new BusinessLog();
            businessLog.setOperateType(Integer.parseInt(type));
            businessLog.setCompanyId(sysUser.getComId());
            businessLog.setVisitType(BusinessLog.HISTORY);
            businessLog.setDeviceId(new DataSource(dsId));
            businessLog.setStartTime(new Timestamp(System.currentTimeMillis()));
            businessLog.setVisitUser(sysUser);
            businessLogService.save(businessLog);
            return true;
        }
        return false;
    }

}
