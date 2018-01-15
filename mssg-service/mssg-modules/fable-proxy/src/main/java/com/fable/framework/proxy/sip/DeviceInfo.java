package com.fable.framework.proxy.sip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/9/4
 * @since 1.3.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    private String deviceId;
    private InetSocketAddress address;
    private String sipId;
}
