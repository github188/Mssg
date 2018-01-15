package com.fable.mssg.login.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.bean.info.SessionInfo;
import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.usermanager.SysMenu;
import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.login.convert.LoginUserInfoConvert;
import com.fable.mssg.service.login.LoginService;
import com.fable.mssg.service.login.SessionService;
import com.fable.mssg.service.login.exception.InvalidUserInfoException;
import com.fable.mssg.service.login.exception.UserForbiddenException;
import com.fable.mssg.service.onlinelog.OnLineLogService;
import com.fable.mssg.service.user.exception.SysUserException;
import com.fable.mssg.user.repository.SysMenuRepository;
import com.fable.mssg.user.repository.SysUserRepository;
import com.fable.mssg.utils.login.LoginUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuhl Created on 17:09 2017/11/1 0001
 */
@Service
@Exporter(interfaces =LoginService.class )
@Slf4j
public class LoginServiceImpl implements LoginService, UserDetailsService {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SysMenuRepository sysMenuRepository;

    @Autowired
    private LoginUserInfoConvert loginUserInfoConvert;

    @Autowired
    OnLineLogService onLineLogService;

    @Value("${master.forceUserOfflineTime}")
    String loginOutTime;

    public static Map<String, HttpSession> sessionMap = new HashMap<String, HttpSession>();

    /**
     * 用户登录
     * @param loginName
     * @param password
     */

    @Override
    public SessionInfo login(HttpServletRequest request,String loginName, String password, SysUser sysUser) {
        try {
            log.debug("=====loginName：" + loginName + "=====password" + password);
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
            if(sysUser.getRoleId().getRoleType()==2){
                throw new SysUserException(SysUserException.SLAVE_CAN_NOT_LOGIN);
            }
           /* if(sysUser.getLoginState()==1){
                throw new SysUserException(SysUserException.USER_ALREADY_LOGIN);
            }*/
            if(sysUser.getDeleteFlag()==1){
                throw new SysUserException(SysUserException.USER_ALREADY_DELECTED);
            }
            //用这个sysUserid去查询对应session
            HttpSession session = sessionMap.remove(sysUser.getId());
            if(session!=null){
                try {
                    session.invalidate();//session失效
                }catch (IllegalStateException e){

                }

            }
           //查出最后的下线时间
            OnLineLog isTonLineLog=onLineLogService.findOneOnline(sysUser.getId(),1);//被强制下线
            Timestamp currenttime=new Timestamp(System.currentTimeMillis());
            if(isTonLineLog!=null){
                if((currenttime.getTime()-isTonLineLog.getLogoutTime().getTime())<Long.valueOf(loginOutTime)) {
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
            sessionMap.put(sysUser.getId(), request.getSession()); //停用 强制下线时使用  剔除用户使用
            sysUserRepository.updateUserLogined(sysUser.getId());//保存状态设置为已经登录
            //添加一条登录日志的记录
            OnLineLog onlineLog = onLineLogService.findOneOnline(sysUser, null);
            if(onlineLog==null){
                OnLineLog onLineLog=new OnLineLog();
                onLineLog.setOnlineRate(0L);//访问流量m
                onLineLog.setLoginTime(new Timestamp(System.currentTimeMillis()));//访问时间
                onLineLog.setIstOffline(0);//没有被强制下线
                onLineLog.setVistEquipCount(0);//设备访问数
                onLineLog.setLoginIp(getIpAddr(request));//登录ip地址
                onLineLog.setUser(loginUserInfo.getSysUser());//放入创建人
                OnLineLog currentonline=onLineLogService.addOnlineLog(onLineLog);//添加记录
                if(onLineLog!=null){
                    loginUserInfo.setOnLineLog(currentonline); //放入到session中
                }
            }
            return new SessionInfo(LoginUtils.CURRENT_USER_KEY, loginUserInfo);
        } catch (BadCredentialsException e) {
            log.error("Bad credentials {}", e);
            throw new InvalidUserInfoException();
        }
    }

    /**
     * slave远程登录
     * @param ip
     * @param loginName
     * @param password
     * @param sysUser
     * @return
     */

    /*@Override
    public SessionInfo login(String ip,String loginName, String password, SysUser sysUser) {
        try {
            log.debug("=====loginName：" + loginName + "=====password" + password);
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(loginName, password);
            if(sysUser.getDeleteFlag()==1){
                throw new SysUserException(SysUserException.USER_ALREADY_DELECTED);
            }
            if(sysUser.getState()==1){
                throw new SysUserException(SysUserException.USER_ALREADY_DISABLED);
            }
            if(sysUser.getRoleId().getRoleType()==1){
                throw new SysUserException(SysUserException.MASTER_CAN_NOT_LOGIN);
            }
            if(sysUser.getLoginState()==1){
                throw new SysUserException(SysUserException.USER_ALREADY_LOGIN);
            }
            sysUserRepository.updateUserLogined(sysUser.getId());//保存状态设置为已经登录
            *//**
             * 处理用户登录信息
             *//*
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginUserInfo loginUserInfo = loginUserInfoConvert.convert(sysUser);
            // 查询用户角色信息
            SysRole role = sysUser.getRoleId();
            // 查询用户菜单信息
            List<SysMenu> menuList = this.queryMenuByUserId(sysUser.getRoleId().getId(),sysUser.getRoleId().getRoleType());//共享端
            if (null != role) { // 角色信息
                loginUserInfo.setRole(role);
            }
            if (null != menuList && menuList.size() > 0) { // 菜单信息
                loginUserInfo.setMenuList(menuList);
            }
            if (sysUser.getState() == 1) { // 账号被停用，禁止登录
                throw new UserForbiddenException();
            }
            //添加一条登录日志的记录
            OnLineLog onLineLog=new OnLineLog();
            onLineLog.setOnlineRate(0L);//访问流量
            onLineLog.setLoginTime(new Timestamp(System.currentTimeMillis()));//访问时间
            onLineLog.setIstOffline(0);//没有被强制下线
            onLineLog.setVistEquipCount(0);//设备访问数
            onLineLog.setLoginIp(ip);//登录ip地址
            onLineLog.setUser(loginUserInfo.getSysUser());//放入创建人
            OnLineLog currentonline=onLineLogService.addOnlineLog(onLineLog);//添加记录
            if(onLineLog!=null){
                loginUserInfo.setOnLineLog(currentonline); //放入到session中
            }
            return new SessionInfo(LoginUtils.CURRENT_USER_KEY, loginUserInfo);
        } catch (BadCredentialsException e) {
            log.error("Bad credentials {}", e);
            throw new InvalidUserInfoException();
        }
    }*/


   /* *//**
     * 用户强制下线
     *
     * @param sysUser
     *//*
    @Override
    public void forceLogout(Map<String, HttpSession> sessionMap,SysUser sysUser) {
        HttpSession session = sessionMap.get(sysUser.getId());
        sessionMap.remove(sysUser.getId());
        session.invalidate();//提示session 已经被注销
    }*/

    /**
     * 登录验证时，根据用户名查询用户权限信息
     * @param loginName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        SysUser user = sysUserRepository.findByUserName(loginName);
        if (user == null) { // 用户不存在
            throw new UsernameNotFoundException("User does not exit");
        }
        // 根据用户id查询菜单列表
        List<SysMenu> menuList = this.queryMenuByUserId(user.getId(),user.getRoleId().getRoleType());
        List<SimpleGrantedAuthority> authorities = Lists.newArrayList(new SimpleGrantedAuthority(LoginUtils.ROLE_USER));
        for (SysMenu menu: menuList) { // 菜单权限信息
            if (null != menu && null != menu.getLabel()) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(menu.getLabel());
                authorities.add(authority);
            }
        }
        return new User(user.getLoginName(), user.getPassword(), authorities);
    }

    /**
     * 根据userId查询菜单列表
     *
     * @param
     * @return
     */
    @Override
    public List<SysMenu> queryMenuByUserId(String roleId,int menuType) {
        return sysMenuRepository.querySysMenuByUserId(roleId,menuType);//查询管理端
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
