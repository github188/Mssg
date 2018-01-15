package com.fable.mssg.service.mediainfo;

/**
 * @author: yuhl Created on 10:24 2017/10/27 0027
 */
public interface SourceCommonService {

    /**
     * 媒体源可用性检测
     * @param serverIp 媒体源ip
     * @param serverPort 媒体源端口
     * @return
     */
    public boolean mediaSourceConn(String serverIp, int serverPort);

    /**
     * 发送媒体源目录查询接口
     * @param deviceId 媒体源设备id
     * @param serverIp 媒体源ip
     * @param serverPort 媒体源端口
     * @param mediaName 媒体源名称
     */
    public void sendCatalogQuerySip(String deviceId, String serverIp, int serverPort, String mediaName);

    /**
     * 根据设备id删除媒体源目录
     * @param deviceId
     * @return
     */
    public int deleteOriginalDs(String deviceId);

}
