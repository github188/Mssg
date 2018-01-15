package com.fable.mssg.slave.web.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description  流媒体链接异常类
 * @Author xiejk 2017/11/20
 */
public class MediaInfoException extends RestApiException {

    public static final String MEDIAINFO_NOT_FOUND ="1001";  //没有被找到
    public static final String PWD_NOT_SAME ="1002";  //密码不一致
    public static final String MEDIA_INSERT_ERROR ="1003";  //添加异常
    public static final String MEDIA_CONNECT_ERROR ="1004";  //链接流媒体异常
    public static final String MEIDA_DEL_ERROR ="1005";  //删除异常
    public static final String DATASOURCE_EXIST ="1006";  //存在资源
    public static final String UPDATE_ERROR ="1007";  //修改异常



    String code;

    public MediaInfoException(String code) {
        this.code = code;
    }

    @Override
    public String getErrorCode() {
        return this.code;
    }

}
