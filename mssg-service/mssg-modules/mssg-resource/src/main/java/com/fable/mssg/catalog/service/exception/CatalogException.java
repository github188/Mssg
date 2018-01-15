package com.fable.mssg.catalog.service.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description
 * @Author wangmeng 2017/11/8
 */
public class CatalogException extends RestApiException {

    public static final String CATALOG_NOT_FOUND = "3101";
    public static final String CATALOG_REMAIN_RESOURCE = "3102";
    public static final String CATALOG_NAME_ALREADY_EXIST = "3103";
    public static final String CATALOG_CODE_ALREADY_EXIST = "3104";


    private String errorCode;

    public CatalogException(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

}
