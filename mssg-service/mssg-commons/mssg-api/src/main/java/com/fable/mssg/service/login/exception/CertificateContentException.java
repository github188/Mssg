package com.fable.mssg.service.login.exception;

import com.fable.framework.web.exception.RestApiException;
import com.fable.mssg.utils.login.LoginUtils;

/**
 * @author: yuhl Created on 17:01 2017/11/2 0002
 */
public class CertificateContentException extends RestApiException {

    @Override
    public String getErrorCode() {
        return LoginUtils.CERTIFICATE_CONTENT_ERROR;
    }
}
