package com.fable.mssg.resource.service.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description  审批异常类
 * @Author xiejk 2017/11/20
 */
public class ApprException extends RestApiException {

    public static final String RES_SUB_NOT_FOUND ="1201";  //订阅资源不存在
    public static final String APPR_NOT_FOUND ="1202";  //审批资源部存在
    public static final String APPR_ERROR ="1203";  //订阅审批异常
    public static final String DOWNLOAD_ERROR ="1204";  //下载文件异常
    public static final String REGISTER_NOT_FOUND ="1205";  //注册审批不存在
    public static final String APPR_REGISTER_ERROR ="1206";  //审批注册异常

    String code;
    public ApprException(String code) {

        this.code = code;
    }
    @Override
    public String getErrorCode() {
        return this.code;
    }


}
