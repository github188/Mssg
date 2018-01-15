package com.fable.mssg.resource.service.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description
 * @Author wangmeng 2017/11/7
 */
public class ResourceException extends RestApiException {
    public static final String RESOURCE_NOT_FOUNT = "4001";
    public static final String ICON_IO_EXCEPTION = "4002";
    public static final String ICON_FORMAT_INCORRECT ="4003" ;
    public static final String RESOURCE_CODE_DUPLICATION = "4004";
    public static final String RESOURCE_NAME_DUPLICATION = "4005";
    public static final String CAN_NOT_MODIFY ="4006";
    public static final String CAN_NOT_DELETE ="4007";
    public static final String CAN_NOT_REPUBLISH ="4008";
    public static final String CAN_NOT_UN_SUBMIT = "4009";
    public static final String CAN_NOT_PUBLISH = "4010";

    private String errorCode;


    public ResourceException(String errorCode){
        this.errorCode=errorCode;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }
}
