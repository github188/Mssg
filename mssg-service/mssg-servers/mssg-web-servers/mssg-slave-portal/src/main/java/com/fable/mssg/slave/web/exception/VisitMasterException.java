package com.fable.mssg.slave.web.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description
 * @Author wangmeng 2017/11/7
 */
public class VisitMasterException extends RestApiException {
    private String code;

    public VisitMasterException(String code){
        this.code = code;

    }

    @Override
    public String getErrorCode() {
        return code;
    }
}
