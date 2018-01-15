//package com.fable.mssg.slave.web.subscribe;
//
//import com.alibaba.fastjson.JSONObject;
//import com.fable.framework.core.support.remoting.ServiceRegistry;
//import com.fable.mssg.bean.SubscribeCondition;
//import com.fable.mssg.bean.info.LoginUserInfo;
//import com.fable.mssg.domain.apprlistmanager.ApprList;
//import com.fable.mssg.domain.subscribemanager.ResSubscribe;
//import com.fable.mssg.domain.subscribemanager.SubscribePrv;
//import com.fable.mssg.domain.usermanager.SysUser;
//import com.fable.mssg.converter.subscribe.ResSubscribeConverter;
//import com.fable.mssg.converter.subscribe.SubscribeInfoConverter;
//import com.fable.mssg.converter.subscribe.SubscribePrvConverter;
//import com.fable.mssg.exception.ResSubscribeException;
//import com.fable.mssg.service.resource.ResSubscribeService;
//import com.fable.mssg.service.resource.ResourceService;
//import com.fable.mssg.service.user.ApprListService;
//import com.fable.mssg.slave.web.exception.VisitMasterException;
//import com.fable.mssg.utils.login.LoginUtils;
//import com.fable.mssg.vo.DataTable;
//import com.fable.mssg.vo.subscribe.VResSubscribe;
//import com.fable.mssg.vo.subscribe.VSubscribeInfo;
//import com.fable.mssg.vo.subscribe.VSubscribePrv;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.PostConstruct;
//import javax.servlet.http.HttpSession;
//import javax.transaction.Transactional;
//import java.io.File;
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.util.HashSet;
//import java.util.List;
//
///**
// * @Description
// * @Author wangmeng 2017/10/12
// */
//@RestController
//@RequestMapping("/resSubscribe")
//@Slf4j
//public class ResSubscribeController {
//
//
//
//    @Autowired
//    private ResSubscribeConverter subscribeConverter;
//
//    @Autowired
//    private SubscribeInfoConverter subscribeInfoConverter;
//
//    @Autowired
//    private SubscribePrvConverter subscribePrvConverter;
//
//    private ResourceService resourceService;
//
//    private ApprListService apprListService;
//
//    private ResSubscribeService subscribeService;
//
//    @Autowired
//    private ServiceRegistry serviceRegistry;
//
//    @Value("${subscribe.file.path}")
//    private String subscribeFilePath;
//
//    @Value("${remote.proxy.server.ip}")
//    private String remoteIp;
//
//    @Value("${remote.proxy.server.port}")
//    private int remotePort;
//
//    @PostConstruct
//    public void init(){
//        try {
//            resourceService = serviceRegistry.lookup(remoteIp,remotePort,ResourceService.class);
//            apprListService = serviceRegistry.lookup(remoteIp,remotePort,ApprListService.class);
//            subscribeService = serviceRegistry.lookup(remoteIp,remotePort,ResSubscribeService.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new VisitMasterException("801");
//        }
//
//    }
//
//
//    /**
//     * 按条件查询订阅情况
//     *
//     * @param condition
//     * @return
//     */
//    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
//    public DataTable<VResSubscribe> findByCondition(SubscribeCondition condition, HttpSession session) {
//        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
//        SysUser sysUser = loginUserInfo.getSysUser();
//        condition.setApprovalId(sysUser.getId());
//        return DataTable.buildDataTable(subscribeConverter.convert(subscribeService.findByCondition(condition)));
//
//    }
//
//    @RequestMapping(value = "/listSubscribe", method = RequestMethod.GET)
//    public DataTable<VResSubscribe> findMySubscribe(HttpSession session) {
//        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
//        SysUser sysUser = loginUserInfo.getSysUser();
//
//        return DataTable.buildDataTable(subscribeConverter.convert(subscribeService.findByApplyUser(sysUser)));
//
//    }
//
//    @RequestMapping(value = "/queryInfoById", method = RequestMethod.GET)
//    public VSubscribeInfo findSubscribeInfo(String id) {
//        ResSubscribe resSubscribe = subscribeService.findById(id);
//        return subscribeInfoConverter.convert(resSubscribe);
//    }
//
//    @RequestMapping(value = "uploadFile", method = RequestMethod.POST)
//    public String uploadSubscribeFile(@RequestParam MultipartFile file) {
//        File dir = new File(subscribeFilePath);
//        if (dir.exists() && dir.isFile() || !dir.exists()) {
//            dir.mkdirs();
//        }
//
//        String originalFilename = file.getOriginalFilename();
//        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String fileName = "" + System.currentTimeMillis() + suffix;
//        File tempFile = new File(subscribeFilePath + fileName);
//        try {
//            log.debug(tempFile.getAbsolutePath());
//            file.transferTo(tempFile);
//        } catch (IOException e) {
//            log.error("上传文件出错", e);
//            throw new ResSubscribeException(ResSubscribeException.SUBSCRIBE_FILE_UPLOAD_EXP, "上传文件出错");
//        }
//        return fileName;
//    }
//
//    /**
//     * @param prvsJson
//     * @param applyCause
//     * @param linkMan
//     * @param telPhone
//     * @param resId
//     * @param session
//     * @return
//     */
//    @Transactional
//    @RequestMapping(value = "/add", method = RequestMethod.GET)
//    public void save(String prvsJson, String applyCause, String linkMan, String telPhone,
//                     String resId, String filename, HttpSession session) {
//        List<SubscribePrv> subscribePrvs = JSONObject.parseArray(prvsJson, SubscribePrv.class);
//        ResSubscribe resSubscribe = new ResSubscribe();
//        resSubscribe.setApplyCause(applyCause);
//        resSubscribe.setLinkMan(linkMan);
//        resSubscribe.setTelPhone(telPhone);
//        resSubscribe.setSubscribePrvList(new HashSet<>(subscribePrvs));
//        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
//        SysUser sysUser = loginUserInfo.getSysUser();
//        resSubscribe.setApplyUser(sysUser);
//        resSubscribe.setCreateUser(sysUser);
//        resSubscribe.setComId(sysUser.getComId());
//        resSubscribe.setCreateTime(new Timestamp(System.currentTimeMillis()));
//        resSubscribe.setApplyTime(new Timestamp(System.currentTimeMillis()));
//        resSubscribe.setApplyDocPath(filename);
//        if (resourceService.findOneRes(resId).getResLevel() == 0) { //完全共享的情况不需要审核
//            resSubscribe.setState(2);//直接通过审核
//        } else {
//            resSubscribe.setState(1);
//            ApprList apprList = new ApprList();
//            apprList.setApprType(3L);
//            apprList.setCreateTime(new Timestamp(System.currentTimeMillis()));
//            apprList.setCreateUser(sysUser.getId());
//            apprListService.save(apprList);
//        }
//        subscribeService.save(resSubscribe, resId);
//    }
//
//    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
//    public void cancel(String id) {
//        subscribeService.cancel(id);
//    }
//
//    /**
//     * 查询订阅的 设备-权限 列表 根据用户所在单位来查询
//     *
//     * @return
//     */
//    @RequestMapping(value = "/findSubscribe", method = RequestMethod.GET)
//    public List<VSubscribePrv> findSubscribeResource(HttpSession session) {
//        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
//
//        SysUser sysUser = loginUserInfo.getSysUser();//常量待定义
//
//        return subscribePrvConverter.convert(subscribeService.findByCompany(sysUser.getComId()));
//
//    }
//
//    @ApiOperation(value = "资源共享(推送)")
//    @RequestMapping(value = "/resourceShare", method = RequestMethod.GET)
//    public void resourceShare(String subscribeId, String mediaId) {
//
//        subscribeService.shared(mediaId,subscribeId);
//    }
//
//
//}
