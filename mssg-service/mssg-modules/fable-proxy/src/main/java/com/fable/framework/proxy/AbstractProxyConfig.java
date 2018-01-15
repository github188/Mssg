package com.fable.framework.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/11/29
 * @since 1.3.0
 */
@Data
@AllArgsConstructor
public abstract class AbstractProxyConfig {
    private InetSocketAddress clientAddress;
    private InetSocketAddress remoteAddress;
    private InetSocketAddress backServerAddress;
}
