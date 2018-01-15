package com.fable.mssg.service.login.exception;

import com.fable.framework.web.exception.RestApiException;
import com.fable.mssg.utils.login.LoginUtils;

/**
 * @author: yuhl Created on 16:46 2017/11/13 0013
 */
public class UserForbiddenException extends RestApiException {
    @Override
    public String getErrorCode() {
        return LoginUtils.USER_FORBIDDEN_ERROR;
    }
}
