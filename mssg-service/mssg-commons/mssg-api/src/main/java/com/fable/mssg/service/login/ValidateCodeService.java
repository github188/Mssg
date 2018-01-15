package com.fable.mssg.service.login;

/**
 * @author: yuhl Created on 14:34 2017/11/20 0020
 */
public interface ValidateCodeService {

    /**
     * 验证码校验
     * @param code
     * @return
     */
    boolean validateCode(String code, String sessionCode);

}
