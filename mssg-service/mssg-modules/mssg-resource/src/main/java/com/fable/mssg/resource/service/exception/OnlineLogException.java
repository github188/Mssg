package com.fable.mssg.resource.service.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description  现在日志异常类
 * @Author xiejk 2017/11/20
 */
public class OnlineLogException extends RestApiException {

    public static final String ONLINE_LOG_ERROR ="1301";  //下线异常
    public static final String ONLINE_NOT_FOUND="1302";  //日志不存在



    String code;

    public OnlineLogException(String code) {
        this.code = code;
    }

    @Override
    public String getErrorCode() {
        return this.code;
    }

}
