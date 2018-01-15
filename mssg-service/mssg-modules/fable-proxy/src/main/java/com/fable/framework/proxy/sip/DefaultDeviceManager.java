package com.fable.framework.proxy.sip;

/**
 * 设备地址映射.
 *
 * @author stormning 2017/9/1
 * @since 1.3.0
 */
public class DefaultDeviceManager implements DeviceManager {

    @Override
    public DeviceInfo getDeviceInfo(String deviceId) {
        return new DeviceInfo(deviceId, null, null);
    }
}
