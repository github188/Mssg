package com.fable.mssg.login.web;

import com.fable.framework.web.annotation.OperationLogAnnotation;
import com.fable.mssg.bean.info.LoginInfo;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.bean.info.SessionInfo;
import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.login.service.impl.LoginServiceImpl;
import com.fable.mssg.service.login.LoginService;
import com.fable.mssg.service.login.ValidateCodeService;
import com.fable.mssg.service.login.exception.*;
import com.fable.mssg.service.onlinelog.OnLineLogService;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.service.user.exception.SysUserException;
import com.fable.mssg.utils.MD5Utils;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.utils.login.ValidationCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: yuhl Created on 14:01 2017/11/2 0002
 */
@Slf4j
@RestController
@Api(value = "用户登录", description = "用户登录")
public class LoginController {

    private Pattern pattern;

    @Autowired
    public LoginService loginService;

    @Autowired
    public SysUserService sysUserService;

//    @Setter
//    public SessionService sessionService;

    @Autowired
    OnLineLogService onLineLogService;

    @Autowired
    private ValidateCodeService validateCodeService;

    /**
     * 登录名和密码登录
     * @param loginInfo
     * @param request
     * @return
     */
    @OperationLogAnnotation
    @ApiOperation(value = "密码登录", notes = "密码登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public SessionInfo commonLogin(@RequestBody LoginInfo loginInfo, HttpServletRequest request) {
        String loginName = loginInfo.getLoginName(); // 登录名
        String password = MD5Utils.getMD5Value(loginInfo.getPassword()); // 密码
        SysUser sysUser = sysUserService.findByLoginName(loginName); // 根据登录名查询用户信息
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
            return loginService.login(request,loginName, password, sysUser);
        } else {
            throw new InvalidAuthCodeException();
        }
    }

    /**
     * 退出登录
     * @param request
     * @param response
     * @return
     */
    @OperationLogAnnotation
    @RequestMapping(value = "/commonLogout", method = RequestMethod.POST)
    @ApiOperation(value = "退出登录", notes = "退出登录")
    @Secured(value = LoginUtils.ROLE_USER)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null != authentication) { // 退出登录
            LoginUserInfo loginUserInfo = (LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
            sysUserService.updateLoginState(loginUserInfo.getSysUser().getId());//保存状态
            OnLineLog onLineLog=loginUserInfo.getOnLineLog();
            onLineLogService.updateLoginout(new Timestamp(System.currentTimeMillis()),onLineLog.getId());//更新下线时间
            LoginServiceImpl.sessionMap.remove(loginUserInfo.getSysUser().getId());//清楚掉sessinMap
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        // 删除session缓存信息
        request.getSession().removeAttribute(LoginUtils.CURRENT_USER_KEY);

        return "login";
    }

    /**
     * 数字证书登录
     *
     * @param request
     */
    @RequestMapping(value = "/certificateLogin", method = RequestMethod.POST)
    @ApiOperation(value = "数字证书登录", notes = "数字证书登录")
    public void certificateLogin(HttpServletRequest request) {

        // 获取证书链
        X509Certificate[] certificates = (X509Certificate[]) request
                .getAttribute("javax.servlet.request.X509Certificate");

        if (certificates == null || certificates.length == 0) { // 获取证书失败
            throw new GetCertificateFailureException();
        }
        // 匹配用户证书
        X509Certificate clientCertificate = certificates[0];
        String subjectDN = clientCertificate.getSubjectDN().getName();
        Matcher matcher = pattern.matcher(subjectDN);
        String idCard = null;
        if (matcher.find() && matcher.groupCount() == 1) {
            idCard = matcher.group(1);
        } else { //证书格式错误
            throw new CertificateFormatErrorException();
        }

        if (null != idCard) { // 身份证信息不为空
            String[] cnInfoArr = idCard.trim().split(" ");
            idCard = cnInfoArr.length == 2 ? cnInfoArr[1] : "";
            SysUser sysUser = sysUserService.findByIdCard(idCard);
            if (sysUser == null) {
                throw new CertificateContentException();
            }
            log.info("Successfully match with a user, username："
                    + sysUser.getLoginName());
        }
    }

    //生成验证码
    @RequestMapping(value = "/createValCade", method = RequestMethod.GET)
    @ApiOperation(value = "生成验证码", notes = "生成验证码")
    public void createValCade(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpeg");
            //生成随机字串
            String verifyCode = ValidationCode.generateVerifyCode(4);
            //存入会话session
            HttpSession session = request.getSession(true);
            //删除以前的
            session.removeAttribute(LoginUtils.VALIDATE_CODE);
            session.setAttribute(LoginUtils.VALIDATE_CODE, verifyCode.toLowerCase());
            //生成图片
            int w = 100, h = 30;
            ValidationCode.outputImage(w, h, response.getOutputStream(), verifyCode);
            //System.out.print(session.getAttribute("verCode").toString());
        } catch (IOException e) {
            log.error("createValCade error");
            e.printStackTrace();
        }
    }


    /**
     * 设置检测证书主体的正则表达式.
     *
     * @param subjectDnRegex 正则表达式
     */
    private void setSubjectDnRegex(String subjectDnRegex) {
        Assert.hasText(subjectDnRegex,
                "Regular expression may not be null or empty");
        pattern = Pattern.compile(subjectDnRegex,
                Pattern.CASE_INSENSITIVE);
    }

 /*   @RequestMapping(value = "/test", method = RequestMethod.GET)
    public void test(int loginState,String id) {
        sysUserService.updateLoginState(loginState,id);
    }*/


}
