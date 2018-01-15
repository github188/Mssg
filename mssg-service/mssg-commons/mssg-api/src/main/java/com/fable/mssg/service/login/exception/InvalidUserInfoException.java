package com.fable.mssg.service.login.exception;

import com.fable.framework.web.exception.RestApiException;
import com.fable.mssg.utils.login.LoginUtils;

/**
 * @author: yuhl Created on 15:46 2017/11/22 0022
 */
public class InvalidUserInfoException extends RestApiException {
    @Override
    public String getErrorCode() {
        return LoginUtils.NAME_PASSWORD_ERROR;
    }
}
