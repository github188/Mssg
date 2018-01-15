package com.fable.mssg.slave.web;

import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.company.converter.CompanyConverter;
import com.fable.mssg.company.service.exception.CompanyException;
import com.fable.mssg.company.vo.VCompany;
import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.apprlistmanager.ApprList;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.usermanager.SysMenu;
import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.bean.info.LoginInfo;
import com.fable.mssg.bean.info.SessionInfo;
import com.fable.mssg.login.convert.LoginUserInfoConvert;
import com.fable.mssg.login.service.impl.LoginServiceImpl;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.service.login.LoginService;
import com.fable.mssg.service.login.ValidateCodeService;
import com.fable.mssg.service.login.exception.InvalidAuthCodeException;
import com.fable.mssg.service.login.exception.InvalidUserInfoException;
import com.fable.mssg.service.login.exception.UserForbiddenException;
import com.fable.mssg.service.login.exception.UserInfoNullException;
import com.fable.mssg.service.onlinelog.OnLineLogService;
import com.fable.mssg.service.user.ApprListService;
import com.fable.mssg.service.user.RoleService;
import com.fable.mssg.service.user.SysMenuService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.service.user.exception.SysUserException;
import com.fable.mssg.slave.vo.RegisterBean;
import com.fable.mssg.slave.web.exception.VisitMasterException;
import com.fable.mssg.utils.MD5Utils;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.utils.login.ValidationCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.management.relation.Role;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@Api(value = "slave用户登录", description = "slave用户登录")
@RequestMapping("/slaveLogin")
@Slf4j
@Log4j
public class SlaveLoginController {

    @Autowired
    ServiceRegistry registry;

    @Value("${remote.proxy.server.ip}")
    private String remoteIp;

    @Value("${remote.proxy.server.port}")
    private int remotePort;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    ValidateCodeService validateCodeService;
    @Autowired
    LoginService loginService;
    @Autowired
    OnLineLogService onLineLogService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    LoginUserInfoConvert loginUserInfoConvert;
    @Autowired
    SysMenuService sysMenuService;
    @Autowired
    RoleService roleService;
    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyConverter companyConverter;
    @Autowired
    ApprListService apprListService;
    @Value("${master.forceUserOfflineTime}")
    String loginOutTime;

    @PostConstruct
    public void init (){
        try {
            sysUserService=registry.lookup(remoteIp, remotePort, true,SysUserService.class);
            validateCodeService=registry.lookup(remoteIp, remotePort, true,ValidateCodeService.class);
            loginService=registry.lookup(remoteIp, remotePort, true,LoginService.class);
            onLineLogService=registry.lookup(remoteIp, remotePort, true,OnLineLogService.class);
            sysMenuService=registry.lookup(remoteIp, remotePort, true,SysMenuService.class);
            roleService=registry.lookup(remoteIp, remotePort, true,RoleService.class);
            companyService=registry.lookup(remoteIp, remotePort, true,CompanyService.class);
            apprListService=registry.lookup(remoteIp, remotePort, true,ApprListService.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("链接master服务器异常");
        }
    }

    public static Map<String, HttpSession> sessionMap = new HashMap<String, HttpSession>();



    @RequestMapping(value = "/slaveCommonLogin",method = RequestMethod.POST)
    @ApiOperation(value = "slave用户登录", notes = "slave用户登录")
    public SessionInfo slaveCommonLogin(@RequestBody LoginInfo loginInfo, HttpServletRequest request){

        String loginName = loginInfo.getLoginName(); // 登录名
        String password = MD5Utils.getMD5Value(loginInfo.getPassword()); // 密码
        SysUser sysUser =  sysUserService.findByLoginName(loginName); // 根据登录名查询用户信息
        if(null==sysUser){
            throw  new SysUserException(SysUserException.USER_LOGINNAME_NOT_EXIST);
        }else{
            if(!MD5Utils.getMD5Value(loginInfo.getPassword()).equals(sysUser.getPassword())){
                throw new SysUserException(SysUserException.USER_PASSWORD_ERROR);
            }
        }
        String authCode = loginInfo.getAuthCode(); // 验证码
        if (loginName == null || password == null || "".equals(loginName)
                || "".equals(password)) { // 用户名或密码为空
            throw new UserInfoNullException();
        }
        String sessionCode = (String) request.getSession().getAttribute(LoginUtils.VALIDATE_CODE);
        // 校验验证码
        if (validateCodeService.validateCode(authCode, sessionCode)) {
            SessionInfo sessionInfo=login(request,loginName, password, sysUser);
            request.getSession().setAttribute(LoginUtils.CURRENT_USER_KEY,sessionInfo.getLoginUserInfo());
            sessionMap.put(sysUser.getId(),request.getSession());//放入到sessionMap中
            return  sessionInfo;
        }else{
            throw new InvalidAuthCodeException();
        }
    }


    //登录的方法
    public SessionInfo login(HttpServletRequest request,String loginName, String password, SysUser sysUser) {
        try {
            log.debug("=====slaveloginName：" + loginName + "=====slavepassword" + password);
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginName, password);
            if(sysUser.getState()==1){
                throw new SysUserException(SysUserException.USER_ALREADY_DISABLED);
            }
            if(sysUser.getState()==2){
                throw new SysUserException(SysUserException.USER_IS_APPROVING);
            }
            if(sysUser.getState()==3){
                throw new SysUserException(SysUserException.USER_APPROVE_IS_REFUSED);
            }
            if(sysUser.getRoleId().getRoleType()==1){
                throw new SysUserException(SysUserException.MASTER_CAN_NOT_LOGIN);
            }
            if(sysUser.getDeleteFlag()==1){
                throw new SysUserException(SysUserException.USER_ALREADY_DELECTED);
            }
            //用这个sysUserid去查询对应session
            HttpSession session = sessionMap.remove(sysUser.getId());
            if(session!=null){
                try {
                    session.invalidate();//若session已经失效则会报错
                }catch (IllegalStateException e){

                }
                
            }

            //查出最后的下线时间
            OnLineLog isTonLineLog=onLineLogService.findOneOnline(sysUser.getId(),1);//被强制下线
            Timestamp currenttime=new Timestamp(System.currentTimeMillis());
            if(isTonLineLog!=null){
                if((currenttime.getTime()-isTonLineLog.getLogoutTime().getTime())<Long.valueOf(loginOutTime)){
                    throw new SysUserException(SysUserException.USER_ISTOFFINE);
                }
            }

            /**
             * 处理用户登录信息
             */
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginUserInfo loginUserInfo = loginUserInfoConvert.convert(sysUser);
            // 查询用户角色信息
            SysRole role = sysUser.getRoleId();
            // 查询用户菜单信息
            List<SysMenu> menuList = this.queryMenuByUserId(sysUser.getRoleId().getId(),sysUser.getRoleId().getRoleType());
            if (null != role) { // 角色信息
                loginUserInfo.setRole(role);
            }
            if (null != menuList && menuList.size() > 0) { // 菜单信息
                loginUserInfo.setMenuList(menuList);
            }
            if (sysUser.getState() == 1) { // 账号被停用，禁止登录
                throw new UserForbiddenException();
            }
            //request.getSession().invalidate(); // 使session失效
            request.getSession().setAttribute(LoginUtils.CURRENT_USER_KEY, loginUserInfo);
            //sessionMap.put(sysUser.getId(), request.getSession()); // 强制下线时使用
            sysUserService.updateLogin(sysUser.getId());//保存状态设置为已经登录
            OnLineLog onlineLog = onLineLogService.findOneOnline(sysUser, null);
            if(onlineLog==null){
                //添加一条登录日志的记录
                OnLineLog onLineLog=new OnLineLog();
                onLineLog.setLoginTime(new Timestamp(System.currentTimeMillis()));//访问时间
                onLineLog.setOnlineRate(0L);//访问流量
                onLineLog.setIstOffline(0);//没有被强制下线
                onLineLog.setVistEquipCount(0);//设备访问数
                onLineLog.setLoginIp(getIpAddr(request));//登录ip地址
                onLineLog.setUser(loginUserInfo.getSysUser());//放入创建人
                OnLineLog currentonline=onLineLogService.addOnlineLog(onLineLog);//添加记录
                if(onLineLog!=null){
                    loginUserInfo.setOnLineLog(currentonline); //放入到loginUserInfo
                }
            }
            return new SessionInfo(LoginUtils.CURRENT_USER_KEY, loginUserInfo);
        } catch (BadCredentialsException e) {
            log.error("Bad credentials {}", e);
            throw new InvalidUserInfoException();
        }
    }

    public List<SysMenu> queryMenuByUserId(String roleId,int menuType) {
        return sysMenuService.querySysMenuByUserId(roleId,menuType);//查询共享单位
    }

    //生成验证码
    @RequestMapping(value = "/createslaveValCade", method = RequestMethod.GET)
    @ApiOperation(value = "生成验证码", notes = "生成验证码")
    public  void createslaveValCade(HttpServletRequest request, HttpServletResponse response){
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "No-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");
            //生成随机字串
            String verifyCode = ValidationCode.generateVerifyCode(4);
            //存入会话session
            HttpSession session = request.getSession(true);
            //删除以前的
            session.removeAttribute("verCode");
            session.setAttribute("verCode", verifyCode.toLowerCase());
            //生成图片
            int w = 100, h = 30;
            ValidationCode.outputImage(w, h, response.getOutputStream(), verifyCode);
            //System.out.print(session.getAttribute("verCode").toString());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("生成验证码异常");
        }



    }


    /**
     * 退出登录
     * @param request
     * @param response
     * @return
     */
    @Secured(LoginUtils.ROLE_USER)
    @RequestMapping(value = "/commonLogout", method = RequestMethod.POST)
    @ApiOperation(value = "退出登录", notes = "退出登录")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) { // 退出登录
            LoginUserInfo loginUserInfo = (LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
            OnLineLog onLineLog=loginUserInfo.getOnLineLog();
            onLineLogService.updateLoginout(new Timestamp(System.currentTimeMillis()),onLineLog.getId());//更新下线时间
            sysUserService.updateLoginState(loginUserInfo.getSysUser().getId());//保存状态
            sessionMap.remove(loginUserInfo.getSysUser().getId());//去除sessionId
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        request.getSession().removeAttribute(LoginUtils.CURRENT_USER_KEY);
        return "login";
    }

    @RequestMapping(value = "findAllRoleRegister",method = RequestMethod.GET)
    @ApiOperation(value = "查询所有的共享端角色信息", notes = "查询所有的共享端角色信息")
    public List<SysRole> findAllRoleRegister(){
        List<SysRole> list= roleService.findAllRole("单位管理员");
        return  list;
    }
    //全部单位共享端的启用的公司
    @RequestMapping(value = "findAllCompanys",method = RequestMethod.GET)
    @ApiOperation(value = "查询所有的公司", notes = "查询所有的公司")
    public List<VCompany> findAllCompanys(){
        //查询共享端单位
        List<Company> list = companyService.findAllByComTypeAndStatus(2,0);//共享端的 已经启用的
        return  companyConverter.convert(list);
    }


    //用户注册功能、
    @ApiOperation(value = "用户注册功能",notes = "用户注册功能")
    @RequestMapping(value = "registerSysUser",method = RequestMethod.POST)
    public void registerSysUser(@RequestBody RegisterBean registerBean){
        SysUser sysUser=new SysUser();
        sysUser.setLoginName(registerBean.getLoginName());
        sysUser.setUserName(registerBean.getUserName());
        sysUser.setIdCard(registerBean.getIdCard());
        sysUser.setPosition(registerBean.getPosition());
        sysUser.setTelphone(registerBean.getTelephone());
        sysUser.setCellPhoneNumber(registerBean.getCellphonenumber());
        sysUser.setRoleId(new SysRole(registerBean.getRoleId()) );
        sysUser.setDescription(registerBean.getDescription());
        //保存单位信息
        if(registerBean.getComId()==null||registerBean.getComId()==""){
            Company company=new Company();
            company.setCreateTime(new Timestamp(System.currentTimeMillis()));//创建时间
            company.setName(registerBean.getCname());
            company.setAddress(registerBean.getCaddress());
            company.setContacts(registerBean.getCcontacts());
            company.setPosition(registerBean.getCposition());
            company.setOfficePhone(registerBean.getCofficePhone());
            company.setTelphone(registerBean.getCtelephone());
            company.setComType(2);//共享端
            company.setStatus(2);//待审核状态
            //新增单位
            Company com=companyService.save(company);
            sysUser.setComId(com);//设置用户的公司id
        }else{
            Company company=new Company(registerBean.getComId());
            sysUser.setComId(company);//设置用户公司id
        }
        //保存用户信息
        sysUser.setLoginState(0);//未登录
        sysUser.setSalt("11");
        sysUser.setState(2);//待审核
        sysUser.setDeleteFlag(0);//已经删除
        sysUser.setCreateTime(new Timestamp(System.currentTimeMillis()));//创建时间
        sysUser.setCreateUser("register");//创建人
        sysUser.setPassword(MD5Utils.getMD5Value(registerBean.getPassword()));//密码
        sysUser.setSipId(registerBean.getIdCard()+"aa");//20位id
        //保存用户
        SysUser user=sysUserService.insertSysUser(sysUser);
        //保存到审批表中
        ApprList apprList=new ApprList();
        apprList.setApprStatus(1);//待审批
        apprList.setApprId(user.getId());
        apprList.setApprType(3L);//注册审批
        apprList.setCreateTime(new Timestamp(System.currentTimeMillis()));//创建时间
        apprList.setCreateUser(user.getId());//创建人
        //保存审批对象
        apprListService.save(apprList);
    }




    //获得登录ip地址
    public String getIpAddr(HttpServletRequest request){
        String ipAddress = request.getHeader("x-forwarded-for");
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress= inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",")>0){
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }





}
