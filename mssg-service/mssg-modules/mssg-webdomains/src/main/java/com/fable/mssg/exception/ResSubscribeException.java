package com.fable.mssg.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description
 * @Author wangmeng 2017/11/13
 */
public class ResSubscribeException extends RestApiException {
    public static final String SUBSCRIBE_NOT_FOUND="7001";
    public static final String SUBSCRIBE_ALREADY_EXIST="7002";
    public static final String FILE_IO_EXCEPTION ="7003" ;
    public static final String CAN_NOT_CANCEL = "7004";
    public static final String SUBSCRIBE_ALREADY_SHARED = "7005";

    String code;

    public ResSubscribeException(String code) {
        this.code = code;
    }


    @Override
    public String getErrorCode() {
        return this.code;
    }

}
