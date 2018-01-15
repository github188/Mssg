package com.fable.mssg.login.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.service.login.ValidateCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author: yuhl Created on 14:35 2017/11/20 0020
 */
@Service
@Slf4j
@Exporter(interfaces = ValidateCodeService.class)
public class ValidateCodeServiceImpl implements ValidateCodeService {

    /**
     * 验证码校验
     *
     * @param code
     * @return
     */
    @Override
    public boolean validateCode(String code, String sessionCode) {
        log.info("session中的验证码 {}", sessionCode);
        return StringUtils.equalsIgnoreCase(code, sessionCode);
    }
}
