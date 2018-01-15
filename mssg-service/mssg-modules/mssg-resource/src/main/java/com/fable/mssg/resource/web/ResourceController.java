package com.fable.mssg.resource.web;

import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.fable.mssg.service.resource.MediaInfoService;
import com.fable.mssg.utils.DataSourceUtil;
import com.fable.mssg.vo.orginalds.VEquipmentCatalogBean;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.resmanager.Catalog;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.domain.resmanager.ResourceConfig;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.converter.ResourceConverter;
import com.fable.mssg.resource.converter.ResourceInfoConverter;
import com.fable.mssg.resource.vo.VResourceInfo;
import com.fable.mssg.service.resource.ResourceService;
import com.fable.mssg.resource.service.exception.ResourceException;
import com.fable.mssg.resource.vo.ResourceStatus;
import com.fable.mssg.resource.vo.VResource;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import com.fable.mssg.vo.resource.PublishDataSourceInfo;
import com.fable.mssg.vo.subscribe.VResAndSubscribeStatus;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * description  资源控制器
 *
 * @author xiejk 2017/9/30
 */
@RestController
@RequestMapping("/res")
@Slf4j
@Secured(LoginUtils.ROLE_USER)
public class ResourceController {
    //资源转换类操作对象
    @Autowired
    private ResourceConverter resourceConverter;

    //资源接口操作对象
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceInfoConverter resourceInfoConverter;

    @Autowired
    private MediaInfoService mediaInfoService;

    @Value("${resource.icon.path}")
    private String resImagePath;

    @RequestMapping(value = "/findToApproval", method = RequestMethod.GET)
    @ApiOperation(value = "查询待审核的资源")
    public DataTable<VResource> findToApproval(HttpSession session, String catalogId,
                                               @RequestParam(required = false, defaultValue = "1") String page,
                                               @RequestParam(required = false, defaultValue = "10") String size) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();


        Page<Resource> resources = resourceService
                .findByApprovalAndCatalog(sysUser.getId(), catalogId, Integer.parseInt(page), Integer.parseInt(size));

        return DataTable.buildDataTable(resources);


    }

    @RequestMapping(value = "/findResourceInfo", method = RequestMethod.GET)
    @ApiOperation(value = "查询单个资源信息,包含数据源信息(和单位等级有关)")
    public VResourceInfo getResourceInfo(@RequestParam String resourceId, HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        Company company = sysUser.getComId();
        return resourceInfoConverter.convert(resourceService.findResourceInfoCanView(resourceId, company));

    }


    /**
     * 条件查询所有的资源信息
     *
     * @param resStatus
     * @param resName    资源名称
     * @param catalogId  目录id
     * @param searchTime 查询时间
     * @return List<VResource>
     */
    @RequestMapping(value = "/findAllResByCondition", method = RequestMethod.GET)
    public DataTable<VResource> findAllResByCondition(String resStatus, String resName, String catalogId, String searchTime, Integer page, Integer size) {
        page = page == null ? 1 : page;
        size = size == null ? 10 : size;

        Page<VResource> vResources = resourceService.findAllResourceByCondition(resStatus, resName, catalogId, searchTime, page, size).map(resourceConverter);
        return DataTable.buildDataTable(vResources);

    }

    /**
     * 分页显示对应目录下的资源信息
     *
     * @param cid  对应目录id
     * @param size 每页显示的个数
     * @param page 当前页数
     * @return DataTable对象
     */
    @RequestMapping(value = "/findAllResByCatalogId", method = RequestMethod.GET)
    public DataTable findAllResByCatalogId(@RequestParam String cid, @RequestParam String size, @RequestParam String page) {
        Page<VResource> rlist = resourceService.findByCatalogId(cid, Integer.valueOf(size), Integer.valueOf(page))
                .map(resourceConverter);
        return DataTable.buildDataTable(rlist);
    }

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadIcon", method = RequestMethod.POST)
    public Object uploadIcon(@RequestParam MultipartFile file, String lastIconFilename) {
        File dir = new File(resImagePath);
        if (dir.exists() && dir.isFile() || !dir.exists()) {
            dir.mkdirs();
        }
        File lastIcon = new File(resImagePath + lastIconFilename);
        if (lastIcon.exists()) {
            lastIcon.delete();//删除原图片 更新处理
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!suffix.equals(".jpeg") && !suffix.equals(".bmp") && !suffix.equals(".png") && !suffix.equals(".jpg")) {
            throw new ResourceException(ResourceException.ICON_FORMAT_INCORRECT);
        }
        String tmpFileName = System.currentTimeMillis() + suffix;
        File tempFile = new File(resImagePath + tmpFileName);
        try {
            log.debug(tempFile.getAbsolutePath());
            file.transferTo(tempFile);
        } catch (IOException e) {
            log.error("图片上传出错", e);
            throw new ResourceException(ResourceException.ICON_IO_EXCEPTION);
        }
        return tmpFileName;

    }

    @RequestMapping(value = "/downloadIcon", method = RequestMethod.GET)
    public void downloadIcon(@RequestParam String fileName, HttpServletResponse response) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);//取得后缀
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File(resImagePath + fileName));
        } catch (IOException e) {
            log.error("读取图片出错", e);
            throw new ResourceException(ResourceException.ICON_IO_EXCEPTION);
        }
        response.setContentType("image/" + suffix);
        try {
            ImageIO.write(bufferedImage, suffix, response.getOutputStream());
        } catch (IOException e) {
            throw new ResourceException(ResourceException.ICON_IO_EXCEPTION);
        }

    }

    /**
     * 新增资源的方法
     *
     * @param resCase //@param request
     *                //@param file
     * @return message
     */
    @RequestMapping(value = "/insertRes", method = RequestMethod.GET)
    public void insertRes(String resCase, String resType, String linkMan, String phone, String resName,
                          String englishName, String resCode, String resLevel, String iconRoot, String resAbstract,
                          String resMain, String resIndustry, String catalogId, HttpSession session) {
        Resource res = new Resource();
        res.setResStatus(ResourceStatus.TO_SUBMIT);
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        res.setCreateUser(loginUserInfo.getSysUser());
        res.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Catalog catalog = new Catalog();
        catalog.setId(catalogId);
        res.setCatalogId(catalog);
        res.setHyCategory(Integer.parseInt(resIndustry));
        //设置图片的路径
        if (iconRoot == null || iconRoot.equals("")) {
            res.setIconRoot(VResource.DEFAULT_ICON);
        } else {
            res.setIconRoot(iconRoot);
        }
        res.setLinkMan(linkMan);
        res.setMainCategory(Integer.parseInt(resMain));
        res.setResAbstract(resAbstract);
        res.setResCase(resCase);
        res.setResCode(resCode);
        res.setResName(resName);
        res.setResEngName(englishName);
        res.setResLevel(Integer.valueOf(resLevel));
        res.setTelPhone(phone);
        //资源类型
        res.setResType(Integer.parseInt(resType));
        resourceService.save(res);
    }


    /**
     * 查询单个的resource
     *
     * @param rid 对应目录id
     * @return Resource 对象
     */
    @RequestMapping(value = "/findOneRes", method = RequestMethod.GET)
    public VResource findOneRes(@RequestParam String rid) {
        return resourceConverter.convert(resourceService.findOneRes(rid));
    }

    /**
     * 修改待提交的资源 修改图片也可以修改图片
     *
     * @param id 对应资源对象
     * @return message 返回结果
     */
    @RequestMapping(value = "/updateRes", method = RequestMethod.GET)
    public void updateRes(String id, String resCase, String resType, String iconRoot, String linkMan,
                          String phone, String resName, String englishName, String resCode, String resLevel,
                          String resAbstract, String resMain, String resIndustry, HttpSession session) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        Resource res = resourceService.findOneRes(id);
        res.setUpdateUser(sysUser);
        res.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        //审核人
        // res.setApprovalMan(); 资源审核这版本不做
        //设置图片的路径
        res.setIconRoot(iconRoot);
        res.setLinkMan(linkMan);
        res.setMainCategory(Integer.parseInt(resMain));
        res.setResAbstract(resAbstract);
        res.setResCase(resCase);
        res.setResCode(resCode);
        res.setResName(resName);
        res.setResEngName(englishName);
        res.setResLevel(Integer.valueOf(resLevel));
        res.setTelPhone(phone);
        res.setHyCategory(Integer.parseInt(resIndustry));
        res.setResType(Integer.parseInt(resType));
        //资源类型
        res.setResType(9);
        resourceService.updateResBeforePending(res);

    }

    /**
     * 删除对应的资源
     *
     * @param rid 对应资源id
     * @return 返回结果
     */
    @RequestMapping(value = "/delRes", method = RequestMethod.GET)
    public void delRes(@RequestParam String rid) {
        resourceService.delRes(rid);
    }

    /**
     * 提交资源
     *
     * @param rid 资源对应id
     * @return 返回状态
     */
    @RequestMapping(value = "/pendRes", method = RequestMethod.GET)
    public void pendRes(@RequestParam String rid) {
        resourceService.pendRes(rid);
    }

    /**
     * 撤销资源
     *
     * @param rid 对应资源id
     * @return 返回状态
     */
    @RequestMapping(value = "/revokeRes", method = RequestMethod.GET)
    public void revokeRes(@RequestParam String rid) {
        resourceService.revokeRes(rid);
    }

    /**
     * 发布资源
     *
     * @param publishForm 资源
     * @return 返回状态
     */
    @ApiOperation(value = "资源发布")
    @RequestMapping(value = "/publishRes", method = RequestMethod.POST)
    public void publishRes(HttpSession session, @RequestBody PublishDataSourceInfo publishForm) {

        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);
        ResourceConfig defaultPrv = new ResourceConfig();
        defaultPrv.setDownload(publishForm.getDownload());
        defaultPrv.setHistSnap(publishForm.getHistSnap());
        defaultPrv.setPlayBack(publishForm.getHistDays());
        defaultPrv.setRealControl(publishForm.getRealControl());
        defaultPrv.setRealPlay(publishForm.getRealDays());
        defaultPrv.setRealSnap(publishForm.getRealSnap());
        defaultPrv.setRecord(publishForm.getRecord());
        List<DataSource> dataSources = new ArrayList<>(publishForm.getEquipmentCatalogBeans().size());
        for (VEquipmentCatalogBean vEquipmentCatalogBean : publishForm.getEquipmentCatalogBeans()) {
            DataSource dataSource = new DataSource();
            MediaInfo mediaInfo = null;
            if (vEquipmentCatalogBean.getMediaDeviceId() != null) {
                mediaInfo = mediaInfoService.findByDeviceId(vEquipmentCatalogBean.getMediaDeviceId());
            }
            dataSource.setMediaId(mediaInfo);
            dataSource.setAddress(vEquipmentCatalogBean.getAddress());
            dataSource.setBlock(vEquipmentCatalogBean.getBlock());
            dataSource.setBusGroupId(vEquipmentCatalogBean.getBusGroupId());
            dataSource.setCivilCode(vEquipmentCatalogBean.getCivilCode());
            dataSource.setCreateTime(new Date(System.currentTimeMillis()));
            dataSource.setCreateUser(loginUserInfo.getSysUser().getId());
            dataSource.setDsCode(vEquipmentCatalogBean.getDeviceId());
            dataSource.setDsLevel(vEquipmentCatalogBean.getDsLevel());
            dataSource.setDsName(vEquipmentCatalogBean.getDsName());
            dataSource.setDsType(vEquipmentCatalogBean.getDsType() == null ? 4 : vEquipmentCatalogBean.getDsType());//手动新增为虚拟目录
            dataSource.setEquipType(vEquipmentCatalogBean.getEquipType());
            dataSource.setIpAddress(vEquipmentCatalogBean.getIpAddress());
            dataSource.setLat(vEquipmentCatalogBean.getLat());
            dataSource.setLng(vEquipmentCatalogBean.getLng());
            dataSource.setLocationType(vEquipmentCatalogBean.getLocationType());
            dataSource.setLoginPwd(vEquipmentCatalogBean.getLoginPwd());
            dataSource.setManuName(vEquipmentCatalogBean.getManuName());
            dataSource.setModel(vEquipmentCatalogBean.getModel());
            dataSource.setOwner(vEquipmentCatalogBean.getOwner());
            dataSource.setParental(vEquipmentCatalogBean.getParental());
            dataSource.setParentId(vEquipmentCatalogBean.getParent_id());//传过来的数据中 下划线Id是精确符合层级关系的
            dataSource.setRsId(publishForm.getRid());
            dataSource.setRegisterWay(vEquipmentCatalogBean.getRegisterWay());
            dataSource.setSecrecy(vEquipmentCatalogBean.getSecrecy());
            dataSource.setStandardParentId(vEquipmentCatalogBean.getParent_id());
            dataSource.setStatus(vEquipmentCatalogBean.getStatus());
            dataSources.add(dataSource);
        }

        resourceService.publishRes(publishForm.getRid(), dataSources, defaultPrv, loginUserInfo.getSysUser());
    }

    @ApiOperation(value = "查询资源和订阅状态")
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public DataTable<VResAndSubscribeStatus> findResAndSubscribeStatus(HttpSession session, String resName, String catalogId, String searchTime, String subStatus, Integer resMain, Integer resIndustry, Integer page, Integer size) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);

        SysUser sysUser = loginUserInfo.getSysUser();
        page = page == null ? 1 : page;
        size = size == null ? 10 : size;
        List<VResAndSubscribeStatus> vResAndSubscribeStatuses = resourceService.findResAndSubscribeStatus(resMain, resIndustry, sysUser.getComId().getId(), resName, catalogId, searchTime, subStatus,page,size);
        return DataTable.buildDataTable(vResAndSubscribeStatuses);

    }

    @ApiOperation(value = "重新发布")
    @RequestMapping(value = "republish", method = RequestMethod.GET)
    public void republish(String resId, HttpSession session) {
        resourceService.republish(resId);
    }


    @ApiOperation(value = "撤销提交")
    @RequestMapping(value = "unSubmit", method = RequestMethod.GET)
    public void unSubmit(String rid) {
        resourceService.unSubmit(rid);

    }
}




