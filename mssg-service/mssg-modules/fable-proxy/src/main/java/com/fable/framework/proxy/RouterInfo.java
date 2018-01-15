package com.fable.framework.proxy;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/7
 * @since 1.3.0
 */
public interface RouterInfo<T> {

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    T getMessage();
}
