package com.fable.mssg.resource.web.mediainfo;

import com.fable.framework.core.config.Address;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.service.exception.MediaInfoException;
import com.fable.mssg.service.datasource.OriginaldsService;
import com.fable.mssg.service.equipment.MasterResourceService;
import com.fable.mssg.service.login.SessionService;
import com.fable.mssg.service.mediainfo.SourceCommonService;
import com.fable.mssg.domain.dsmanager.Originalds;
import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.fable.mssg.resource.converter.dsConvert.OriginaldsConverter;
import com.fable.mssg.resource.converter.mediaInfoConverter.MediaInfoConverter;
import com.fable.mssg.service.resource.MediaInfoService;
import com.fable.mssg.resource.vo.vDataSource.VOriginalds;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.mediainfo.VMediaInfo;
import com.fable.mssg.service.datasource.DataSourceService;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
 * description  流媒体平台控制器
 *
 * @author xiejk 2017/9/30
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/media")
@Slf4j
@Api(value = "流媒体源控制器", description = "流媒体源控制器")
public class MediaInfoController {

    //媒体流平台接口操作对象
    @Autowired
    private MediaInfoService mediaInfoService;

    //没体流平台转换类操作对象
    @Autowired
    private MediaInfoConverter mediaInfoConverter;

    //操作对象
    @Autowired
    private SourceCommonService sourceCommonService;

    @Autowired
    ServiceRegistry registry;

    @Value("${com.fable.mssg.proxy.sip.sipServer}")
    Address address;

    @Autowired
    MasterResourceService  masterResourceService;

    @Autowired
    DataSourceService dataSourceService;
    @Autowired
    OriginaldsConverter originaldsConverter;
    @Autowired
    SessionService sessionService;
    @Autowired
    OriginaldsService originaldsService;

    @PostConstruct
    public void init(){
        try {
            masterResourceService=registry.lookup(address.getHost(),address.getPort(),false,MasterResourceService.class);
        } catch (Exception e) {
            log.error("链接sip模块异常");
            e.printStackTrace();
        }
    }

    /**
     * 分页查询媒体流信息
     *
     * @param mediaName 媒体流平台名称
     * @param page      当前页数
     * @param size      每页显示个数
     * @return DataTable 分页对象
     */
    @ApiOperation(value = "分页查询媒体流信息", notes = "分页查询媒体流信息")
    @RequestMapping(value = "/findAllMediaInfoByCondition", method = RequestMethod.GET)
    public DataTable findAllMediaInfoByCondition(String mediaName, @RequestParam String page, @RequestParam String size) {
        Page<VMediaInfo> rlist = mediaInfoService.findAllPageMediaInfoByCondition(mediaName, Integer.valueOf(page) - 1, Integer.valueOf(size))
                .map(mediaInfoConverter);
        return DataTable.buildDataTable(rlist);
    }

    /**
     * 查看单个的媒体流信息
     *
     * @param mid 媒体流平台信息
     * @return MediaInfo
     */
    @ApiOperation(value = "查看单个的媒体流信息", notes = "查看单个的媒体流信息")
    @RequestMapping(value = "/findOneMediaInfo", method = RequestMethod.GET)
    public VMediaInfo findOneMediaInfo(@RequestParam String mid) {
        MediaInfo mediaInfo = mediaInfoService.findOneMediaInfo(mid);
        return mediaInfoConverter.convert(mediaInfo);
    }

    /**
     * 修改媒体流信息
     *
     * @param
     * @return 返回更新状态
     */

    @ApiOperation(value = "修改媒体流信息", notes = "修改媒体流信息")
    @RequestMapping(value = "/updateMediaInfo", method = RequestMethod.POST)
    public void updateMediaInfo(HttpServletRequest request, @RequestBody MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            throw new MediaInfoException(MediaInfoException.MEDIAINFO_NOT_FOUND);
        }
        MediaInfo meinfo = mediaInfoService.findOneMediaInfo(mediaInfo.getId());
        meinfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        LoginUserInfo loginUserInfo = (LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        meinfo.setUpdateUser(sysUser.getId()); //修改人
        meinfo.setMediaName(mediaInfo.getMediaName());
        meinfo.setIpAddress(mediaInfo.getIpAddress());
        meinfo.setAreaName(mediaInfo.getAreaName());
        meinfo.setDeviceId(mediaInfo.getDeviceId());
        meinfo.setSessionPort(mediaInfo.getSessionPort());
        meinfo.setHeartTime(mediaInfo.getHeartTime());
        meinfo.setPassword(mediaInfo.getPassword());
        meinfo.setAuth(mediaInfo.getAuth());
        meinfo.setRealm(mediaInfo.getRealm());
        meinfo.setSingalFormat(mediaInfo.getSingalFormat());
        meinfo.setMediaType(mediaInfo.getMediaType());
        meinfo.setRemark(mediaInfo.getRemark());
        meinfo.setMediaFormat(mediaInfo.getMediaFormat());//
        meinfo.setGbVersion(mediaInfo.getGbVersion());//更新版本
        if (sourceCommonService.mediaSourceConn(meinfo.getIpAddress(), meinfo.getSessionPort())) {
            if (mediaInfoService.updateMediaInfo(meinfo)) {
                //发送媒体源目录查询接口
                //sourceCommonService.sendCatalogQuerySip(deviceId,ipAddress,sessionPort.intValue(),mediaName);
            } else {
                throw new MediaInfoException(MediaInfoException.MEDIA_INSERT_ERROR);
            }
        } else {
            throw new MediaInfoException(MediaInfoException.MEDIA_CONNECT_ERROR);
        }
    }


    /**
     * 测试流媒体平台链接
     *
     * @param serverIp   平台ip
     * @param serverPort 平台端口
     * @return message
     */
    @ApiOperation(value = "测试流媒体平台链接", notes = "测试流媒体平台链接")
    @RequestMapping(value = "/testMediaInfoConn", method = RequestMethod.GET)
    public void testMediaInfoConn(@RequestParam String serverIp, @RequestParam int serverPort) {
        if (!sourceCommonService.mediaSourceConn(serverIp, serverPort)) {
            throw new MediaInfoException(MediaInfoException.MEDIA_CONNECT_ERROR);
        }
    }


    /**
     * 新增媒体流信息
     *
     * @param
     * @return 新增状态
     */
    @ApiOperation(value = "新增媒体流信息", notes = "新增媒体流信息")
    @RequestMapping(value = "/insertMedia", method = RequestMethod.POST)
    public void insertMedia(HttpServletRequest request, @RequestBody MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            throw new MediaInfoException(MediaInfoException.MEDIAINFO_NOT_FOUND);
        }
        mediaInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        LoginUserInfo loginUserInfo = (LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        SysUser sysUser = loginUserInfo.getSysUser();
        mediaInfo.setCreateUser(sysUser.getId()); //修改人
        mediaInfo.setForModule(1);//
        mediaInfo.setComId(sysUser.getComId().getId());//公司id
        //测试链接是不是通的方法
        if (sourceCommonService.mediaSourceConn(mediaInfo.getIpAddress(), mediaInfo.getSessionPort())) {
            if (mediaInfoService.insertMedia(mediaInfo)) {
                //发送媒体源目录查询接口
                masterResourceService.sendCatalogQuerySip(mediaInfo.getDeviceId(),mediaInfo.getIpAddress(),mediaInfo.getSessionPort().intValue(),mediaInfo.getMediaName());
            } else {
                throw new MediaInfoException(MediaInfoException.MEDIA_INSERT_ERROR);
            }
        } else {
            throw new MediaInfoException(MediaInfoException.MEDIA_CONNECT_ERROR);
        }
    }


    /**
     * 删除媒体流信息
     *
     * @param mid 媒体流平台id
     * @return 删除状态
     */
    @ApiOperation(value = "删除媒体流信息", notes = "删除媒体流信息")
    @RequestMapping(value = "/delMedia", method = RequestMethod.GET)
    public void delMedia(@RequestParam String mid) {
        MediaInfo mediaInfo = mediaInfoService.findOneMediaInfo(mid);
        if (mediaInfo != null) {
            List<DataSource> list = dataSourceService.findByMediaId(mediaInfo.getDeviceId());
            //存在数据源的情况，不能被删除
            if (list.size() != 0) {
                throw new MediaInfoException(MediaInfoException.DATASOURCE_EXIST);
            }
            if (!mediaInfoService.delMedia(mid)) {
                throw new MediaInfoException(MediaInfoException.MEIDA_DEL_ERROR);
            }
        } else {
            throw new MediaInfoException(MediaInfoException.MEDIAINFO_NOT_FOUND);
        }

    }

    /**
     * 查询其对应下的媒体元设备目录
     *
     * @param mid 媒体流平台id
     * @return 对应的设备目录
     */
    @ApiOperation(value = "媒体元设备目录", notes = "媒体元设备目录")
    @RequestMapping(value = "/findOdsByMediaId", method = RequestMethod.GET)
    public List<VOriginalds> findOdsByMediaId(@RequestParam String mid) {
        MediaInfo mediaInfo = mediaInfoService.findOneMediaInfo(mid);
        if (mediaInfo == null) {
            throw new MediaInfoException(MediaInfoException.MEDIAINFO_NOT_FOUND);
        }
        List<Originalds> list = originaldsService.findAllBymediadeviceid(mediaInfo.getDeviceId());
        return originaldsConverter.convert(list);
    }


    //查询所有媒体源 无分页
    @ApiOperation("查询所有媒体源 无分页")
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<VMediaInfo> findAll() {
        return mediaInfoConverter.convert(mediaInfoService.findAll());

    }

    //查询所有媒体源 无分页
    @ApiOperation("查询下级平台无分页")
    @RequestMapping(value = "/findLowLevel", method = RequestMethod.GET)
    public List<VMediaInfo> findLowLevel() {
        return mediaInfoConverter.convert(mediaInfoService.findLowLevel());

    }



    @ApiOperation("查询本单位的上级媒体平台")
    @RequestMapping(value = "/findMediaAndStatus", method = RequestMethod.GET)
    public List<VMediaInfo> findByComId(HttpSession session, String subscribeId) {
        LoginUserInfo loginUserInfo = (LoginUserInfo) session.getAttribute(LoginUtils.CURRENT_USER_KEY);

        return mediaInfoConverter.convert(mediaInfoService.findByCompany(subscribeId, loginUserInfo.getSysUser().getComId().getId()));
    }

}









