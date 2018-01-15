package com.fable.mssg.slave.web;

import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.company.converter.CompanyConverter;
import com.fable.mssg.company.vo.VCompany;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.service.user.ApprListService;
import com.fable.mssg.service.user.RoleService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.service.user.exception.SysUserException;
import com.fable.mssg.slave.vo.VSysUser;
import com.fable.mssg.slave.web.exception.VisitMasterException;
import com.fable.mssg.user.converter.SysUserConverter;
import com.fable.mssg.user.vo.SysUserPwd;
import com.fable.mssg.utils.MD5Utils;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/10/27
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/user")
@Slf4j
@Api(value = "slave用户管理", description = "slave用户管理")
public class UserController {



    @Autowired
    private ServiceRegistry registry;

    @Autowired
    SysUserConverter sysUserConverter;

    @Autowired
    CompanyConverter companyConverter;
    @Value("${remote.proxy.server.ip}")
    private String remoteIp;

    @Value("${remote.proxy.server.port}")
    private int remotePort;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    RoleService roleService;
    @Autowired
    CompanyService companyService;
    @Autowired
    ApprListService apprListService;

    @PostConstruct
    public void init(){
        try {
            sysUserService=registry.lookup(remoteIp, remotePort, true,SysUserService.class);
            roleService=registry.lookup(remoteIp, remotePort,true,RoleService.class);
            companyService=registry.lookup(remoteIp, remotePort, true,CompanyService.class);
            apprListService=registry.lookup(remoteIp, remotePort, true,ApprListService.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("链接master服务器异常");
        }
    }



    /**
     * 新增用户信息
     * @param
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ApiOperation(value = "新增用户信息", notes = "新增用户信息")
    public void addUser(HttpServletRequest request, @RequestBody SysUser sysUser) {
        if(!sysUserService.findUserByUserIDCard(sysUser.getIdCard()))
        {
            throw new SysUserException(SysUserException.USER_IDCARD_EXIST);
        }
        if(!sysUserService.findUserByUserName(sysUser.getLoginName())){
            throw new SysUserException(SysUserException.USER_LOGINNAME_ALREADY_EXIST);
        }
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        sysUser.setCreateUser(loginUserInfo.getSysUser().getId());//创建人
        sysUser.setCreateTime(new Timestamp(System.currentTimeMillis()));//创建时间
        sysUser.setPassword(MD5Utils.getMD5Value(sysUser.getPassword()));//密码转换成md5
        String companyId=loginUserInfo.getSysUser().getComId().getId();//获得公司id
        Company company=new Company(companyId);
        sysUser.setComId(company);//设置所在的公司
        sysUser.setSalt("11");//设置加密方式
        sysUser.setState(0);//停用状态
        sysUser.setDeleteFlag(0);//未删除
        sysUser.setLoginState(0);//未登录
        sysUser.setSipId(sysUser.getIdCard()+"aa");//设置sipid
        sysUserService.insertSysUser(sysUser);
    }


    @ApiOperation(value = "删除用户", notes = "删除用户")
    @RequestMapping(value = "/delete",method = RequestMethod.GET)
    public void deleteUser(HttpServletRequest request,@RequestParam String userId) {
        SysUser user=sysUserService.findOneUserByUserId(userId);
        if(user==null){
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        if(loginUserInfo.getSysUser().getId().equals(userId)){//不能删除自己
            throw new SysUserException(SysUserException.USER_CAN_NOT_DELETE_SELF);
        }
        if(!sysUserService.delSysUser(userId)){
            throw new SysUserException(SysUserException.USER_DELETE_ERROR);
        }
    }


    /**
     * 根据公司id  分页查询本单位用户信息
     * @param request
     * @return
     */
    @ApiOperation(value = "根据公司id分页查询本单位用户信息", notes = "根据公司id分页查询本单位用户信息")
    @RequestMapping(value = "/listComUser", method = RequestMethod.GET)
    public DataTable findAll(HttpServletRequest request, String size, String page, String loginName, String userName, String roleId) {
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        String companyId=loginUserInfo.getSysUser().getComId().getId();//获得公司id
        Page<SysUser> sysUsers = sysUserService.findUserByCompany(companyId,loginName,userName,roleId,Integer.valueOf(size),Integer.valueOf(page)-1);
        List<VSysUser> vSysUsers = new ArrayList<>();
        for (SysUser sysUser : sysUsers) {
            vSysUsers.add(VSysUser.convert(sysUser));
        }
        DataTable dataTable=new DataTable();
        dataTable.setRecordsTotal(sysUsers.getTotalElements());
        dataTable.setRecordsFiltered(sysUsers.getTotalElements());
        dataTable.setData(vSysUsers);
        return dataTable;
    }

    //查询单个的
    @ApiOperation(value = "查询单个的用户信息", notes = "查询单个的用户信息")
    @RequestMapping(value = "/findOneUser", method = RequestMethod.GET)
    public VSysUser findOneUser(String userId){
        SysUser user;
        user =sysUserService.findOneUserByUserId(userId);
        return VSysUser.convert(user);
    }


    @ApiOperation(value = "编辑用户", notes = "编辑用户")
    @RequestMapping(value = "/modify",method = RequestMethod.POST)
    public void modifyUser(HttpServletRequest request, @RequestBody SysUser sysUser) {
        if(sysUser==null){
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        if(!sysUserService.findUserByUserIDCard(sysUser.getIdCard(),sysUser.getId())){
            throw new SysUserException(SysUserException.USER_IDCARD_EXIST);
        }
        if(!sysUserService.findUserByUserName(sysUser.getLoginName(),sysUser.getId())){
            throw new SysUserException(SysUserException.USER_LOGINNAME_ALREADY_EXIST);
        }
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser user = sysUserService.findOneUserByUserId(sysUser.getId());//查询到创建时间
        if(user.getLoginName().equals("admin")){
            throw new SysUserException(SysUserException.UPDATE_USER_ERROR);
        }
        user.setUpdateUser(loginUserInfo.getSysUser().getId());//更新人
        user.setUpdateTime(new Timestamp(System.currentTimeMillis()));//更新时间
        user.setLoginName(sysUser.getLoginName());
        user.setUserName(sysUser.getUserName());
        user.setIdCard(sysUser.getIdCard());
        user.setRoleId(sysUser.getRoleId());
        user.setPosition(sysUser.getPosition());
        user.setTelphone(sysUser.getTelphone());
        user.setCellPhoneNumber(sysUser.getCellPhoneNumber());
        user.setComId(sysUser.getComId());
        user.setDescription(sysUser.getDescription());
        if(!sysUserService.updateSysUser(user)){
            throw new SysUserException(SysUserException.UPDATE_USER_ERROR);
        }
    }

    //查询说有共享端的信息 不包括单位管理员
    @RequestMapping(value = "findRoleExceptManager",method = RequestMethod.GET)
    @ApiOperation(value = "查询所有的共享端角色信息", notes = "查询所有的共享端角色信息")
    public SysRole findRoleExceptManager(){
        List<SysRole> list;
        list= roleService.findAllByRoleType(2,"单位管理员");
        SysRole sysRole=list.get(0);
        return  sysRole;
    }



    @RequestMapping(value = "findAllRoleRegister",method = RequestMethod.GET)
    @ApiOperation(value = "查询所有的共享端角色信息", notes = "查询所有的共享端角色信息")
    public List<SysRole> findAllRoleRegister(){
        List<SysRole> list= roleService.findAllByRoleType(2);//共享端
        return  list;
    }


    //登录用户本单位
    @RequestMapping(value = "findAllCompany",method = RequestMethod.GET)
    @ApiOperation(value = "查询所有的公司", notes = "查询所有的公司")
    public Company findAllCompany(HttpServletRequest request){
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        Company company= companyService.findById(loginUserInfo.getSysUser().getComId().getId());
        return  company;
    }

    //全部单位共享端的用户
    @RequestMapping(value = "findAllCompanys",method = RequestMethod.GET)
    @ApiOperation(value = "查询所有的公司", notes = "查询所有的公司")
    public List<VCompany> findAllCompanys(){
        //查询共享端单位
        List<Company> list = companyService.findAllByComTypeAndStatus(2,0);
        return  companyConverter.convert(list);
    }

    //重置密码
    @ApiOperation(value = "重置密码", notes = "重置密码")
    @RequestMapping(value = "restPassword",method = RequestMethod.POST)
    public void restPassword(HttpServletRequest request, @RequestBody SysUser sysUser) {
        SysUser user =sysUserService.findOneUserByUserId(sysUser.getId());
        if(user==null){
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        user.setUpdateTime(new Timestamp(System.currentTimeMillis()));//更新时间
        user.setUpdateUser(loginUserInfo.getSysUser().getId());//更新人
        user.setPassword(MD5Utils.getMD5Value(sysUser.getPassword()));
        if(!sysUserService.updateSysUser(user)){
            throw new SysUserException(SysUserException.USER_RESET_ERROR);
        }
    }

    //启用用户
    @ApiOperation(value = "启用用户", notes = "启用用户")
    @RequestMapping(value = "enableUser",method = RequestMethod.GET)
    public void enableUser(HttpServletRequest request,@RequestParam String userId) {
        SysUser user =  sysUserService.findOneUserByUserId(userId);
        if(user==null){
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        user.setUpdateTime(new Timestamp(System.currentTimeMillis()));//更新时间
        user.setUpdateUser(loginUserInfo.getSysUser().getId());//更新人
        user.setState(0);//启用状态
        if(!sysUserService.updateSysUser(user)){
            throw new SysUserException(SysUserException.USER_ENABLE_ERROR);
        }
    }

    //停用用户
    @ApiOperation(value = "停用用户", notes = "停用用户")
    @RequestMapping(value = "disabled",method = RequestMethod.GET)
    public void disabled(HttpServletRequest request,@RequestParam String userId) {
        SysUser user =  sysUserService.findOneUserByUserId(userId);
        if(user==null){
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        if(user.getLoginState()==1){
            HttpSession session=SlaveLoginController.sessionMap.get(user.getId());
            SlaveLoginController.sessionMap.remove(user.getId());
            if(session!=null){
                session.invalidate();//清除session
            }
            user.setLoginState(0);//登录状态
        }
        //设置用户状态
        user.setState(1);//停用用户
        user.setUpdateTime(new Timestamp(System.currentTimeMillis()));//更新时间
        user.setUpdateUser(loginUserInfo.getSysUser().getId());//更新人
        if(!sysUserService.updateSysUser(user)){
            throw new SysUserException(SysUserException.DISABLE_USER_ERROR);
        }
    }


    //证书下载功能
    @RequestMapping("/downloadCertificate")
    public void downloadCertificate(@RequestParam String userId) {
        try {
            //TODO

        } catch (RemoteAccessException e) {
            log.error("访问master失败", e);
            throw new VisitMasterException("1");

        } catch (Exception e) {
            log.error("master内部异常", e);
            throw new VisitMasterException("2");
        }


    }


    /**
     * 导入用户  只能导入2003版的表格
     * @param userfile
     * @return
     */
    @ApiOperation(value = "导入用户",notes = "导入用户")
    @RequestMapping(value = "leadIn",method = RequestMethod.POST)
    public void leadIn(@RequestParam MultipartFile userfile,HttpServletRequest request) {
        if (!userfile.isEmpty()) {
            SysRole role= null;
            try {
                role = roleService.findAllRole("单位访问员").get(0);//单位访问员
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<SysUser> sysUsers = new ArrayList<>();
            HSSFWorkbook workbook = null;
            try {
                workbook = new HSSFWorkbook(userfile.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            HSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                SysUser sysUser = new SysUser();
                sysUser.setCreateTime(new Timestamp(System.currentTimeMillis()));//创建时间
                LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
                sysUser.setCreateUser(loginUserInfo.getSysUser().getId());//创建人
                sysUser.setComId(loginUserInfo.getSysUser().getComId());//设置所在的公司
                sysUser.setUserName(getString(row.getCell(0)));//设置用户名称
                sysUser.setCellPhoneNumber(String.valueOf(getDouble(row.getCell(1)).longValue()));//手机号码
                sysUser.setPassword(MD5Utils.getMD5Value(getString(row.getCell(2))));//用户初始密码
                sysUser.setIdCard(getString(row.getCell(3)));
                sysUser.setLoginName(getString(row.getCell(4)));
                sysUser.setTelphone(String.valueOf(getDouble(row.getCell(5)).longValue()));//办公电话
                sysUser.setDescription(getString(row.getCell(6)));//描述
                sysUser.setPosition(getString(row.getCell(7)));//职务

                sysUser.setRoleId(role);//设置角色  只有一个角色  单位访问员
                sysUser.setSalt("11");//设置加密方式
                sysUser.setState(0);//正常状态
                sysUser.setDeleteFlag(0);//未删除
                sysUser.setLoginState(0);//未登录
                sysUser.setSipId(getString(row.getCell(3))+"aa");//sip id
                sysUsers.add(sysUser);//

            }
            for (SysUser s:sysUsers){

                //相当于新增，要判断身份证跟登录名
                if(!sysUserService.findUserByUserIDCard(s.getIdCard()))
                {
                    throw new SysUserException(SysUserException.USER_IDCARD_EXIST);
                }
                if(!sysUserService.findUserByUserName(s.getLoginName())){
                    throw new SysUserException(SysUserException.USER_LOGINNAME_ALREADY_EXIST);
                }
                if(!sysUserService.updateSysUser(s)){
                    throw new SysUserException(SysUserException.USER_INSERT_ERROR);
                }

            }

        }

    }

    //用户修改自身密码
    @ApiOperation(value = "用户修改自身密码",notes = "用户修改自身密码")
    @RequestMapping(value = "/updateSysUserpwd",method = RequestMethod.POST)
    public void updateSysUserpwd(HttpServletRequest request,@RequestBody SysUserPwd sysUserPwd){
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser=loginUserInfo.getSysUser();
        if(!sysUser.getPassword().equals(MD5Utils.getMD5Value(sysUserPwd.getOldpwd()))){
            throw new SysUserException(SysUserException.USER_OLD_PASSWORD_ERROR);
        }
        sysUser.setPassword(MD5Utils.getMD5Value(sysUserPwd.getNewpwd()));
        if(!sysUserService.updateSysUser(sysUser)){
            throw new SysUserException(SysUserException.UPDATE_USER_ERROR);
        }
    }




    //从cell中读取String
    private String getString(HSSFCell cell) {
        if (cell == null) {
            return null;
        } else {
            return cell.getStringCellValue();
        }
    }

    //从cell中读取int
    private Double getDouble(HSSFCell cell) {
        if (cell == null) {
            return null;
        } else {
            return cell.getNumericCellValue();
        }
    }



}
