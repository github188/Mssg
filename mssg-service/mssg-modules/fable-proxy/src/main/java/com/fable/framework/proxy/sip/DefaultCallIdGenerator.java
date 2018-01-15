package com.fable.framework.proxy.sip;

import java.util.UUID;

/**
 * .
 *
 * @author stormning 2017/8/31
 * @since 1.3.0
 */
public class DefaultCallIdGenerator implements CallIdGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
