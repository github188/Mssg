package com.fable.mssg.slave.web;

import com.fable.framework.core.support.remoting.ServiceRegistry;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.exception.ResSubscribeException;
import com.fable.mssg.resource.service.exception.ResourceException;
import com.fable.mssg.service.resource.ResSubscribeService;
import com.fable.mssg.service.slave.FileLoadService;
import com.fable.mssg.utils.login.LoginUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 用于下载支持文件, 获取支持信息
 * @Author wangmeng 2017/9/5
 */
@Secured(LoginUtils.ROLE_USER)
@RestController
@RequestMapping("/support")
@Slf4j
public class SupportController {

    private static final int DEFAULT_BUFF_SIZE = 1024 * 1024;

    @Autowired
    private ServiceRegistry registry;

    @Value("${remote.proxy.server.ip}")
    private String remoteIp;

    @Value("${remote.proxy.server.port}")
    private Integer remotePort;

    private ResSubscribeService resSubscribeService;

    @Value("${support.file.path}")
    private String supportFilePath;

    @Value("${com.fable.mssg.slave.config.host}")
    private String slaveConfigHost;

    @Value("${com.fable.mssg.slave.config.port}")
    private String slaveConfigPort;

    @Value("${media.transfer.type}")
    private String transferType;

    private FileLoadService fileLoadService;


    @PostConstruct
    public void init() {
        try {
            resSubscribeService = registry.lookup(remoteIp, remotePort, true, ResSubscribeService.class);
            fileLoadService = registry.lookup(remoteIp, remotePort, true, FileLoadService.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public void getFile(String filename, HttpServletResponse res) {
        InputStream resInput = null;
        try {
            resInput = new FileInputStream(new File(
                    supportFilePath + filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (null != resInput) {
            OutputStream out = null;
            BufferedInputStream bis = null;
            try {
                res.setContentType("application/force-download");// 设置强制下载打开
                res.addHeader("Content-Disposition", "attachment;fileName=" + filename);// 设置文件名
                out = res.getOutputStream();
                bis = new BufferedInputStream(resInput, DEFAULT_BUFF_SIZE);
                byte[] data = new byte[DEFAULT_BUFF_SIZE];
                int length;
                while ((length = bis.read(data)) != -1) {
                    out.write(data, 0, length);
                }

            } catch (IOException e) {
                log.error("下载出错", e);
            } finally {
                try {
                    if (null != out) {
                        out.close();
                    }
                    if (null != bis) {
                        bis.close();
                    }
                } catch (IOException e) {
                    log.error("关闭流异常", e);
                }
            }

        }

    }

    @RequestMapping(value = "/equipment-config",method = RequestMethod.GET)
    public Map getEquipmentConfig(HttpServletRequest request) {
        Map<String, String> resMap = new HashMap<>();
        LoginUserInfo loginUserInfo = (LoginUserInfo) request.getSession().getAttribute(LoginUtils.CURRENT_USER_KEY);

        resMap.put("host", slaveConfigHost);
        resMap.put("port", slaveConfigPort);
        resMap.put("deviceId", loginUserInfo.getSysUser().getSipId());
        resMap.put("localIp", request.getRemoteAddr());
        resMap.put("transferType", transferType);
        return resMap;

    }

    @ApiOperation("上传订阅文件")
    @RequestMapping(value = "/subscribe/fileUpload", method = RequestMethod.POST)
    public String upload(MultipartFile multipartFile) {
        try {
            return resSubscribeService.uploadFile(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("上传订阅文件失败", e);
            throw new ResSubscribeException(ResSubscribeException.FILE_IO_EXCEPTION);
        }

    }

    @ApiOperation("slave下载资源图标")
    @RequestMapping(value = "/resource/downloadIcon",method = RequestMethod.GET)
    public void downloadResourceImage(String fileName, HttpServletResponse response) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);//取得后缀
        byte[] bytes = fileLoadService.download(fileName);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(byteArrayInputStream);
        } catch (IOException e) {
            throw new ResourceException(ResourceException.ICON_IO_EXCEPTION);
        }
        response.setContentType("image/" + suffix);
        try {
            ImageIO.write(bufferedImage, suffix, response.getOutputStream());
        } catch (IOException e) {
            throw new ResourceException(ResourceException.ICON_IO_EXCEPTION);
        }

    }

}
