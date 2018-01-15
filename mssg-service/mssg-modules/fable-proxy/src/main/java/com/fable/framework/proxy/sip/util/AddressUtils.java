package com.fable.framework.proxy.sip.util;

import com.fable.framework.core.config.Address;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/14
 * @since 1.3.0
 */
public class AddressUtils {
    private AddressUtils() {
    }

    public static InetSocketAddress nullSafeGet(Address address) {
        return address == null ? null : address.getInetSocketAddress();
    }
}
