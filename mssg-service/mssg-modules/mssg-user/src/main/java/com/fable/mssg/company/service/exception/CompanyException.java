package com.fable.mssg.company.service.exception;

import com.fable.framework.web.exception.RestApiException;

/**
 * @Description
 * @Author wangmeng 2017/11/14
 */
public class CompanyException extends RestApiException {

    public static final String COMPANY_NOT_FOUND = "2101";
    public static final String INPUT_ERROR = "2104";
    public static final String COM_LEVEL_ALREADY_EXIST = "2105";
    public static final String FILE_NOT_CORRECT = "2103";
    public static final String FILE_IS_EMPTY = "2107";
    public static final String IO_EXCEPTION = "2108";
    public static final String COMPANY_REMAIN_USER = "2102";
    public static final String COMPANY_REMAIN_SUBSCRIBE = "2106";
    public static final String COMPANY_NAME_ALREADY_EXIST = "2109";
    public static final String COM_LEVEL_IN_USED = "2110";
    public static final String EQUIP_LEVEL_IS_NULL = "2111";

    String code;

    public CompanyException(String code) {
        this.code = code;
    }

    @Override
    public String getErrorCode() {
        return this.code;
    }

}
