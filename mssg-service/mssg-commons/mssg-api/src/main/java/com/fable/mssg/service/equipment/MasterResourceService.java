package com.fable.mssg.service.equipment;

/**
 * @author: yuhl Created on 11:20 2017/12/8 0008
 */
public interface MasterResourceService {

    /**
     * 发送媒体源目录查询接口
     * @param deviceId 媒体源设备id
     * @param serverIp 媒体源ip
     * @param serverPort 媒体源端口
     * @param mediaName 媒体源名称
     */
    public void sendCatalogQuerySip(String deviceId, String serverIp, int serverPort, String mediaName);

    /**
     * 向slave上级发送注册信令
     * @param deviceId
     * @param serverIp
     * @param serverPort
     */
    public void sendRegiterSip(String deviceId, String serverIp, int serverPort);

}
