package com.fable.mssg.service.login.exception;

import com.fable.framework.web.exception.RestApiException;
import com.fable.mssg.utils.login.LoginUtils;

/**
 * @author: yuhl Created on 14:42 2017/11/20 0020
 */
public class InvalidAuthCodeException extends RestApiException {

    @Override
    public String getErrorCode() {
        return LoginUtils.AUTH_CODE_ERROR;
    }
}
