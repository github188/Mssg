package com.fable.mssg.resource.web.apprlist;

import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.catalog.service.CatalogService;
import com.fable.mssg.company.converter.CompanyConverter;
import com.fable.mssg.company.vo.VCompany;
import com.fable.mssg.domain.resmanager.Catalog;
import com.fable.mssg.resource.converter.ConverterConstants;
import com.fable.mssg.resource.service.exception.ApprException;
import com.fable.mssg.resource.service.exception.ResourceException;
import com.fable.mssg.service.company.CompanyService;
import com.fable.mssg.datasource.converter.DataSourceConverter;
import com.fable.mssg.service.dict.DictItemService;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.datasource.VDataSource;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.apprlistmanager.ApprList;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.domain.subscribemanager.SubscribePrv;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.converter.ResourceConverter;
import com.fable.mssg.converter.subscribe.ResSubscribeConverter;
import com.fable.mssg.converter.subscribe.SubscribePrvConverter;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.service.resource.ResourceService;

import com.fable.mssg.service.user.ApprListService;
import com.fable.mssg.user.converter.SysUserConverter;
import com.fable.mssg.user.vo.VSysUser;
import com.fable.mssg.vo.resource.ApprovalForm;
import com.fable.mssg.vo.resource.SubscribeForm;
import com.fable.mssg.vo.resource.SubscribeFormPrv;
import com.fable.mssg.vo.subscribe.VResSubscribe;
import com.fable.mssg.resource.vo.VResource;
import com.fable.mssg.vo.VRegisterApprList;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * description  审批控制器
 *
 * @author xiejk 2017/11/06
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/apprlist")
@Api(value = "审批控制器", description = "审批控制器")
@Slf4j
public class ApprListController {

    @Autowired
    ResSubscribeService resSubscribeService;
    @Autowired
    ResSubscribeConverter resSubscribeConverter;
    @Autowired
    ApprListService apprListService;
    @Autowired
    ResourceService resourceService;
    @Autowired
    ResourceConverter resourceConverter;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    CompanyService companyService;
    @Autowired
    DataSourceConverter dataSourceConverter;
    @Autowired
    SubscribePrvConverter subscribePrvConverter;
    @Autowired
    CatalogService catalogService;
    @Autowired
    SysUserConverter sysUserConverter;
    @Autowired
    CompanyConverter companyConverter;
    @Autowired
    DictItemService dictItemService;

    @Value("${subscribe.file.path}")
    String docpath;


    //查询资源目录树
    @ApiOperation(value = "查询资源目录树", notes = "查询资源目录树")
    @RequestMapping(value = "/findAllcatalog", method = RequestMethod.GET)
    public List<Catalog> findAllcatalog() {
        return catalogService.findAll();
    }

    /**
     * 分页查询订阅审批
     * @param type
     * @param size
     * @param page
     * @param resName
     * @param catalogid
     * @param searchTime
     * @return dataTable
     */
    @ApiOperation(value = "分页查询订阅审批", notes = "分页查询订阅审批")
    @RequestMapping(value = "/findAllPageByCondition", method = RequestMethod.GET)
    public DataTable findAllPageByCondition(@RequestParam String type, @RequestParam String size
            , @RequestParam String page, String resName, String catalogid
            , @RequestParam String searchTime) {
        int apprStatus;
        if (type.equals("1")) {//待审核
            apprStatus = 1;
        } else if (type.equals("2")) {//已审核
            apprStatus = 2;
        } else if (type.equals("3")) { //已拒绝
            apprStatus = 3;
        } else { //查询全部
            apprStatus = 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        Timestamp tmp = null;
        switch (Integer.valueOf(searchTime)) {
            case 0://全部
                c = null;
                break;
            case 1://一周之内
                c.setTime(new Date());
                c.add(Calendar.DATE, -7);
                break;
            case 2://一个月之类
                c.setTime(new Date());
                c.add(Calendar.MONTH, -1);
                break;
            case 3://三个月之类
                c.setTime(new Date());
                c.add(Calendar.MONTH, -3);
                break;
            case 4://一年之类
                c.setTime(new Date());
                c.add(Calendar.YEAR, -1);
                break;
        }
        if (c != null) {
            Date date = c.getTime();
            tmp.valueOf(sdf.format(date));
        } else {
            tmp = null;
        }
        Map map = apprListService.findAllPageByCondition(apprStatus, Integer.valueOf(size), Integer.valueOf(page)
                , resName, catalogid, tmp);
        DataTable dataTable = new DataTable();
        dataTable.setRecordsFiltered(Long.valueOf((int) map.get("count")));
        dataTable.setRecordsTotal(Long.valueOf((int) map.get("count")));
        dataTable.setData((List) map.get("vslist"));
        return dataTable;
    }


    /**
     * 查询资源订阅详细信息
     * @param id
     * @return
     */
    @ApiOperation(value = "查询资源订阅详细信息", notes = "查询资源订阅详细信息")
    @RequestMapping(value = "/findOneResSub", method = RequestMethod.GET)
    public VResSubscribe findOneResSub(@RequestParam String id) {
        ApprList apprlist = apprListService.findOneApprList(id);
        if (apprlist != null) {
            ResSubscribe r = resSubscribeService.findById(apprlist.getApprId());
            return resSubscribeConverter.convert(r);
        } else {
            throw new ApprException(ApprException.RES_SUB_NOT_FOUND);
        }


    }

    /**
     * 查询资源信息
     * @param id
     * @return
     */
    @ApiOperation(value = "查询资源信息", notes = "查询资源信息")
    @RequestMapping(value = "/findOneResource", method = RequestMethod.GET)
    public Map findOneResource(@RequestParam String id) {
        ApprList apprlist = apprListService.findOneApprList(id);
        if (apprlist == null) {
            throw new ApprException(ApprException.APPR_NOT_FOUND);
        }
        ResSubscribe resSub = resSubscribeService.findById(apprlist.getApprId());
        if (resSub == null) {
            throw new ApprException(ApprException.RES_SUB_NOT_FOUND);
        }
        Resource resource = resourceService.findOneRes(resSub.getResId().getId());
        if (resource == null) {
            throw new ResourceException(ResourceException.RESOURCE_NOT_FOUNT);
        }
        List<DataSource> ds = resource.getDas();
        List<VDataSource> vds = dataSourceConverter.convert(ds);
        VResource vr = resourceConverter.convert(resource);
        vr.setResType(ConverterConstants.getType(Integer.valueOf(vr.getResType())));//资源类型
        vr.setResLevel(ConverterConstants.getResourceLevel(Integer.valueOf(vr.getResLevel())));//等级
        vr.setResMain(dictItemService.findByDictItemCode(Long.valueOf(vr.getResMain())).getDictItemName());//主题名臣
        vr.setResIndustry(dictItemService.findByDictItemCode(Long.valueOf(vr.getResIndustry())).getDictItemName());//主题名臣
        Map map = new HashMap();
        map.put("vr", vr);  //返回资源
        map.put("vds", vds);//返回数据源
        return map;
    }


    /**
     * 查询资源信息权限信息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "查询资源信息权限信息", notes = "查询资源信息权限信息")
    @RequestMapping(value = "/findOneResourceAndPrv", method = RequestMethod.GET)
    public Map findOneResourceAndPrv(@RequestParam String id) {
        ApprList apprlist = apprListService.findOneApprList(id);//审核对象
        ResSubscribe resSub = resSubscribeService.findById(apprlist.getApprId());//资源发布对象
        //Resource resource = resourceService.findOneRes(resSub.getResId().getId());//资源对象
        //List<DataSource> ds = resource.getDas();
        // List<VDataSource> vds = dataSourceConverter.convert(ds);//数据源
        Set<SubscribePrv> rslist = resSub.getSubscribePrvList();//获得订阅权限
        List<SubscribePrv> list = new ArrayList<>(rslist);
        List<SubscribeFormPrv> sfb = new ArrayList<>();
        for (SubscribePrv sp : list) {
            DataSource ds = sp.getDsId();
            SubscribeFormPrv s = new SubscribeFormPrv();
            s.setDownload(sp.getDownload());
            s.setDsCode(ds.getDsCode());//数据源dscode
            s.setDsId(ds.getId());//数据源id
            s.setDsName(ds.getDsName());
            s.setDsType(ds.getDsType());
            s.setHisSnap(sp.getHistSnap());
            s.setHistTime(sp.getHisTime());
            s.setPid(ds.getStandardParentId());
            s.setRealControl(sp.getRealControl());
            s.setRealSnap(sp.getRealSnap());
            s.setRealTime(sp.getRealTime());
            s.setRecord(sp.getRecord());
            sfb.add(s);
        }
        Map map = new HashMap();
        //map.put("vds", vds);//返回数据源
        map.put("vlist", sfb);//返回权限
        return map;
    }


    //资源订阅审批
    @ApiOperation(value = "资源订阅审批", notes = "资源订阅审批")
    @RequestMapping(value = "/apprResSub", method = RequestMethod.POST)
    @Transactional
    public void apprResSub(@RequestBody ApprovalForm approvalForm, HttpSession session) {

        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);

        ApprList apprList = apprListService.findOneApprList(approvalForm.getId());
        if (apprList == null) {
            throw new ApprException(ApprException.APPR_NOT_FOUND);
        }
        ResSubscribe resSub = resSubscribeService.findById(apprList.getApprId());
        //删除原有的权限
        resSubscribeService.deletePrv(resSub.getSubscribePrvList());

        apprList.setApprMsg(approvalForm.getApprMsg());
        if (approvalForm.getApprStatus().equals("0")) {//同意
            apprList.setApprStatus(2);
            resSub.setState(2);
            List<SubscribeFormPrv> subscribeFormPrvs = approvalForm.getSubscribeFormPrvs();
            Set<SubscribePrv> subscribePrvs = new HashSet<>(subscribeFormPrvs.size());
            for (SubscribeFormPrv subscribeFormPrv : subscribeFormPrvs) {
                SubscribePrv subscribePrv = new SubscribePrv();
                subscribePrv.setDsId(new DataSource(subscribeFormPrv.getDsId()));
                //设置权限时间
                subscribePrv.setRealBeginTime(SubscribeForm.getBeginAndEnd(subscribeFormPrv.getRealTime(),0)[0]);
                subscribePrv.setRealEndTime(SubscribeForm.getBeginAndEnd(subscribeFormPrv.getRealTime(),0)[1]);
                subscribePrv.setHisBeginTime(SubscribeForm.getBeginAndEnd(subscribeFormPrv.getHistTime(),1)[0]);
                subscribePrv.setHisEndTime(SubscribeForm.getBeginAndEnd(subscribeFormPrv.getHistTime(),1)[1]);
                subscribePrv.setRealPlay("-1".equals(subscribeFormPrv.getRealTime()) ? 0 :
                        "0".equals(subscribeFormPrv.getRealTime()) ? 2 : 1);
                subscribePrv.setPlayBack("-1".equals(subscribeFormPrv.getHistTime()) ? 0 :
                        "0".equals(subscribeFormPrv.getHistTime()) ? 2 : 1);
                subscribePrv.setRealSnap(subscribeFormPrv.getRealSnap());
                subscribePrv.setRecord(subscribeFormPrv.getRecord());
                subscribePrv.setHistSnap(subscribeFormPrv.getHisSnap());
                subscribePrv.setDownload(subscribeFormPrv.getDownload());
                subscribePrv.setRealControl(subscribeFormPrv.getRealControl());
                subscribePrvs.add(subscribePrv);
            }
            //赋予新的权限
            resSub.setSubscribePrvList(subscribePrvs);
        } else {//拒绝 也要删除权限信息 方便用户再次订阅
            apprList.setApprStatus(3);
            resSub.setSubscribePrvList(new HashSet<>());
            resSub.setState(3);
        }
        try {
            //保存
            apprListService.update(apprList);
            //保存
            resSubscribeService.approval(resSub, loginUserInfo.getSysUser());

        } catch (Exception e) {
            e.printStackTrace();
            throw new ApprException(ApprException.APPR_ERROR);
        }

    }

    //下载申请文件
    @ApiOperation(value = "下载申请文件", notes = "下载申请文件")
    @RequestMapping(value = "/downloadZip", method = RequestMethod.GET)
    public void downloadZip(HttpServletResponse response, String fileName) {
        InputStream resInput = null;
        try {
            resInput = new FileInputStream(new File(
                    docpath + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (null != resInput) {
            OutputStream out = null;
            BufferedInputStream bis = null;
            try {
                response.setContentType("application/force-download");// 设置强制下载打开
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
                out = response.getOutputStream();
                bis = new BufferedInputStream(resInput, 1024 * 1024);
                byte[] data = new byte[1024 * 1024];
                int length;
                while ((length = bis.read(data)) != -1) {
                    out.write(data, 0, length);
                }

            } catch (IOException e) {
                log.error("下载审批资料出错", e);
            } finally {
                try {
                    if (null != out) {
                        out.close();
                    }
                    if (null != bis) {
                        bis.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流异常!", e);
                }
            }

        }
    }


    /**
     * 分页查询注册审批
     *
     * @param size
     * @param page
     * @return
     */
    @ApiOperation(value = "分页查询注册审批", notes = "分页查询注册审批")
    @RequestMapping(value = "/findAllRegditerPage", method = RequestMethod.GET)
    public DataTable findAllRegditerPage(@RequestParam String size, @RequestParam String page) {
        Map map = apprListService.findAllResigterPage(Integer.valueOf(size), Integer.valueOf(page));
        DataTable dataTable = new DataTable();
        dataTable.setRecordsFiltered(Long.valueOf((int) map.get("count")));
        dataTable.setRecordsTotal(Long.valueOf((int) map.get("count")));
        List<VRegisterApprList> vslist = (List) map.get("vslist");
        for (VRegisterApprList vl : vslist) {
            if (vl.getApprStatus().equals("1")) {
                vl.setApprStatus("未审批");
            }
            if (vl.getApprStatus().equals("2")) {
                vl.setApprStatus("已审批");
            }
            if (vl.getApprStatus().equals("3")) {
                vl.setApprStatus("已拒绝");
            }
        }
        dataTable.setData(vslist);
        return dataTable;
    }


    //查询单个审批注册详细信息
    @ApiOperation(value = "查询单个审批注册详细信息", notes = "查询单个审批注册详细信息")
    @RequestMapping(value = "/findOneVRegisterApprList", method = RequestMethod.GET)
    public VRegisterApprList findOneVRegisterApprList(String id) {
        VRegisterApprList vr = apprListService.findOneVRegisterApprList(id);

        if (vr.getApprStatus().equals("1")) {
            vr.setApprStatus("未审批");
        }
        if (vr.getApprStatus().equals("2")) {
            vr.setApprStatus("已审批");
        }
        if (vr.getApprStatus().equals("3")) {
            vr.setApprStatus("已拒绝");
        }
        return vr;
    }

    //审批注册
    @ApiOperation(value = "审批注册", notes = "审批注册")
    @RequestMapping(value = "/apprRegister", method = RequestMethod.GET)
    public void apprRegister(@RequestParam String id, String apprMsg, String result, Integer companylevel) {
        //更改审批表的状态  更改公司等级   更改用户的状态

        ApprList apprlist = apprListService.findOneApprList(id);//审批信息
        if (apprlist == null) {
            throw new ApprException(ApprException.REGISTER_NOT_FOUND);
        }
        SysUser sysuser = sysUserService.findOneUserByUserId(apprlist.getApprId());//得到用户
        Company company = companyService.findById(sysuser.getComId().getId());
        apprlist.setApprMsg(apprMsg);//设置意见
        if (result.equals("0")) {//同意
            apprlist.setApprStatus(2);//已经审批
            sysuser.setState(0);//用户启用
            sysuser.setDeleteFlag(0);//未删除
            company.setComLevel(companylevel);//单位等级
            company.setStatus(0);//公司启用状态
        } else {//拒绝
            apprlist.setApprStatus(3);//拒绝
            sysuser.setState(3);//用户注册被拒绝
            if(company.getStatus()==2){//待审核状态
                company.setStatus(3);//公司审核被拒绝
            }
        }
        //保存
        try {
            apprListService.update(apprlist);
            sysUserService.updateSysUser(sysuser);
            companyService.save(company);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApprException(ApprException.APPR_REGISTER_ERROR);
        }
    }


    //查询用户信息
    @ApiOperation(value = "查询用户信息", notes = "查询用户信息")
    @RequestMapping(value = "/findOneSysUer", method = RequestMethod.GET)
    public VSysUser findOneSysUer(@RequestParam String id) {
        ApprList apprlist = apprListService.findOneApprList(id);//审批信息
        SysUser sysUser = sysUserService.findOneUserByUserId(apprlist.getApprId());
        return sysUserConverter.convert(sysUser);
    }

    //查询单位信息
    @ApiOperation(value = "查询单位信息", notes = "查询单位信息")
    @RequestMapping(value = "/findOneCompany", method = RequestMethod.GET)
    public VCompany findOneCompany(@RequestParam String id) {
        ApprList apprlist = apprListService.findOneApprList(id);//审批信息
        SysUser sysUser = sysUserService.findOneUserByUserId(apprlist.getApprId());
        Company company = companyService.findById(sysUser.getComId().getId());
        return companyConverter.convert(company);
    }


}









