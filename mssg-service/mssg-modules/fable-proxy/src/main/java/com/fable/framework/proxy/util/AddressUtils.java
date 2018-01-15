package com.fable.framework.proxy.util;

import com.google.common.collect.Maps;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * .
 *
 * @author stormning 2017/9/19
 * @since 1.3.0
 */
public class AddressUtils {
    private static final Map<Integer, InetSocketAddress> ADDRESSES = Maps.newConcurrentMap();

    public static InetSocketAddress from(String host, int port) {
        int key = hashCode(host, port);
        return ADDRESSES.computeIfAbsent(key, k -> new InetSocketAddress(host, port));
    }

    public static int hashCode(InetSocketAddress socketAddress) {
        return hashCode(socketAddress.getHostString(), socketAddress.getPort());
    }

    public static int hashCode(String host, int port) {
        return (host + ':' + port).hashCode();
    }
}