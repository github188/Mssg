package com.fable.mssg.user.web;

import com.fable.framework.core.config.Address;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.company.converter.CompanyConverter;
import com.fable.mssg.company.vo.VCompany;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.login.service.impl.LoginServiceImpl;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.service.login.ForceUserOffine;
import com.fable.mssg.service.user.RoleService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.service.user.exception.SysUserException;
import com.fable.mssg.user.converter.SysUserConverter;
import com.fable.mssg.user.vo.SysUserPwd;
import com.fable.mssg.user.vo.VSysUser;
import com.fable.mssg.utils.MD5Utils;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;



/**
 * description  用户控制器
 * @author xiejk 2017/10/26
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/sysUser")
@Log4j
@Api(value = "用户管理制器",description = "用户管理制器")
public class SysUserController {

    //用户接口操作对象
    @Autowired
    private SysUserService sysUserService;
    ///用户转换类操作对象
    @Autowired
    private SysUserConverter sysUserConverter;

    @Autowired
    RoleService roleService;

    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyConverter companyConverter;

    @Autowired
    ServiceRegistry registry;

    @Value("${com.fable.mssg.proxy.sip.remoteServer}")
    Address address;


    /**
     * 分页查询用户信息
     * @param size 每页显示的数量
     * @param page 当前页数
     * @param userName 用户名称
     * @param userName 登录名称
     * @param roleId 角色名称
     * @return DataTable
     */
    @ApiOperation(value = "分页查询用户信息",notes = "分页查询用户信息")
    @RequestMapping(value = "/findPageUserByCondition",method = RequestMethod.GET)
    public DataTable findPageUserByCondition(@RequestParam String size, @RequestParam String page, String userName
            , String loginName, String roleId, String comId){
        Page<VSysUser> rlist =sysUserService.findPageUserByCondition(Integer.valueOf(size),Integer.valueOf(page)-1,userName,loginName,roleId,comId)
                .map(sysUserConverter);
        return  DataTable.buildDataTable(rlist);
    }

    /**
     *新增用户
     * @param
     * @return  message
     */
    @ApiOperation(value = "新增用户",notes = "新增用户")
    @RequestMapping(value = "/insertSysUser",method = RequestMethod.POST)
    public void insertSysUser(HttpServletRequest request, @RequestBody SysUser sysUser){
        if(!sysUserService.findUserByUserIDCard(sysUser.getIdCard()))
        {
            throw new SysUserException(SysUserException.USER_IDCARD_EXIST);
        }
        if(!sysUserService.findUserByUserName(sysUser.getLoginName())){
            throw new SysUserException(SysUserException.USER_LOGINNAME_ALREADY_EXIST);
        }
        sysUser.setPassword(MD5Utils.getMD5Value(sysUser.getPassword()));
        sysUser.setState(0);//已启用
        sysUser.setLoginState(0);//未登录
        sysUser.setDeleteFlag(0);//未删除
        sysUser.setCreateTime(new Timestamp(System.currentTimeMillis()));//创建时间
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        sysUser.setCreateUser(loginUserInfo.getSysUser().getId());//创建人当前登录的人
        sysUser.setSalt("11");//加密
        sysUser.setSipId(sysUser.getIdCard()+"aa");  //sipid
        sysUserService.insertSysUser(sysUser);
    }

    /**
     *删除用户
     * @param userId  用户id
     * @return  message
     */
    @ApiOperation(value = "删除用户",notes = "删除用户")
    @RequestMapping(value = "/delSysUser",method = RequestMethod.GET)
    public void delSysUser(HttpServletRequest request,@RequestParam String userId) {
        SysUser sysUser = sysUserService.findOneUserByUserId(userId);
        if (sysUser==null) {
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        if (sysUser.getLoginName().equals("admin")) {
            throw new SysUserException(SysUserException.ADMIN_CAN_NOT_DELETE);
        }
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        if(loginUserInfo.getSysUser().getId().equals(sysUser.getId())){//用户删除自己的情况
            throw  new SysUserException(SysUserException.USER_CAN_NOT_DELETE_SELF);
        }
        sysUser.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        sysUser.setUpdateUser(loginUserInfo.getSysUser().getId());//修改人
        if (!sysUserService.delSysUser(userId)) {
            throw new SysUserException(SysUserException.USER_DELETE_ERROR);
        }


    }

    /**
     *编辑用户
     * @param
     * @return  message
     */
    @ApiOperation(value = "编辑用户",notes = "编辑用户")
    @RequestMapping(value = "/updateSysUser",method = RequestMethod.POST)
    public void updateSysUser(HttpServletRequest request,@RequestBody SysUser sysUser ){
        if(!sysUserService.findUserByUserIDCard(sysUser.getIdCard(),sysUser.getId()))
        {
            throw new SysUserException(SysUserException.USER_IDCARD_EXIST);
        }
        if(!sysUserService.findUserByUserName(sysUser.getLoginName(),sysUser.getId()))
        {
            throw new SysUserException(SysUserException.USER_LOGINNAME_ALREADY_EXIST);
        }
        if(sysUser!=null){
            SysUser user = sysUserService.findOneUserByUserId(sysUser.getId());
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
            user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
            user.setUpdateUser(loginUserInfo.getSysUser().getId());
            if(!sysUserService.updateSysUser(user)){
                throw new SysUserException(SysUserException.UPDATE_USER_ERROR);
            }
        }else{
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }

    }

    /**
     *查找单个用户
     * @param
     * @return  message
     */
    @ApiOperation(value = "查找单个用户",notes = "查找单个用户")
    @RequestMapping(value = "/findOneUser",method = RequestMethod.GET)
    public VSysUser findOneUser(String userId){
        SysUser sysUser = sysUserService.findOneUserByUserId(userId);
        return  sysUserConverter.convert(sysUser);
    }

    /**
     * 停用用户
     * @param userId  用户id
     * @return  message
     */
    @ApiOperation(value = "停用用户",notes = "停用用户")
    @RequestMapping(value = "/forceUserDisabled",method = RequestMethod.GET)
    public void  forceUserDisabled(HttpServletRequest request,@RequestParam String userId){
        SysUser sysUser = sysUserService.findOneUserByUserId(userId);
        if (sysUser==null) {
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        if(sysUser.getRoleId().getRoleType()==1){//管理端
            if(sysUser.getLoginState()==1){//用户在线的情况
                HttpSession session= LoginServiceImpl.sessionMap.get(sysUser.getId());
                LoginServiceImpl.sessionMap.remove(sysUser.getId());
                if(session!=null){
                    session.invalidate();//清除session
                }
                sysUser.setLoginState(0);//下线状态
            }
        }
        if(sysUser.getRoleId().getRoleType()==2){//共享端
            if(sysUser.getLoginState()==1){//用户在线的情况
                try {
                    registry.lookup(address.getHost(), address.getPort(), true,ForceUserOffine.class).forceUserOffline(sysUser);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("链接salve模块异常");
                }
                sysUser.setLoginState(0);//下线状态
            }
        }
        sysUser.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        sysUser.setUpdateUser(loginUserInfo.getSysUser().getId());//修改人
        sysUser.setState(1);//停用
        if (sysUser.getLoginName().equals("admin")) {
            throw new SysUserException(SysUserException.ADMIN_CAN_NOT_DISABLED);
        }
        if(!sysUserService.updateSysUser(sysUser)){
            throw new SysUserException(SysUserException.DISABLE_USER_ERROR);
        }
    }


    /**
     *启用用户
     * @param userId 用户id
     * @return  string
     */
    @ApiOperation(value = "启用用户",notes = "启用用户")
    @RequestMapping(value = "/forceUserEnabled",method = RequestMethod.GET)
    public void forceUserEnabled(HttpServletRequest request, @RequestParam String userId){
        SysUser sysUser = sysUserService.findOneUserByUserId(userId);
        if (sysUser==null) {
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        sysUser.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        sysUser.setUpdateUser(loginUserInfo.getSysUser().getId());//修改人
        if(!sysUserService.forceUserEnabled(userId)){
            throw new SysUserException(SysUserException.USER_ENABLE_ERROR);
        }
    }

    /**
     *重置密码
     * @param
     * @return  string
     */
    @ApiOperation(value = "重置密码",notes = "重置密码")
    @RequestMapping(value = "/resetUserPwd",method = RequestMethod.POST)
    public void resetUserPwd(HttpServletRequest request, @RequestBody SysUser sysUser){
        SysUser user = sysUserService.findOneUserByUserId(sysUser.getId());
        if (user==null) {
            throw new SysUserException(SysUserException.USER_NOT_FOUND);
        }
        if (user.getRoleId().getRoleName().equals("admin")) {
            throw new SysUserException(SysUserException.DISABLE_USER_ERROR);
        }
        user.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        user.setUpdateUser(loginUserInfo.getSysUser().getId());//修改人
        user.setPassword(MD5Utils.getMD5Value(sysUser.getPassword()));
        if(!sysUserService.updateSysUser(user)){
            throw new SysUserException(SysUserException.USER_RESET_ERROR);
        }

    }

    //安全用户证书下载


    //查询全部的角色信息
    @ApiOperation(value = "查询全部的角色信息",notes = "查询全部的角色信息")
    @RequestMapping(value = "/findAllRole",method = RequestMethod.GET)
    public List<SysRole>   findAllRole(){
        return  roleService.findAllRole();
    }

    //查询管理单位
    @ApiOperation(value = "全部公司",notes = "全部公司")
    @RequestMapping(value = "/findAllCompany",method = RequestMethod.GET)
    public List<VCompany>   findAllCompany(){
        List<Company> list = companyService.findAll(0);//全部公司启用的
        return companyConverter.convert(list);
    }


    //用户修改自身密码
    @ApiOperation(value = "用户修改自身密码",notes = "用户修改自身密码")
    @RequestMapping(value = "/updateSysUserpwd",method = RequestMethod.POST)
    public void updateSysUserpwd(HttpServletRequest request, @RequestBody SysUserPwd sysUserPwd){
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser=loginUserInfo.getSysUser();
        if(!(MD5Utils.getMD5Value(sysUserPwd.getOldpwd())).equals(sysUser.getPassword())){
            throw new SysUserException(SysUserException.USER_OLD_PASSWORD_ERROR);
        }
        sysUser.setPassword(MD5Utils.getMD5Value(sysUserPwd.getNewpwd()));
        if(!sysUserService.updateSysUser(sysUser)){
            throw new SysUserException(SysUserException.UPDATE_USER_ERROR);
        }
    }

    @ApiOperation(value = "查询所有审核员",notes = "查询所有审核员")
    @RequestMapping(value = "/findApprovalMan",method = RequestMethod.GET)
    public List<VSysUser> findApprovalMan(){
        return sysUserConverter.convert(sysUserService.findAllApprovalUser());
    }


    //根据角色的类型查询对应公司的类型
    @ApiOperation(value = "根据角色的类型查询对应公司的类型",notes = "根据角色的类型查询对应公司的类型")
    @RequestMapping(value = "/findComByRoleType",method = RequestMethod.GET)
    public List<VCompany> findComByRoleType(String roleId){
        SysRole role=roleService.findOneRole(roleId);
        List<Company>companies= companyService.findAllByComTypeAndStatus(role.getRoleType(),0);//启用的
        return  companyConverter.convert(companies);
    }



}









