package com.fable.mssg.resource.web;

import com.alibaba.fastjson.JSONArray;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.dictmanager.DictItem;
import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.converter.subscribe.SubscribeInfoConverter;
import com.fable.mssg.converter.subscribe.ResSubscribeConverter;
import com.fable.mssg.converter.subscribe.SubscribePrvConverter;
import com.fable.mssg.resource.converter.ResourceInfoConverter;
import com.fable.mssg.resource.vo.subscribe.VResourceSubscribeInfo;
import com.fable.mssg.service.dict.DictItemService;
import com.fable.mssg.utils.DataSourceUtil;
import com.fable.mssg.vo.resource.SubscribeForm;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.exception.ResSubscribeException;
import com.fable.mssg.bean.SubscribeCondition;
import com.fable.mssg.vo.subscribe.VResSubscribe;
import com.fable.mssg.vo.subscribe.VSubscribeInfo;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.subscribe.VSubscribePrv;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Description
 * @Author wangmeng 2017/10/12
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/resSubscribe")
@Slf4j
public class ResSubscribeController {

    @Autowired
    private ResSubscribeService subscribeService;

    @Autowired
    private ResSubscribeConverter subscribeConverter;

    @Autowired
    private SubscribeInfoConverter subscribeInfoConverter;

    @Autowired
    private SubscribePrvConverter subscribePrvConverter;

    @Autowired
    DictItemService dictItemService;

    @Autowired
    private ResourceInfoConverter resourceInfoConverter;

    @Value("${subscribe.file.path}")
    private String subscribeFilePath;


    /**
     * 按条件查询订阅情况
     *
     * @param condition
     * @return
     */
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public DataTable<VResSubscribe> findByCondition(SubscribeCondition condition, HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        condition.setApprovalId(sysUser.getId());
        return DataTable.buildDataTable(subscribeConverter.convert(subscribeService.findByCondition(condition)));

    }

    @RequestMapping(value = "/listSubscribe", method = RequestMethod.GET)
    public DataTable<VResSubscribe> findMySubscribe(HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();

        return DataTable.buildDataTable(subscribeConverter.convert(subscribeService.findByApplyUser(sysUser)));

    }

    @RequestMapping(value = "/queryInfoById", method = RequestMethod.GET)
    public VSubscribeInfo findSubscribeInfo(String id) {
        ResSubscribe resSubscribe = subscribeService.findById(id);
        return subscribeInfoConverter.convert(resSubscribe);
    }

    @RequestMapping(value = "uploadFile", method = RequestMethod.POST)
    public String uploadSubscribeFile(@RequestParam MultipartFile file) {
        File dir = new File(subscribeFilePath);
        if (dir.exists() && dir.isFile() || !dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "" + System.currentTimeMillis() + suffix;
        File tempFile = new File(subscribeFilePath + fileName);
        try {
            log.debug(tempFile.getAbsolutePath());
            file.transferTo(tempFile);
        } catch (IOException e) {
            log.error("上传文件出错", e);
            throw new ResSubscribeException(ResSubscribeException.FILE_IO_EXCEPTION);
        }
        return fileName;
    }

    /**
     * @return
     */
    @Transactional
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public void save(HttpSession session, @RequestBody SubscribeForm subscribeForm) {
        ResSubscribe resSubscribe = new ResSubscribe();
        resSubscribe.setApplyCause(subscribeForm.getApplyCause());
        resSubscribe.setLinkMan(subscribeForm.getLinkMan());
        resSubscribe.setTelPhone(subscribeForm.getPhone());
        resSubscribe.setSubscribePrvList(new HashSet<>(subscribeForm.getSubscribePrv()));
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        resSubscribe.setApplyUser(sysUser);
        resSubscribe.setCreateUser(sysUser);
        resSubscribe.setComId(sysUser.getComId());
        resSubscribe.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resSubscribe.setApplyTime(new Timestamp(System.currentTimeMillis()));
        resSubscribe.setApplyDocPath(subscribeForm.getFileName());
        resSubscribe.setCellPhone(subscribeForm.getCellPhone());
        resSubscribe.setDuty(subscribeForm.getDuty());
        subscribeService.save(resSubscribe, subscribeForm.getResId(), sysUser.getComId());
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public void cancel(String id) {
        subscribeService.cancel(id);
    }

    /**
     * 查询订阅的 设备-权限 列表 根据用户所在单位查询
     *
     * @return
     */
    @RequestMapping(value = "/findSubscribe", method = RequestMethod.GET)
    public List<Set<VSubscribePrv>> findSubscribeResource(HttpSession session, Integer dsLevel, Integer locationType, String dsName) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);

        SysUser sysUser = loginUserInfo.getSysUser();

        List<ResSubscribe> resSubscribes = subscribeService.findByCompany(sysUser.getComId(), dsLevel, locationType, dsName);
        List<List<VSubscribePrv>> privilegeList = new ArrayList<>();
        List<Set<VSubscribePrv>> privilegeSets = new ArrayList<>();

        for (ResSubscribe resSubscribe : resSubscribes) {
            privilegeList.add(subscribePrvConverter.convert(new ArrayList<>(resSubscribe.getSubscribePrvList())));
        }

        for (List<VSubscribePrv> vSubscribePrvs : privilegeList) {
            Set<VSubscribePrv> privilegeSet = new HashSet<>();
            Map<String, VSubscribePrv> prvMap = new HashMap<>();

            for (VSubscribePrv vSubscribePrv : vSubscribePrvs) {
                prvMap.put(vSubscribePrv.getDsCode(), vSubscribePrv);
            }

            for (VSubscribePrv subscribePrv : vSubscribePrvs) {

                if (subscribePrv.getDsType() == 5) {
                    boolean flag = false;
                    if (dsLevel == null && (dsName == null || "".equals(dsName)) && locationType == null) {
                        flag = true;
                    } else {
                        if (subscribePrv.getDsLevel().equals(dsLevel)) {
                            flag = true;
                        }
                        if (!"".equals(dsName) && subscribePrv.getDsName().contains(dsName)) {
                            flag = true;
                        }
                        if (subscribePrv.getLocationType().equals(locationType)) {
                            flag = true;
                        }
                    }

                    if (flag) {
                        DataSourceUtil.getRoot(subscribePrv, prvMap, privilegeSet);
                        privilegeSet.add(subscribePrv);
                    }
                }
            }

            privilegeSets.add(privilegeSet);

        }


        return privilegeSets;
    }


    @ApiOperation(value = "资源共享(推送)")
    @RequestMapping(value = "/resourceShare", method = RequestMethod.GET)
    public void resourceShare(String subscribeId, String mediaId, HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        JSONArray jsonArray = JSONArray.parseArray(mediaId);
        String[] mediaIds = new String[jsonArray.size()];
        jsonArray.toArray(mediaIds);
        subscribeService.shared(mediaIds, subscribeId, sysUser.getId());
    }

    @ApiOperation(value = "查看订阅信息详情")
    @RequestMapping(value = "/findSubscribeInfo", method = RequestMethod.GET)
    public VResourceSubscribeInfo findSubscribeResourceInfo(String subscribeId) {
        ResSubscribe resSubscribe = subscribeService.findById(subscribeId);
        return VResourceSubscribeInfo.builder()
                .resourceInfo(resourceInfoConverter.convert(resSubscribe.getResId()))
                .subscribeInfo(subscribeInfoConverter.convert(resSubscribe))
                .build();


    }


    @ApiOperation(value = "主题分类")
    @RequestMapping(value = "/findAllMain", method = RequestMethod.GET)
    public List<DictItem> findAllMain() {
        List<DictItem> list = dictItemService.findAll(10000002L);
        return list;
    }

    @ApiOperation(value = "行业分类")
    @RequestMapping(value = "/findAllHy", method = RequestMethod.GET)
    public List<DictItem> findAllHy() {
        List<DictItem> list = dictItemService.findAll(10000003L);
        return list;
    }


}
