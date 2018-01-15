package com.fable.mssg.slave.web;


import com.fable.framework.core.config.Address;
import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.mediainfo.MediaInfo;
import com.fable.mssg.resource.converter.mediaInfoConverter.MediaInfoConverter;
import com.fable.mssg.service.equipment.MasterResourceService;
import com.fable.mssg.service.mediainfo.SourceCommonService;
import com.fable.mssg.service.resource.MediaInfoService;
import com.fable.mssg.slave.web.exception.MediaInfoException;
import com.fable.mssg.slave.web.exception.VisitMasterException;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.DataTable;
import com.fable.mssg.vo.mediainfo.VMediaInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/mediaInfoSlave")
@Api(value = "slave媒体流管理", description = "slave媒体流管理")
@Slf4j
public class MediaInfoSlaveController {

    @Autowired
    ServiceRegistry registry;
    @Value("${remote.proxy.server.ip}")
    private String remoteIp;

    @Value("${remote.proxy.server.port}")
    private int remotePort;

    @Value("${com.fable.mssg.proxy.sip.sipServer}")
    Address address;


    @Autowired
    MediaInfoConverter mediaInfoConverter;
    @Autowired
    MediaInfoService mediaInfoService;
    @Autowired
    SourceCommonService sourceCommonService;
    @Autowired
    MasterResourceService masterResourceService;
    @PostConstruct
    public void  init(){
        try {
            mediaInfoService=registry.lookup(remoteIp,remotePort,true,MediaInfoService.class);
            sourceCommonService=registry.lookup(remoteIp,remotePort,true,SourceCommonService.class);
            masterResourceService=registry.lookup(address.getHost(),address.getPort(),false,MasterResourceService.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("访问sip模块失败！");
        }

    }



    /**
     *分页查询媒体流信息
     * @param mediaName 媒体流平台名称
     * @param page 当前页数
     * @param size 每页显示个数
     * @return DataTable 分页对象
     */
    @ApiOperation(value ="分页查询媒体流信息" ,notes ="分页查询媒体流信息")
    @RequestMapping(value = "/findAllMediaInfoByCondition",method = RequestMethod.GET)
    public DataTable findAllMediaInfoByCondition(String mediaName, @RequestParam String  page, @RequestParam String size){
        Page<VMediaInfo> rlist =mediaInfoService.findAllPageMediaInfoByCondition(mediaName,1L,Integer.valueOf(page)-1,Integer.valueOf(size))
                .map(mediaInfoConverter);
        return  DataTable.buildDataTable(rlist);
    }

    /**
     * 查看单个的媒体流信息
     * @param mid   媒体流平台信息
     * @return  MediaInfo
     */
    @ApiOperation(value ="查看单个的媒体流信息" ,notes ="查看单个的媒体流信息")
    @RequestMapping(value = "/findOneMediaInfo",method = RequestMethod.GET)
    public VMediaInfo findOneMediaInfo(@RequestParam String mid){
        MediaInfo mediaInfo = mediaInfoService.findOneMediaInfo(mid);
        return mediaInfoConverter.convert(mediaInfo);
    }

    /**
     * 测试流媒体平台链接
     * @param serverIp  平台ip
     * @param serverPort 平台端口
     * @return message
     */
    @ApiOperation(value ="测试流媒体平台链接" ,notes ="测试流媒体平台链接")
    @RequestMapping(value = "/testMediaInfoConn",method =RequestMethod.GET )
    public void testMediaInfoConn(@RequestParam String serverIp,@RequestParam int serverPort){
        if(!sourceCommonService.mediaSourceConn(serverIp,serverPort)){
            throw  new MediaInfoException(MediaInfoException.MEDIA_CONNECT_ERROR);
        }
    }
    /**
     * 新增媒体流信息
     * @param
     * @return 新增状态
     */
    @ApiOperation(value ="新增媒体流信息" ,notes ="新增媒体流信息")
    @RequestMapping(value = "/insertMedia",method = RequestMethod.POST)
    public  void   insertMedia(HttpServletRequest request, @RequestBody MediaInfo mediaInfo){
        if(mediaInfo==null){
            throw  new MediaInfoException(MediaInfoException.MEDIAINFO_NOT_FOUND);
        }
        mediaInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        mediaInfo.setCreateUser(loginUserInfo.getSysUser().getId());//创建人
        mediaInfo.setForModule(2);//slave端
        mediaInfo.setComId(loginUserInfo.getSysUser().getComId().getId());//公司id
        //测试链接是不是通的方法
        if(sourceCommonService.mediaSourceConn(mediaInfo.getIpAddress(),mediaInfo.getSessionPort())){
            if(mediaInfoService.insertMedia(mediaInfo)){
                masterResourceService.sendRegiterSip(mediaInfo.getDeviceId(),mediaInfo.getIpAddress(),mediaInfo.getSessionPort().intValue());
            }else {
                throw  new MediaInfoException(MediaInfoException.MEDIA_INSERT_ERROR);
            }
        } else{
            throw  new MediaInfoException(MediaInfoException.MEDIA_CONNECT_ERROR);
        }
    }

    /**
     * 修改媒体流信息
     * @param
     * @return  返回更新状态
     */
    @ApiOperation(value ="修改媒体流信息" ,notes ="修改媒体流信息")
    @RequestMapping(value = "/updateMediaInfo",method = RequestMethod.POST)
    public  void updateMediaInfo(HttpServletRequest request,@RequestBody MediaInfo mediaInfo){

        if(mediaInfo==null){
            throw  new MediaInfoException(MediaInfoException.MEDIAINFO_NOT_FOUND);
        }
        MediaInfo meinfo = mediaInfoService.findOneMediaInfo(mediaInfo.getId());
        meinfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        LoginUserInfo loginUserInfo=(LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);
        meinfo.setUpdateUser(loginUserInfo.getSysUser().getId());//修改人
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
        meinfo.setMediaFormat(mediaInfo.getMediaFormat());
        meinfo.setGbVersion(mediaInfo.getGbVersion());//国际版本
        if(sourceCommonService.mediaSourceConn(meinfo.getIpAddress(),meinfo.getSessionPort())){
            if(mediaInfoService.updateMediaInfo(meinfo)){
                //发送媒体源目录查询接口ip跟端口不能修改
                //masterResourceService.sendCatalogQuerySip(meinfo.getDeviceId(),meinfo.getIpAddress(),meinfo.getSessionPort().intValue(),meinfo.getMediaName());
            }else {
                throw  new MediaInfoException(MediaInfoException.UPDATE_ERROR);
            }
        } else{
            throw  new MediaInfoException(MediaInfoException.MEDIA_CONNECT_ERROR);
        }


    }


    /**
     * 删除媒体流信息
     * @param mid  媒体流平台id
     * @return  删除状态
     */
    @ApiOperation(value ="删除媒体流信息" ,notes ="删除媒体流信息")
    @RequestMapping(value = "/delMedia",method = RequestMethod.GET)
    public  void  delMedia(@RequestParam String mid){

        MediaInfo mediaInfo=mediaInfoService.findOneMediaInfo(mid);
        if(mediaInfo!=null){
            if(!mediaInfoService.delMedia(mid)){
                throw  new MediaInfoException(MediaInfoException.MEIDA_DEL_ERROR);
            }
        }else{
            throw  new MediaInfoException(MediaInfoException.MEDIAINFO_NOT_FOUND);
        }


    }

}
