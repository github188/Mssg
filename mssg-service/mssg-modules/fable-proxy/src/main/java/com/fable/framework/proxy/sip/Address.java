package com.fable.framework.proxy.sip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * 序列化反序列化需要.
 *
 * @author stormning 2017/11/20
 * @since 1.3.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address implements Serializable {

    private String host;

    private int port;

    public InetSocketAddress get() {
        return new InetSocketAddress(host, port);
    }
}
